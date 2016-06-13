package com.voodoo.GadgetBridgeFiles.devices.pebble;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.PRMS_MiBand.R;
import com.voodoo.GadgetBridgeFiles.activities.AppManagerActivity;
import com.voodoo.GadgetBridgeFiles.devices.AbstractDeviceCoordinator;
import com.voodoo.GadgetBridgeFiles.devices.InstallHandler;
import com.voodoo.GadgetBridgeFiles.devices.SampleProvider;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.impl.GBDeviceCandidate;
import com.voodoo.GadgetBridgeFiles.model.DeviceType;
import com.voodoo.GadgetBridgeFiles.util.Prefs;

public class PebbleCoordinator extends AbstractDeviceCoordinator {
    public PebbleCoordinator() {
    }

    @Override
    public boolean supports(GBDeviceCandidate candidate) {
        return candidate.getName().startsWith("Pebble");
    }

    @Override
    public boolean supports(GBDevice device) {
        return getDeviceType().equals(device.getType());
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.PEBBLE;
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    public Class<? extends Activity> getPrimaryActivity() {
        return AppManagerActivity.class;
    }

    @Override
    public SampleProvider getSampleProvider() {
        Prefs prefs = GBApplication.getPrefs();
        int activityTracker = prefs.getInt("pebble_activitytracker", SampleProvider.PROVIDER_PEBBLE_HEALTH);
        switch (activityTracker) {
            case SampleProvider.PROVIDER_PEBBLE_HEALTH:
                return new HealthSampleProvider();
            case SampleProvider.PROVIDER_PEBBLE_MISFIT:
                return new MisfitSampleProvider();
            case SampleProvider.PROVIDER_PEBBLE_MORPHEUZ:
                return new MorpheuzSampleProvider();
            case SampleProvider.PROVIDER_PEBBLE_GADGETBRIDGE:
                return new PebbleGadgetBridgeSampleProvider();
            default:
                return new HealthSampleProvider();
        }
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        PBWInstallHandler installHandler = new PBWInstallHandler(uri, context);
        return installHandler.isValid() ? installHandler : null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return false;
    }

    @Override
    public boolean supportsScreenshots() {
        return true;
    }

    @Override
    public boolean supportsAlarmConfiguration() {
        return false;
    }

    @Override
    public int getTapString() {
        return R.string.tap_connected_device_for_app_mananger;
    }
}
