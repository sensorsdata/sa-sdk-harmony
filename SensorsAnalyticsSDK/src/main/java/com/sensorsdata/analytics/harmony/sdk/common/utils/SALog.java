/*
 * Created by chenru on 2021/06/01.
 * Copyright 2015－2021 Sensors Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensorsdata.analytics.harmony.sdk.common.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.HashMap;

public class SALog {
    private static boolean enableLog = false;
    private static final int CHUNK_SIZE = 1000;
    private static final int SA_DOMAIN = 0x0001;
    private static final HashMap<String, HiLogLabel> sLabelMaps = new HashMap<>();

    public static void i(String tag, String msg, Object... format) {
        if (enableLog) {
            info(tag, msg, format);
        }
    }

    public static void e(String tag, String msg, Object... format) {
        if (enableLog) {
            HiLog.error(getLogLabel(tag), msg, format);
        }
    }


    /**
     * 此方法谨慎修改
     * 插件配置 disableLog 会修改此方法
     *
     * @param tag String
     * @param msg String
     */
    public static void info(String tag, String msg, Object... format) {
        try {
            if (msg != null) {
                byte[] bytes = msg.getBytes();
                int length = bytes.length;
                if (length <= CHUNK_SIZE) {
                    HiLog.info(getLogLabel(tag), msg);
                } else {
                    int index = 0, lastIndexOfLF = 0;
                    //当最后一次剩余值小于 CHUNK_SIZE 时，不需要再截断
                    while (index < length - CHUNK_SIZE) {
                        lastIndexOfLF = lastIndexOfLF(bytes, index);
                        int chunkLength = lastIndexOfLF - index;
                        HiLog.info(getLogLabel(tag), new String(bytes, index, chunkLength));
                        if (chunkLength < CHUNK_SIZE) {
                            //跳过换行符
                            index = lastIndexOfLF + 1;
                        } else {
                            index = lastIndexOfLF;
                        }
                    }
                    if (length > index) {
                        HiLog.info(getLogLabel(tag), new String(bytes, index, length - index), format);
                    }
                }
            } else {
                HiLog.info(getLogLabel(tag), null);
            }
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    /**
     * 获取从 fromIndex 开始，最靠近尾部的换行符
     *
     * @param bytes 日志转化的 bytes 数组
     * @param fromIndex 从 bytes 开始的下标
     * @return 换行符的下标
     */
    private static int lastIndexOfLF(byte[] bytes, int fromIndex) {
        int index = Math.min(fromIndex + CHUNK_SIZE, bytes.length - 1);
        for (int i = index; i > index - CHUNK_SIZE; i--) {
            //返回换行符的位置
            if (bytes[i] == (byte) 10) {
                return i;
            }
        }
        return index;
    }

    /**
     * 此方法谨慎修改
     * 插件配置 disableLog 会修改此方法
     *
     * @param e Exception
     */
    public static void printStackTrace(Exception e) {
        if (enableLog && e != null) {
            HiLog.error(getLogLabel("SA.Exception"), HiLog.getStackTrace(e));
        }
    }

    private static HiLogLabel getLogLabel(String tag) {
        HiLogLabel logLabel;
        if (sLabelMaps.containsKey(tag)) {
            logLabel = sLabelMaps.get(tag);
        } else {
            logLabel = new HiLogLabel(HiLog.LOG_APP, SA_DOMAIN, tag);
            sLabelMaps.put(tag, logLabel);
        }
        return logLabel;
    }

    /**
     * 设置是否打印 Log
     *
     * @param isEnableLog Log 状态
     */
    public static void setEnableLog(boolean isEnableLog) {
        enableLog = isEnableLog;
    }

    public static boolean isLogEnabled() {
        return enableLog;
    }
}
