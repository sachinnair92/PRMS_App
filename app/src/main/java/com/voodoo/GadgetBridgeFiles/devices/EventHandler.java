package com.voodoo.GadgetBridgeFiles.devices;

import android.net.Uri;

import java.util.ArrayList;
import java.util.UUID;

import com.voodoo.GadgetBridgeFiles.model.Alarm;
import com.voodoo.GadgetBridgeFiles.model.CallSpec;
import com.voodoo.GadgetBridgeFiles.model.MusicSpec;
import com.voodoo.GadgetBridgeFiles.model.NotificationSpec;

/**
 * Specifies all events that GadgetBridge intends to send to the gadget device.
 * Implementations can decide to ignore events that they do not support.
 * Implementations need to send/encode event to the connected device.
 */
public interface EventHandler {
    void onNotification(NotificationSpec notificationSpec);

    void onSetTime();

    void onSetAlarms(ArrayList<? extends Alarm> alarms);

    void onSetCallState(CallSpec callSpec);

    void onSetMusicInfo(MusicSpec musicSpec);

    void onEnableRealtimeSteps(boolean enable);

    void onInstallApp(Uri uri);

    void onAppInfoReq();

    void onAppStart(UUID uuid, boolean start);

    void onAppDelete(UUID uuid);

    void onAppConfiguration(UUID appUuid, String config);

    void onFetchActivityData();

    void onReboot();

    void onHeartRateTest();

    void onEnableRealtimeHeartRateMeasurement(boolean enable);

    void onFindDevice(boolean start);

    void onScreenshotReq();

    void onEnableHeartRateSleepSupport(boolean enable);
}