package com.voodoo.GadgetBridgeFiles.service.devices.pebble;


import android.util.Pair;

import java.util.ArrayList;
import java.util.UUID;

import com.voodoo.GadgetBridgeFiles.deviceevents.GBDeviceEvent;

public class AppMessageHandler {
    protected final PebbleProtocol mPebbleProtocol;
    protected final UUID mUUID;

    AppMessageHandler(UUID uuid, PebbleProtocol pebbleProtocol) {
        mUUID = uuid;
        mPebbleProtocol = pebbleProtocol;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        return null;
    }

    public GBDeviceEvent[] pushMessage() {
        return null;
    }
}