package com.voodoo.GadgetBridgeFiles.service.devices.miband;

import android.support.annotation.NonNull;

import com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;

public abstract class AbstractMi1SFirmwareInfo extends AbstractMiFirmwareInfo {

    public AbstractMi1SFirmwareInfo(@NonNull byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    @Override
    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return MiBandConst.MI_1S.equals(device.getHardwareVersion());
    }

    @Override
    public boolean isSingleMiBandFirmware() {
        return false;
    }
}
