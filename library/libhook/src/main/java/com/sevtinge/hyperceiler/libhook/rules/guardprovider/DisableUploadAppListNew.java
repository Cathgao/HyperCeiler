/*
  * This file is part of HyperCeiler.

  * HyperCeiler is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation, either version 3 of the
  * License.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.

  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.

  * Copyright (C) 2023-2026 HyperCeiler Contributions
*/
package com.sevtinge.hyperceiler.libhook.rules.guardprovider;

import com.sevtinge.hyperceiler.libhook.base.BaseHook;
import io.github.lingqiqi5211.ezhooktool.xposed.java.IReplaceHook;

import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;

import java.lang.reflect.Method;

import io.github.lingqiqi5211.ezhooktool.xposed.common.HookParam;

public class DisableUploadAppListNew extends BaseHook {
    private Method mDetectAppMethod;
    private Method mAntiDefraudAppManagerMethod;

    @Override
    protected boolean useDexKit() {
        return true;
    }

    @Override
    protected boolean initDexKit() {
        mDetectAppMethod = optionalMember("DetectAppMethod", bridge -> {
            var list = bridge.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                    .usingStrings("https://flash.sec.miui.com/detect/app")
                ));
            return (list != null && !list.isEmpty()) ? list.first() : null;
        });

        mAntiDefraudAppManagerMethod = optionalMember("AntiDefraudAppManager", bridge -> {
            var list = bridge.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                    .usingStrings("AntiDefraudAppManager", "getAllUnSystemAppsStatus error, ")
                ));
            return (list != null && !list.isEmpty()) ? list.first() : null;
        });

        return mDetectAppMethod != null || mAntiDefraudAppManagerMethod != null;
    }

    @Override
    public void init() {
        if (mAntiDefraudAppManagerMethod != null) {
            hookMethod(mAntiDefraudAppManagerMethod, new IReplaceHook() {
                @Override
                public Object replace(HookParam param) {
                    return null;
                }
            });
        }
        if (mDetectAppMethod != null) {
            hookMethod(mDetectAppMethod, new IReplaceHook() {
                @Override
                public Object replace(HookParam param) {
                    return null;
                }
            });
        }
    }
}
