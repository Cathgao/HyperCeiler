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
package com.sevtinge.hyperceiler.libhook.rules.packageinstaller

import android.content.Context
import com.sevtinge.hyperceiler.libhook.base.BaseHook
import io.github.lingqiqi5211.ezhooktool.xposed.java.IMethodHook
import com.sevtinge.hyperceiler.libhook.utils.hookapi.dexkit.DexKit
import io.github.lingqiqi5211.ezhooktool.xposed.common.HookParam
import io.github.lingqiqi5211.ezhooktool.xposed.dsl.createHook
import java.lang.reflect.Method

object DisableCloudCheck : BaseHook() {

    private var fetchMethods: List<Method>? = null

    override fun useDexKit() = true

    override fun initDexKit(): Boolean {
        return try {
            method1
            method2
            fetchMethods = optionalMemberList("DisableCloudCheck_Fetch") {
                it.findMethod {
                    matcher {
                        paramCount = 6
                        usingStrings("antifraudLabel", "app_sec_check")
                        returnType = Any::class.java.name
                    }
                }
            }
            true
        } catch (t: Throwable) {
            false
        }
    }

    private val method1 by lazy<Method> {
        requiredMember("NoNetworkInstallBtn") {
            it.findMethod {
                matcher {
                    usingStrings("install_btn")
                    usingNumbers(16)
                }
            }.single()
        }
    }

    private val method2 by lazy<Method> {
        requiredMember("NormalInstallBtn") { bridge ->
            val declaringClassName = method1.declaringClass.name
            bridge.findMethod {
                matcher {
                    declaredClass(declaringClassName)
                    usingStrings("install_btn")
                }
            }.first { it.name != method1.name }
        }
    }

    override fun init() {
        hookMethod(method1, object : IMethodHook {
            override fun before(param: HookParam) {
                if (java.lang.reflect.Modifier.isStatic(method2.modifiers)) {
                    method2.isAccessible = true
                    method2.invoke(null, param.thisObject, param.args[0])
                } else {
                    callMethod(param.thisObject, method2.name, param.args[0])
                }
                param.result = null
            }
        })

        fetchMethods?.forEach { method ->
            try {
                method.createHook {
                    replace { param ->
                        try {
                            val cloudParamsClass = findClass("com.miui.packageInstaller.model.CloudParams")
                            val cloudParamsInstance = cloudParamsClass.newInstance()

                            val successClass = findClass("com.miui.packageInstaller.model.CloudResult\$Success")
                            val successConstructor = successClass.getDeclaredConstructor(cloudParamsClass)

                            successConstructor.newInstance(cloudParamsInstance)
                        } catch (e: Exception) {
                            BaseHook.invokeOriginalMethod(
                                param.executable as Method,
                                param.thisObject,
                                param.args
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
