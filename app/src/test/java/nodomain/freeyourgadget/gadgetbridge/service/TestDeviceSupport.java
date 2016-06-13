package com.voodoo.GadgetBridgeFiles.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.UUID;

import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.model.Alarm;
import com.voodoo.GadgetBridgeFiles.model.CallSpec;
import com.voodoo.GadgetBridgeFiles.model.MusicSpec;
import com.voodoo.GadgetBridgeFiles.model.NotificationSpec;

public class TestDeviceSupport extends AbstractDeviceSupport {

    public TestDeviceSupport() {
    }

    @Override
    public void setContext(GBDevice gbDevice, BluetoothAdapter btAdapter, Context context) {
        super.setContext(gbDevice, btAdapter, context);
    }

    @Override
    public boolean connect() {
        gbDevice.setState(GBDevice.State.INITIALIZED);
        gbDevice.sendDeviceUpdateIntent(getContext());
        return true;
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean useAutoConnect() {
        return false;
    }

    @Override
    public void pair() {

    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {

    }

    @Override
    public void onSetTime() {

    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {

    }

    @Override
    public void onSetCallState(CallSpec callSpec) {

    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {

    }

    @Override
    public void onInstallApp(Uri uri) {

    }

    @Override
    public void onAppInfoReq() {

    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {

    }

    @Override
    public void onAppDelete(UUID uuid) {

    }

    @Override
    public void onAppConfiguration(UUID appUuid, String config) {

    }

    @Override
    public void onFetchActivityData() {

    }

    @Override
    public void onReboot() {

    }

    @Override
    public void onHeartRateTest() {

    }

    @Override
    public void onFindDevice(boolean start) {

    }

    @Override
    public void onScreenshotReq() {

    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {

    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {

    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {

    }
}
