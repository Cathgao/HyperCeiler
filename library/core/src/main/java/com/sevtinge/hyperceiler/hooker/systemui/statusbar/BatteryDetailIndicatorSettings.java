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
package com.sevtinge.hyperceiler.hooker.systemui.statusbar;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import com.sevtinge.hyperceiler.common.utils.PrefsBridge;
import com.sevtinge.hyperceiler.core.R;
import com.sevtinge.hyperceiler.dashboard.DashboardFragment;

import fan.preference.DropDownPreference;

public class BatteryDetailIndicatorSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {

    private DropDownPreference mTempSource;
    private EditTextPreference mCustomTempPath;

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.system_ui_status_bar_battery_detail;
    }

    @Override
    public void initPrefs() {
        mTempSource = findPreference("prefs_key_system_ui_statusbar_battery_detail_temp_source");
        mCustomTempPath = findPreference("prefs_key_system_ui_statusbar_battery_detail_custom_temp_path");

        if (mTempSource != null && mCustomTempPath != null) {
            mTempSource.setOnPreferenceChangeListener(this);
            int tempSourceVal = PrefsBridge.getStringAsInt("prefs_key_system_ui_statusbar_battery_detail_temp_source", 0);
            mCustomTempPath.setVisible(tempSourceVal == 4);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTempSource) {
            try {
                int val = Integer.parseInt((String) newValue);
                if (mCustomTempPath != null) {
                    mCustomTempPath.setVisible(val == 4);
                }
            } catch (Exception ignored) {
            }
            return true;
        }
        return false;
    }
}
