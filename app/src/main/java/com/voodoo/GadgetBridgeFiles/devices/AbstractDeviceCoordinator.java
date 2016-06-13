package com.voodoo.GadgetBridgeFiles.devices;

import com.voodoo.GadgetBridgeFiles.impl.GBDevice;

public abstract class AbstractDeviceCoordinator implements DeviceCoordinator {
    public boolean allowFetchActivityData(GBDevice device) {
        return device.isInitialized() && !device.isBusy() && supportsActivityDataFetching();
    }
}
