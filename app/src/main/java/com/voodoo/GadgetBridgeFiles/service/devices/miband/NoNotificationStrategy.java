package com.voodoo.GadgetBridgeFiles.service.devices.miband;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voodoo.GadgetBridgeFiles.devices.miband.VibrationProfile;
import com.voodoo.GadgetBridgeFiles.service.btle.BtLEAction;
import com.voodoo.GadgetBridgeFiles.service.btle.TransactionBuilder;

/**
 * Does not do anything.
 */
public class NoNotificationStrategy implements NotificationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NoNotificationStrategy.class);

    @Override
    public void sendDefaultNotification(TransactionBuilder builder, BtLEAction extraAction) {
        LOG.info("dummy notification stragegy: default notification");
    }

    @Override
    public void sendCustomNotification(VibrationProfile vibrationProfile, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        LOG.info("dummy notification stragegy: custom notification");
    }
}
