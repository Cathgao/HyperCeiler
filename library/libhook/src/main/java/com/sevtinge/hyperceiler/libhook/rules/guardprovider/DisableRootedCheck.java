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

package com.sevtinge.hyperceiler.libhook.rules.guardprovider;

import com.sevtinge.hyperceiler.common.log.XposedLog;
import com.sevtinge.hyperceiler.libhook.base.BaseHook;
import io.github.lingqiqi5211.ezhooktool.xposed.java.IMethodHook;

import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.MethodMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import io.github.lingqiqi5211.ezhooktool.xposed.common.HookParam;

public class DisableRootedCheck extends BaseHook {
    private List<Method> mCheckRootMethods;
    private Class<?> mRootClass;

    @Override
    protected boolean useDexKit() {
        return true;
    }

    @Override
    protected boolean initDexKit() {
        mCheckRootMethods = optionalMemberList("CheckRoot", bridge -> bridge.findMethod(FindMethod.create()
            .matcher(MethodMatcher.create()
                .usingStrings("/system/bin/su")
                .returnType(boolean.class)
            )));
        mRootClass = optionalMember("RootClass", bridge -> {
            var list = bridge.findClass(FindClass.create()
                .matcher(ClassMatcher.create()
                    .usingStrings("Current device is rooted")
                ));
            return (list != null && !list.isEmpty()) ? list.first() : null;
        });
        return (mCheckRootMethods != null && !mCheckRootMethods.isEmpty()) || mRootClass != null;
    }

    @Override
    public void init() {
        if (mCheckRootMethods != null) {
            for (Method method : mCheckRootMethods) {
                XposedLog.d(TAG, getPackageName(), "Current hooking method is " + method);
                hookMethod(method, new IMethodHook() {
                    @Override
                    public void before(HookParam param) {
                        param.setResult(false);
                    }
                });
            }
        }
        if (mRootClass != null) {
            resetRootFields();
            try {
                hookAllMethods(android.app.Application.class, "onCreate", new IMethodHook() {
                    @Override
                    public void after(HookParam param) {
                        resetRootFields();
                    }
                });
            } catch (Throwable t) {
                XposedLog.w(TAG, getPackageName(), "Failed to hook Application.onCreate", t);
            }
        }
    }

    private void resetRootFields() {
        if (mRootClass == null) return;
        try {
            Class.forName(mRootClass.getName(), true, getClassLoader());
            for (Field field : mRootClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == boolean.class) {
                    setStaticBooleanField(mRootClass, field.getName(), false);
                    XposedLog.d(TAG, getPackageName(), "Reset static root boolean field: " + field.getName());
                }
            }
        } catch (Throwable t) {
            XposedLog.w(TAG, getPackageName(), "Failed to reset root fields on " + mRootClass, t);
        }
    }
}
