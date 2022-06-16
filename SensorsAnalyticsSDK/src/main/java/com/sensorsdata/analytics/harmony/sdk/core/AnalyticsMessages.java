package com.sensorsdata.analytics.harmony.sdk.core;///*

import com.sensorsdata.analytics.harmony.sdk.common.exception.ConnectErrorException;
import com.sensorsdata.analytics.harmony.sdk.common.exception.InvalidDataException;
import com.sensorsdata.analytics.harmony.sdk.common.exception.ResponseErrorException;
import com.sensorsdata.analytics.harmony.sdk.common.utils.*;
import com.sensorsdata.analytics.harmony.sdk.core.database.SADataOperate;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import static com.sensorsdata.analytics.harmony.sdk.common.utils.Base64Coder.CHARSET_UTF8;

/**
 * Manage communication of events with the internal database and the SensorsData servers.
 * This class straddles the thread boundary between user threads and
 * a logical SensorsData thread.
 */
public class AnalyticsMessages {
    private static final String TAG = "SA.AnalyticsMessages";
    private static final int FLUSH_QUEUE = 3;
    private static final int DELETE_ALL = 4;
    private static AnalyticsMessages S_INSTANCES;
    private final Worker mWorker;
    private final Context mContext;
    private final SADataOperate mOperate;

    /**
     * 不要直接调用，通过 getInstance 方法获取实例
     */
    public AnalyticsMessages(final Context context) {
        mContext = context;
        mOperate = SADataOperate.getInstance();
        mWorker = new Worker();
        S_INSTANCES = this;
    }

    /**
     * 获取 AnalyticsMessages 对象
     */
    public static AnalyticsMessages getInstance() {
        synchronized (S_INSTANCES) {
            if (S_INSTANCES == null) {
                throw new NullPointerException("AnalyticsMessage is not init!");
            }
            return S_INSTANCES;
        }
    }

    private static byte[] slurp(final InputStream inputStream)
            throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[8192];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    void enqueueEventMessage(final SAEventType type, final ZSONObject eventJson) {
        try {
            synchronized (mOperate) {
                int ret = mOperate.insertEvent(eventJson);
                if (ret < 0) {
                    String error = "Failed to enqueue the event: " + eventJson;
                    SALog.i(TAG, error);
                }
                InnerEvent event = InnerEvent.get();
                event.eventId = FLUSH_QUEUE;

                if (ret == SADataContract.SQLite.DB_OUT_OF_MEMORY_ERROR) {
                    mWorker.runEvent(event);
                } else {
                    // SAEventType.TRACK_SIGNUP 立即发送
                    if (type == SAEventType.TRACK_SIGNUP ||
                            ret > SensorsAnalyticsManager.getInstance().getConfigOptions().mFlushBulkSize) {
                        mWorker.runEvent(event);
                    } else {
                        mWorker.runEventOnce(event, SensorsAnalyticsManager.getInstance().getConfigOptions().mFlushInterval);
                    }
                }
            }
        } catch (Exception e) {
            SALog.i(TAG, "enqueueEventMessage error:" + e);
        }
    }

