package com.voodoo.GadgetBridgeFiles.devices;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.voodoo.GadgetBridgeFiles.activities.ControlCenter;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.impl.GBDeviceCandidate;
import com.voodoo.GadgetBridgeFiles.model.ActivityKind;
import com.voodoo.GadgetBridgeFiles.model.DeviceType;

public class UnknownDeviceCoordinator extends AbstractDeviceCoordinator {
    private final UnknownSampleProvider sampleProvider;

    private static final class UnknownSampleProvider implements SampleProvider {
        @Override
        public int normalizeType(int rawType) {
            return ActivityKind.TYPE_UNKNOWN;
        }

        @Override
        public int toRawActivityKind(int activityKind) {
            return 0;
        }

        @Override
        public float normalizeIntensity(int rawIntensity) {
            return 0;
        }

        @Override
        public int getID() {
            return PROVIDER_UNKNOWN;
        }
    }

    public UnknownDeviceCoordinator() {
        sampleProvider = new UnknownSampleProvider();
    }

    @Override
    public boolean supports(GBDeviceCandidate candidate) {
        return false;
    }

    @Override
    public boolean supports(GBDevice device) {
        return getDeviceType().equals(device.getType());
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.UNKNOWN;
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return ControlCenter.class;
    }

    @Override
    public Class<? extends Activity> getPrimaryActivity() {
        return null;
    }

    @Override
    public SampleProvider getSampleProvider() {
        return sampleProvider;
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return false;
    }

    @Override
    public boolean supportsScreenshots() {
        return false;
    }

    @Override
    public boolean supportsAlarmConfiguration() {
        return false;
    }

    @Override
    public int getTapString() {
        return 0;
    }
}
