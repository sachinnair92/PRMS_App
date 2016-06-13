package com.voodoo.GadgetBridgeFiles.devices.miband;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.content.LocalBroadcastManager;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.PRMS_MiBand.R;
import com.voodoo.GadgetBridgeFiles.activities.AbstractSettingsActivity;
import com.voodoo.GadgetBridgeFiles.activities.ControlCenter;

import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.ORIGIN_GENERIC;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.ORIGIN_INCOMING_CALL;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.ORIGIN_K9MAIL;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.ORIGIN_PEBBLEMSG;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.ORIGIN_SMS;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.PREF_MIBAND_ADDRESS;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.PREF_MIBAND_FITNESS_GOAL;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.PREF_MIBAND_RESERVE_ALARM_FOR_CALENDAR;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.PREF_MIBAND_USE_HR_FOR_SLEEP_DETECTION;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.PREF_USER_ALIAS;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.VIBRATION_COUNT;
import static com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst.getNotificationPrefKey;

public class MiBandPreferencesActivity extends AbstractSettingsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.miband_preferences);

        final Preference developmentMiaddr = findPreference(PREF_MIBAND_ADDRESS);
        developmentMiaddr.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                Intent refreshIntent = new Intent(ControlCenter.ACTION_REFRESH_DEVICELIST);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refreshIntent);
                preference.setSummary(newVal.toString());
                return true;
            }

        });

        final Preference enableHeartrateSleepSupport = findPreference(PREF_MIBAND_USE_HR_FOR_SLEEP_DETECTION);
        enableHeartrateSleepSupport.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                GBApplication.deviceService().onEnableHeartRateSleepSupport(Boolean.TRUE.equals(newVal));
                return true;
            }
        });
    }

    @Override
    protected String[] getPreferenceKeysWithSummary() {
        return new String[]{
                PREF_USER_ALIAS,
                PREF_MIBAND_ADDRESS,
                PREF_MIBAND_FITNESS_GOAL,
                PREF_MIBAND_RESERVE_ALARM_FOR_CALENDAR,
                getNotificationPrefKey(VIBRATION_COUNT, ORIGIN_SMS),
                getNotificationPrefKey(VIBRATION_COUNT, ORIGIN_INCOMING_CALL),
                getNotificationPrefKey(VIBRATION_COUNT, ORIGIN_K9MAIL),
                getNotificationPrefKey(VIBRATION_COUNT, ORIGIN_PEBBLEMSG),
                getNotificationPrefKey(VIBRATION_COUNT, ORIGIN_GENERIC),
        };
    }
}
