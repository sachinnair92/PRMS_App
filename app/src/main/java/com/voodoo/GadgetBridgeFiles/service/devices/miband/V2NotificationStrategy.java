package com.voodoo.GadgetBridgeFiles.service.devices.miband;

import android.bluetooth.BluetoothGattCharacteristic;

import com.voodoo.GadgetBridgeFiles.devices.miband.VibrationProfile;
import com.voodoo.GadgetBridgeFiles.service.btle.BtLEAction;
import com.voodoo.GadgetBridgeFiles.service.btle.GattCharacteristic;
import com.voodoo.GadgetBridgeFiles.service.btle.TransactionBuilder;

public class V2NotificationStrategy implements NotificationStrategy {
    private final MiBandSupport support;

    public V2NotificationStrategy(MiBandSupport support) {
        this.support = support;
    }

    @Override
    public void sendDefaultNotification(TransactionBuilder builder, BtLEAction extraAction) {
        VibrationProfile profile = VibrationProfile.getProfile(VibrationProfile.ID_MEDIUM, (short) 3);
        sendCustomNotification(profile, extraAction, builder);
    }

    private void sendCustomNotification(VibrationProfile vibrationProfile, BtLEAction extraAction, TransactionBuilder builder) {
        //use the new alert characteristic
        BluetoothGattCharacteristic alert = support.getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
        for (short i = 0; i < vibrationProfile.getRepeat(); i++) {
            int[] onOffSequence = vibrationProfile.getOnOffSequence();
            for (int j = 0; j < onOffSequence.length; j++) {
                int on = onOffSequence[j];
                on = Math.min(500, on); // longer than 500ms is not possible
                builder.write(alert, new byte[]{GattCharacteristic.MILD_ALERT}); //MILD_ALERT lights up GREEN leds, HIGH_ALERT lights up RED leds
                builder.wait(on);
                builder.write(alert, new byte[]{GattCharacteristic.NO_ALERT});

                if (++j < onOffSequence.length) {
                    int off = Math.max(onOffSequence[j], 25); // wait at least 25ms
                    builder.wait(off);
                }

                if (extraAction != null) {
                    builder.add(extraAction);
                }
            }
        }
    }

    @Override
    public void sendCustomNotification(VibrationProfile vibrationProfile, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        // all other parameters are unfortunately not supported anymore ;-(
        sendCustomNotification(vibrationProfile, extraAction, builder);
    }
}
