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

import com.sensorsdata.analytics.harmony.sdk.BuildConfig;
import ohos.aafwk.ability.RunningProcessInfo;
import ohos.abilityshell.HarmonyApplication;
import ohos.app.Context;
import ohos.bundle.ApplicationInfo;
import ohos.bundle.BundleInfo;
import ohos.bundle.IBundleManager;
import ohos.utils.zson.ZSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SAAppInfoUtils {

    public static final String LIB_NAME = "HarmonyOS";
    public static final String OS_NAME = "HarmonyOS";

    public static String sCurrentProcessName;
    private static ZSONObject mLibProperties = new ZSONObject();

    public static CharSequence getProcessName(Context context) {
        if (context == null) {
            return "";
        }
        return context.getApplicationInfo().getProcess();
    }

    public static String getAbilityLabel(Context context) {
        try {
//            if(SystemVersion.getApiVersion() == 7) {
//                return context.getString(context.getApplicationInfo().getLabelId());
//            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return "";
    }

    public static String getAppVersion(Context context) {
        try {
            IBundleManager bundleManager = context.getBundleManager();
            BundleInfo packageInfo = bundleManager.getBundleInfo(context.getBundleName(), 0);
            return packageInfo.getVersionName();
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return "";
    }

    /**
     * 获取鸿蒙系统 Version
     *
     * @return HarmonyOS Version
     */
    public static String getHarmonyOSVersion() {
        return getProp("hw_sc.build.platform.version", "");
    }

    /**
     * 获取制造商
     *
     * @return 制造商
     */
    public static String getManufacturer() {
        return getProp("ro.product.manufacturer", "");
    }

    private static String getProp(String property, String defaultValue) {
        try {
            Class spClz = Class.forName("android.os.SystemProperties");
            Method method = spClz.getDeclaredMethod("get", String.class);
            String value = (String) method.invoke(spClz, property);
            if (SATextUtils.isEmpty(value)) {
                return defaultValue;
            }
            return value.trim();
        } catch (Throwable throwable) {
            SALog.i("SA.SystemProperties", throwable.getMessage());
        }
        return defaultValue;
    }

    /**
     * 反射获取应用名称
     *
     * @param context AbilityContext
     * @return 应用名称
     */
    public static String getAppLabel(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        Class aClass = applicationInfo.getClass();
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            if (method.getName().contains("getLabelId")) {
                try {
                    Object invoke = method.invoke(applicationInfo);
                    return context.getString(Integer.parseInt(invoke.toString()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    SALog.printStackTrace(e);
                }
            }
        }
        return "";
    }

    public static Object getBrand() {
        return getProp("ro.product.brand", "");
    }

    /**
     * 判断是否为前台进程
     *
     * @param context Context
     * @return 是否前台进程
     */
    public static boolean isForegroundRunningProcess(Context context) {
        if (context == null) {
            return false;
        }
        try {
            List<RunningProcessInfo> runningProcessInfoList = context.getAbilityManager().getAllRunningProcesses();
            if (runningProcessInfoList != null && !runningProcessInfoList.isEmpty()) {
                String currentProcessName = getCurrentProcessName();
                String firstRunningProcessName = runningProcessInfoList.get(0).getProcessName();
                if (SATextUtils.equals(firstRunningProcessName, currentProcessName)) {
                    return true;
                }
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * 获得当前进程的名字
     *
     * @return 进程名称
     */
    public static String getCurrentProcessName() {
        try {
            if (SATextUtils.isEmpty(sCurrentProcessName)) {
                sCurrentProcessName = getCurrentProcessNameByCmd();
                if (SATextUtils.isEmpty(sCurrentProcessName)) {
                    sCurrentProcessName = getCurrentProcessNameByAT();
                }
            }
            return sCurrentProcessName;
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return null;
    }

    private static String getCurrentProcessNameByAT() {
        String processName = null;
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread", false, HarmonyApplication.class.getSuperclass().getClassLoader());
            Method declaredMethod = activityThread.getDeclaredMethod("currentProcessName", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            Object processInvoke = declaredMethod.invoke(null);
            if (processInvoke instanceof String) {
                processName = (String) processInvoke;
            }
        } catch (Throwable e) {
            //ignore
        }
        return processName;
    }

    private static String getCurrentProcessNameByCmd() {
        FileInputStream in = null;
        try {
            String fn = "/proc/self/cmdline";
            in = new FileInputStream(fn);
            byte[] buffer = new byte[256];
            int len = 0;
            int b;
            while ((b = in.read()) > 0 && len < buffer.length) {
                buffer[len++] = (byte) b;
            }
            if (len > 0) {
                return new String(buffer, 0, len, "UTF-8");
            }
        } catch (Throwable e) {
            // ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    SALog.printStackTrace(e);
                }
            }
        }
        return null;
    }

    public static ZSONObject getLibProperties(Context context) {
        if (mLibProperties.isEmpty()) {
            try {
                mLibProperties.put("$lib_version", BuildConfig.VERSION_NAME);
                mLibProperties.put("$lib", SAAppInfoUtils.LIB_NAME);
                StackTraceElement[] trace = (new Exception()).getStackTrace();
                String libDetail = null;
                if (trace.length > 1) {
                    StackTraceElement traceElement = trace[0];
                    libDetail = String.format("%s##%s##%s##%s", traceElement
                                    .getClassName(), traceElement.getMethodName(), traceElement.getFileName(),
                            traceElement.getLineNumber());
                }
                mLibProperties.put("$app_version", SAAppInfoUtils.getAppVersion(context));
                if (!SATextUtils.isEmpty(libDetail)) {
                    mLibProperties.put("$lib_detail", libDetail);
                }
                mLibProperties.put("$lib_method", "code");
            } catch (Exception ignored) {

            }
        }
        return mLibProperties;
    }
}
