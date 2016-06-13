package com.voodoo.GadgetBridgeFiles.devices.pebble;

import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;
import com.voodoo.GadgetBridgeFiles.model.ActivityKind;

public class HealthSampleProvider implements SampleProvider {
    public static final int TYPE_DEEP_SLEEP = 5;
    public static final int TYPE_LIGHT_SLEEP = 4;
    public static final int TYPE_ACTIVITY = -1;

    protected final float movementDivisor = 8000f;

    @Override
    public int normalizeType(int rawType) {
        switch (rawType) {
            case TYPE_DEEP_SLEEP:
                return ActivityKind.TYPE_DEEP_SLEEP;
            case TYPE_LIGHT_SLEEP:
                return ActivityKind.TYPE_LIGHT_SLEEP;
            case TYPE_ACTIVITY:
            default:
                return ActivityKind.TYPE_UNKNOWN;
        }
    }

    @Override
    public int toRawActivityKind(int activityKind) {
        switch (activityKind) {
            case ActivityKind.TYPE_ACTIVITY:
                return TYPE_ACTIVITY;
            case ActivityKind.TYPE_DEEP_SLEEP:
                return TYPE_DEEP_SLEEP;
            case ActivityKind.TYPE_LIGHT_SLEEP:
                return TYPE_LIGHT_SLEEP;
            case ActivityKind.TYPE_UNKNOWN: // fall through
            default:
                return TYPE_ACTIVITY;
        }
    }


    @Override
    public float normalizeIntensity(int rawIntensity) {
        return rawIntensity / movementDivisor;
    }


    @Override
    public int getID() {
        return SampleProvider.PROVIDER_PEBBLE_HEALTH;
    }
}
