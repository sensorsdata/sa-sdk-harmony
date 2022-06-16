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


import com.sensorsdata.analytics.harmony.sdk.common.exception.InvalidDataException;
import ohos.utils.zson.ZSONException;
import ohos.utils.zson.ZSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class SAZSONUtils {
    public static String formatJson(String jsonStr) {
        try {
            if (null == jsonStr || "".equals(jsonStr)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            char last = '\0';
            char current = '\0';
            int indent = 0;
            boolean isInQuotationMarks = false;
            for (int i = 0; i < jsonStr.length(); i++) {
                last = current;
                current = jsonStr.charAt(i);
                switch (current) {
                    case '"':
                        if (last != '\\') {
                            isInQuotationMarks = !isInQuotationMarks;
                        }
                        sb.append(current);
                        break;
                    case '{':
                    case '[':
                        sb.append(current);
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent++;
                            addIndentBlank(sb, indent);
                        }
                        break;
                    case '}':
                    case ']':
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent--;
                            addIndentBlank(sb, indent);
                        }
                        sb.append(current);
                        break;
                    case ',':
                        sb.append(current);
                        if (last != '\\' && !isInQuotationMarks) {
                            sb.append('\n');
                            addIndentBlank(sb, indent);
                        }
                        break;
                    case '\\':
                        break;
                    default:
                        sb.append(current);
                }
            }

            return sb.toString();
        } catch (Exception e) {
            SALog.printStackTrace(e);
            return "";
        }
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        try {
            for (int i = 0; i < indent; i++) {
                sb.append('\t');
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    public static void mergeZSONObject(final ZSONObject source, ZSONObject dest) {
        if (source == null) {
            return;
        }
        try {
            for (String key : source.keySet()) {
                Object value = source.get(key);
                if (value instanceof Date && !"$time".equals(key)) {
                    dest.put(key, SATimeUtils.formatDate((Date) value, Locale.CHINA));
                } else {
                    dest.put(key, value);
                }
            }
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
    }

    /**
     * clone 新的 JSONObject
     * @param zsonObject 原始自定义属性
     * @return clone 新的 JSONObject
     * @throws InvalidDataException 数据验证异常
     */
    public static ZSONObject cloneZsonObject(ZSONObject zsonObject) throws InvalidDataException {
        if (zsonObject == null) {
            return null;
        }
        ZSONObject cloneProperties;
        try {
            SADataHelper.assertPropertyTypes(zsonObject);
            cloneProperties = ZSONObject.stringToZSON(zsonObject.toString());
            for (Iterator<String> iterator = zsonObject.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                Object value = zsonObject.get(key);
                if (value instanceof Date) {
                    cloneProperties.put(key, new Date(((Date) value).getTime()));
                }
            }
        } catch (ZSONException e) {
            cloneProperties = zsonObject;
        }
        return cloneProperties;
    }
}
