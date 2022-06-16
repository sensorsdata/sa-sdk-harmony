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

package com.sensorsdata.analytics.harmony.sdk.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class HttpTaskManager {
    /**
     * 创建一个可重用固定线程数的线程池
     */
    private static final int DEFAULT_POOL_SIZE = 2;
    private static final String DEFAULT_THREAD_NAME = "SENSORS_ANALYTICS_THREAD";

    /**
     * 创建一个可重用固定线程数的线程池
     */
    private volatile static Map<String, ExecutorService> executors = new HashMap<>();

    private HttpTaskManager() {
    }

    public static ExecutorService getExecutor() {
        return getExecutor(DEFAULT_THREAD_NAME);
    }

    public static ExecutorService getExecutor(String threadName) {
        return getExecutor(threadName, DEFAULT_POOL_SIZE);
    }

    public synchronized static ExecutorService getExecutor(String threadName, int poolSize) {
        if (executors.containsKey(threadName)) {
            return executors.get(threadName);
        } else {
            ExecutorService executor = new ThreadPoolExecutor(poolSize, poolSize,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new ThreadFactoryWithName(threadName));
            executors.put(threadName, executor);
            return executor;
        }
    }

    static class ThreadFactoryWithName implements ThreadFactory {

        private final String name;

        ThreadFactoryWithName(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, name);
        }
    }
}