    void flush() {
        try {
            InnerEvent event = InnerEvent.get();
            event.eventId = FLUSH_QUEUE;

            mWorker.runEvent(event);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    void deleteAll() {
        try {
            InnerEvent event = InnerEvent.get();
            event.eventId = DELETE_ALL;
            mWorker.runEvent(event);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    private void sendData() {
        try {
            if (!SAAppInfoUtils.isForegroundRunningProcess(mContext)) {
                return;
            }

            if (!SensorsAnalyticsManager.getInstance().isNetworkRequest()) {
                SALog.i(TAG, "NetworkRequest 已关闭，不发送数据！");
                return;
            }

            if (SATextUtils.isEmpty(SensorsAnalyticsManager.getInstance().getConfigOptions().mServerUrl)) {
                SALog.i(TAG, "Server url is null or empty.");
                return;
            }

            //无网络
            if (!SANetworkUtils.isNetworkAvailable(mContext)) {
                return;
            }

            String networkType = SANetworkUtils.networkType(mContext);
            //不符合同步数据的网络策略
            if (!SANetworkUtils.isShouldFlush(networkType, SensorsAnalyticsManager.getInstance().getConfigOptions().mNetworkTypePolicy)) {
                SALog.i(TAG, String.format("您当前网络为 %s，无法发送数据，请确认您的网络发送策略！", networkType));
                return;
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
            return;
        }
        int count = 100;
        while (count > 0) {
            boolean deleteEvents = true;
            String[] eventsData;
            synchronized (mOperate) {
                eventsData = mOperate.queryEvent(50);
            }
            final String lastId = eventsData[0];
            final String rawMessage = eventsData[1];
            final String gzip = eventsData[2];
            String errorMessage = null;

            try {
                String data = rawMessage;
                if (SADataContract.SQLite.GZIP_DATA_EVENT.equals(gzip)) {
                    data = encodeData(rawMessage);
                }
                if (!SATextUtils.isEmpty(data)) {
                    sendHttpRequest(SensorsAnalyticsManager.getInstance().getConfigOptions().mServerUrl, data, gzip, rawMessage, false);
                }
            } catch (ConnectErrorException e) {
                deleteEvents = false;
                errorMessage = "Connection error: " + e.getMessage();
            } catch (InvalidDataException e) {
                errorMessage = "Invalid data: " + e.getMessage();
            } catch (ResponseErrorException e) {
                deleteEvents = isDeleteEventsByCode(e.getHttpCode());
                errorMessage = "ResponseErrorException: " + e.getMessage();
            } catch (Exception e) {
                deleteEvents = false;
                errorMessage = "Exception: " + e.getMessage();
            } finally {
                if (!SATextUtils.isEmpty(errorMessage)) {
                    SALog.i(TAG, errorMessage);
                }
                if (deleteEvents) {
                    count = mOperate.cleanupEvents(Integer.valueOf(lastId));
                    SALog.i(TAG, String.format(Locale.CHINA, "Events flushed. [left = %d]", count));
                } else {
                    count = 0;
                }
            }
        }
    }

    public void sendHttpRequest(String path, String data, String gzip, String rawMessage, boolean isRedirects) throws ConnectErrorException, ResponseErrorException, MalformedURLException {
        HttpURLConnection connection = null;
        InputStream in = null;
        OutputStream out = null;
        BufferedOutputStream bout = null;
        try {
            final URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            if (connection == null) {
                SALog.i(TAG, String.format("can not connect %s, it shouldn't happen", url.toString()));
                return;
            }
            SAConfigOptions configOptions = SensorsAnalyticsManager.getInstance().getConfigOptions();
            if (configOptions != null && configOptions.mSSLSocketFactory != null
                    && connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(configOptions.mSSLSocketFactory);
            }
            connection.setInstanceFollowRedirects(false);

            Uri.Builder builder = new Uri.Builder();
            //先校验crc
            if (!SATextUtils.isEmpty(data)) {
                builder.appendDecodedQueryParam("crc", String.valueOf(data.hashCode()));
            }

            builder.appendDecodedQueryParam("gzip", gzip);
            builder.appendDecodedQueryParam("data_list", data);

            String query = builder.build().getEncodedQuery();
            if (SATextUtils.isEmpty(query)) {
                return;
            }

            connection.setFixedLengthStreamingMode(query.getBytes(CHARSET_UTF8).length);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            //设置连接超时时间
            connection.setConnectTimeout(30 * 1000);
            //设置读取超时时间
            connection.setReadTimeout(30 * 1000);
            out = connection.getOutputStream();
            bout = new BufferedOutputStream(out);
            bout.write(query.getBytes(CHARSET_UTF8));
            bout.flush();

            int responseCode = connection.getResponseCode();
            SALog.i(TAG, "responseCode: " + responseCode);
            if (!isRedirects && SANetworkUtils.needRedirects(responseCode)) {
                String location = SANetworkUtils.getLocation(connection, path);
                if (!SATextUtils.isEmpty(location)) {
                    closeStream(bout, out, null, connection);
                    sendHttpRequest(location, data, gzip, rawMessage, true);
                    return;
                }
            }
            try {
                in = connection.getInputStream();
            } catch (FileNotFoundException e) {
                in = connection.getErrorStream();
            }
            byte[] responseBody = slurp(in);
            in.close();
            in = null;

            String response = new String(responseBody, CHARSET_UTF8);
            if (SALog.isLogEnabled()) {
                String jsonMessage = SAZSONUtils.formatJson(rawMessage);
                // 状态码 200 - 300 间都认为正确
                if (responseCode >= HttpURLConnection.HTTP_OK &&
                        responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                    SALog.i(TAG, "valid message: \n" + jsonMessage);
                } else {
                    SALog.i(TAG, "invalid message: \n" + jsonMessage);
                    SALog.i(TAG, String.format(Locale.CHINA, "ret_code: %d", responseCode));
                    SALog.i(TAG, String.format(Locale.CHINA, "ret_content: %s", response));
                }
            }
            if (responseCode < HttpURLConnection.HTTP_OK || responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                // 校验错误
                throw new ResponseErrorException(String.format("flush failure with response '%s', the response code is '%d'",
                        response, responseCode), responseCode);
            }
        } catch (MalformedURLException e) {
            throw new MalformedURLException();
        } catch (IOException e) {
            throw new ConnectErrorException(e);
        } finally {
            closeStream(bout, out, in, connection);
        }
    }

    /**
     * 在服务器正常返回状态码的情况下，目前只有 (>= 500 && < 600) || 404 || 403 才不删数据
     *
     * @param httpCode 状态码
     * @return true: 删除数据，false: 不删数据
     */
    private boolean isDeleteEventsByCode(int httpCode) {
        boolean shouldDelete = true;
        if (httpCode == HttpURLConnection.HTTP_NOT_FOUND ||
                httpCode == HttpURLConnection.HTTP_FORBIDDEN ||
                (httpCode >= HttpURLConnection.HTTP_INTERNAL_ERROR && httpCode < 600)) {
            shouldDelete = false;
        }
        return shouldDelete;
    }

    private void closeStream(BufferedOutputStream bout, OutputStream out, InputStream in, HttpURLConnection connection) {
        if (null != bout) {
            try {
                bout.close();
            } catch (Exception e) {
                SALog.i(TAG, e.getMessage());
            }
        }

        if (null != out) {
            try {
                out.close();
            } catch (Exception e) {
                SALog.i(TAG, e.getMessage());
            }
        }

        if (null != in) {
            try {
                in.close();
            } catch (Exception e) {
                SALog.i(TAG, e.getMessage());
            }
        }

        if (null != connection) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                SALog.i(TAG, e.getMessage());
            }
        }
    }

    private String encodeData(final String rawMessage) throws InvalidDataException {
        GZIPOutputStream gos = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(rawMessage.getBytes(CHARSET_UTF8).length);
            gos = new GZIPOutputStream(os);
            gos.write(rawMessage.getBytes(CHARSET_UTF8));
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();
            return new String(Base64Coder.encode(compressed));
        } catch (IOException exception) {
            // 格式错误，直接将数据删除
            throw new InvalidDataException(exception);
        } finally {
            if (gos != null) {
                try {
                    gos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    // Worker will manage the (at most single) IO thread associated with
    // this AnalyticsMessages instance.
    // XXX: Worker class is unnecessary, should be just a subclass of HandlerThread
    class Worker {

        private final Object mHandlerLock = new Object();
        private final EventHandler mHandler;

        Worker() {
            EventRunner runner = EventRunner.create();
            mHandler = new AnalyticsMessageHandler(runner);
        }

        void runEvent(InnerEvent event) {
            synchronized (mHandlerLock) {
                // We died under suspicious circumstances. Don't try to send any more events.
                if (mHandler == null) {
                    SALog.i(TAG, "Dead worker dropping a message: " + event.eventId);
                } else {
                    mHandler.sendEvent(event);
                }
            }
        }

        void runEventOnce(InnerEvent event, long delay) {
            synchronized (mHandlerLock) {
                // We died under suspicious circumstances. Don't try to send any more events.
                if (mHandler == null) {
                    SALog.i(TAG, "Dead worker dropping a message: " + event.eventId);
                } else {
                    if (!mHandler.hasInnerEvent(event.eventId)) {
                        mHandler.sendEvent(event, delay);
                    }
                }
            }
        }

        private class AnalyticsMessageHandler extends EventHandler {

            AnalyticsMessageHandler(EventRunner runner) {
                super(runner);
            }

            @Override
            protected void processEvent(InnerEvent event) {
                if (event.eventId == FLUSH_QUEUE) {
                    try {
                        sendData();
                    } catch (Exception e) {
                        SALog.i(TAG, e.getMessage());
                    }
                } else if (event.eventId == DELETE_ALL) {
                    try {
                        mOperate.deleteAllEvent();
                    } catch (Exception e) {
                        SALog.printStackTrace(e);
                    }
                } else {
                    SALog.i(TAG, "Unexpected message received by SensorsData worker: " + event.eventId);
                }
            }
        }
    }
}