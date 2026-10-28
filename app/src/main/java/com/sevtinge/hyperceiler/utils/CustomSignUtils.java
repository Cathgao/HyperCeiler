/*
 * This file is part of HyperCeiler.
 *
 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2023-2026 HyperCeiler Contributions
 */

package com.sevtinge.hyperceiler.utils;

import android.content.Context;

import com.sevtinge.hyperceiler.expansion.utils.SignUtils;

public class CustomSignUtils {

    public static final String CUSTOM_SHA256 = "45:81:17:79:63:6C:CE:57:44:28:90:D3:D2:A0:D0:8B:62:E3:42:7F:DA:69:A8:D9:98:4E:78:DA:60:10:A3:53";

    /**
     * 校验签名。匹配自定义证书、官方证书，或直接放行（取消强校验）。
     */
    public static boolean isSignCheckPass(Context context) {
        try {
            String currentSign = getSHA256Signature(context);
            if (CUSTOM_SHA256.equalsIgnoreCase(currentSign)) {
                return true;
            }
            if (SignUtils.isSignCheckPass(context)) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    public static String getSHA256Signature(Context context) {
        try {
            return SignUtils.getSHA256Signature(context);
        } catch (Throwable t) {
            return "";
        }
    }
}

