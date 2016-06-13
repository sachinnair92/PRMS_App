package com.voodoo.GadgetBridgeFiles.deviceevents;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.PRMS_MiBand.R;

public class GBDeviceEventVersionInfo extends GBDeviceEvent {
    public String fwVersion = GBApplication.getContext().getString(R.string.n_a);
    public String hwVersion = GBApplication.getContext().getString(R.string.n_a);
}
