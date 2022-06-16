/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensorsdata.analytics.harmony.sdk.core.database.provider.utils;

import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class UriMatcher {
    public static final int NO_MATCH = -1;

    /**
     * Creates the root node of the URI tree.
     *
     * @param code the code to match for the root URI
     */
    public UriMatcher(int code) {
        mCode = code;
        mWhich = -1;
        mChildren = new ArrayList<UriMatcher>();
        mText = null;
    }

    private UriMatcher(int which, String text) {
        mCode = NO_MATCH;
        mWhich = which;
        mChildren = new ArrayList<UriMatcher>();
        mText = text;
    }

    public void addURI(String authority, String path, int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code " + code + " is invalid: it must be positive");
        }

        String[] tokens = null;
        if (path != null) {
            String newPath = path;
            // Strip leading slash if present.
            if (path.length() > 1 && path.charAt(0) == '/') {
                newPath = path.substring(1);
            }
            tokens = newPath.split("/");
        }

        int numTokens = tokens != null ? tokens.length : 0;
        UriMatcher node = this;
        for (int i = -1; i < numTokens; i++) {
            String token = i < 0 ? authority : tokens[i];
            ArrayList<UriMatcher> children = node.mChildren;
            int numChildren = children.size();
            UriMatcher child;
            int j;
            for (j = 0; j < numChildren; j++) {
                child = children.get(j);
                if (token.equals(child.mText)) {
                    node = child;
                    break;
                }
            }
            if (j == numChildren) {
                // Child not found, create it
                child = createChild(token);
                node.mChildren.add(child);
                node = child;
            }
        }
        node.mCode = code;
    }

    private static UriMatcher createChild(String token) {
        switch (token) {
            case "#":
                return new UriMatcher(NUMBER, "#");
            case "*":
                return new UriMatcher(TEXT, "*");
            default:
                return new UriMatcher(EXACT, token);
        }
    }

    /**
     * Try to match against the path in a url.
     *
     * @param uri The url whose path we will match against.
     * @return The code for the matched node (added using addURI),
     * or -1 if there is no matched node.
     */
    public int match(Uri uri) {
        final List<String> pathSegments = uri.getDecodedPathList();
        final int li = pathSegments.size();

        UriMatcher node = this;

        if (li == 0 && uri.getDecodedAuthority() == null) {
            return this.mCode;
        }

        for (int i = -1; i < li; i++) {
            String u = i < 0 ? uri.getDecodedAuthority() : pathSegments.get(i);
            ArrayList<UriMatcher> list = node.mChildren;
            if (list == null) {
                break;
            }
            node = null;
            int lj = list.size();
            for (int j = 0; j < lj; j++) {
                UriMatcher n = list.get(j);
                which_switch:
                switch (n.mWhich) {
                    case EXACT:
                        if (n.mText.equals(u)) {
                            node = n;
                        }
                        break;
                    case NUMBER:
                        int lk = u.length();
                        for (int k = 0; k < lk; k++) {
                            char c = u.charAt(k);
                            if (c < '0' || c > '9') {
                                break which_switch;
                            }
                        }
                        node = n;
                        break;
                    case TEXT:
                        node = n;
                        break;
                }
                if (node != null) {
                    break;
                }
            }
            if (node == null) {
                return NO_MATCH;
            }
        }

        return node.mCode;
    }

    private static final int EXACT = 0;
    private static final int NUMBER = 1;
    private static final int TEXT = 2;

    private int mCode;
    private final int mWhich;
    //    @UnsupportedAppUsage
    private final String mText;
    //    @UnsupportedAppUsage
    private ArrayList<UriMatcher> mChildren;

    public static Uri convertUri(Uri uri) {
        if (uri.toString().contains("///")) {
            uri = Uri.parse(uri.toString().replaceFirst("///", "//"));
        }
        return uri;
    }

    /**
     * revert the uri
     * @param uri dataability://  expected
     * @return dataability:///
     */
    public static Uri revertUri(Uri uri) {
        if (uri.toString().contains("//")) {
            uri = Uri.parse(uri.toString().replaceFirst("//", "///"));
        }
        return uri;
    }
}
