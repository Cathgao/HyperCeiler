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

import android.view.View
import com.sevtinge.hyperceiler.libhook.base.BaseHook
import io.github.lingqiqi5211.ezhooktool.xposed.common.HookParam
import io.github.lingqiqi5211.ezhooktool.xposed.dsl.createHook
import io.github.lingqiqi5211.ezhooktool.xposed.java.IMethodHook
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object DisableSafeModelTip : BaseHook() {
    override fun useDexKit() = true
    private var disableSafeModelTipMethod: Method? = null
    private var safeModeTipClass: Class<*>? = null

    override fun initDexKit(): Boolean {
        disableSafeModelTipMethod = optionalMember("DisableSafeModelTip") {
            it.findMethod {
                matcher {
                    usingEqStrings($$"android.provider.MiuiSettings$Ad")
                }
            }.firstOrNull()
        }
        safeModeTipClass = optionalMember("SafeModeTipClass") {
            it.findClass {
                matcher {
                    className = "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject"
                }
            }.firstOrNull()
        }
        return disableSafeModelTipMethod != null || safeModeTipClass != null
    }

    override fun init() {
        disableSafeModelTipMethod?.createHook {
            replace { false }
        }

        val targetClass = safeModeTipClass ?: findClassIfExists("com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject")
        if (targetClass != null) {
            hookAllConstructors(targetClass, object : IMethodHook {
                override fun after(param: HookParam) {
                    var cls: Class<*>? = param.thisObject.javaClass
                    while (cls != null && cls != Any::class.java) {
                        for (field in cls.declaredFields) {
                            if (!Modifier.isStatic(field.modifiers) && field.type == java.lang.Boolean.TYPE) {
                                field.isAccessible = true
                                field.setBoolean(param.thisObject, false)
                            }
                        }
                        cls = cls.superclass
                    }
                }
            })

            hookAllMethods(targetClass, "D", object : IMethodHook {
                override fun after(param: HookParam) {
                    val holder = param.args.firstOrNull() ?: return
                    val itemView = runCatching { BaseHook.getObjectField(holder, "itemView") as? View }.getOrNull() ?: return
                    itemView.visibility = View.GONE
                    itemView.layoutParams?.let { lp ->
                        lp.height = 0
                        itemView.layoutParams = lp
                    }
                }
            })
        }
    }
}
