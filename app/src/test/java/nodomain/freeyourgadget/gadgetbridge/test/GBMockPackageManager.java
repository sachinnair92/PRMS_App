package com.voodoo.PRMS_MiBand.test;

import android.content.ComponentName;
import android.test.mock.MockPackageManager;

public class GBMockPackageManager extends MockPackageManager {
    @Override
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        // do nothing
    }
}
