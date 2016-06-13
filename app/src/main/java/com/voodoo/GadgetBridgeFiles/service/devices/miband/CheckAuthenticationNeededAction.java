package com.voodoo.GadgetBridgeFiles.service.devices.miband;

import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.service.btle.actions.AbortTransactionAction;

public class CheckAuthenticationNeededAction extends AbortTransactionAction {
    private final GBDevice mDevice;

    public CheckAuthenticationNeededAction(GBDevice device) {
        super();
        mDevice = device;
    }

    @Override
    protected boolean shouldAbort() {
        // the state is set in MiBandSupport.handleNotificationNotif()
        switch (mDevice.getState()) {
            case AUTHENTICATION_REQUIRED: // fall through
            case AUTHENTICATING:
                return true; // abort the whole thing
            default:
                return false;
        }
    }
}
