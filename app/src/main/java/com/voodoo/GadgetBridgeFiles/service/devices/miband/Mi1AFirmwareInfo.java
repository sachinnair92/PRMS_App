package com.voodoo.GadgetBridgeFiles.service.devices.miband;

import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voodoo.GadgetBridgeFiles.devices.miband.MiBandConst;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;

public class Mi1AFirmwareInfo extends AbstractMi1FirmwareInfo {
    private static final Logger LOG = LoggerFactory.getLogger(Mi1AFirmwareInfo.class);

    public static Mi1AFirmwareInfo getInstance(byte[] wholeFirmwareBytes) {
        Mi1AFirmwareInfo info = new Mi1AFirmwareInfo(wholeFirmwareBytes);
        if (info.isGenerallySupportedFirmware()) {
            return info;
        }
        LOG.info("firmware not supported");
        return null;
    }

    protected Mi1AFirmwareInfo(@NonNull byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    @Override
    protected int getSupportedMajorVersion() {
        return 5;
    }

    @Override
    public boolean isGenerallyCompatibleWith(GBDevice device) {
        String hwVersion = device.getHardwareVersion();
        return MiBandConst.MI_1A.equals(hwVersion);
    }
}
