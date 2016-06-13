package com.voodoo.GadgetBridgeFiles.devices.pebble;

import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;

public class PebbleGadgetBridgeSampleProvider extends MorpheuzSampleProvider {
    public PebbleGadgetBridgeSampleProvider() {
        movementDivisor = 63.0f;
    }

    @Override
    public int getID() {
        return SampleProvider.PROVIDER_PEBBLE_GADGETBRIDGE;
    }
}
