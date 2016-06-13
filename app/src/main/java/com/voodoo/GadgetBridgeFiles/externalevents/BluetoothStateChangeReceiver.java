package com.voodoo.GadgetBridgeFiles.externalevents;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.activities.ControlCenter;
import com.voodoo.GadgetBridgeFiles.util.Prefs;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {

                Intent refreshIntent = new Intent(ControlCenter.ACTION_REFRESH_DEVICELIST);
                LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);

                Prefs prefs = GBApplication.getPrefs();
                if (!prefs.getBoolean("general_autoconnectonbluetooth", false)) {
                    return;
                }

                GBApplication.deviceService().connect();
            } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                GBApplication.deviceService().quit();

                Intent quitIntent = new Intent(GBApplication.ACTION_QUIT);

                LocalBroadcastManager.getInstance(context).sendBroadcast(quitIntent);
            }
        }
    }
}
