package com.voodoo.GadgetBridgeFiles.service;

import android.content.Intent;

import com.voodoo.GadgetBridgeFiles.impl.GBDeviceService;
import com.voodoo.PRMS_MiBand.test.GBMockIntent;

public class TestDeviceService extends GBDeviceService {
    private final AbstractServiceTestCase<?> mTestCase;

    public TestDeviceService(AbstractServiceTestCase<?> testCase) throws Exception {
        super(testCase.getContext());
        mTestCase = testCase;
    }

    @Override
    protected Intent createIntent() {
        return new GBMockIntent();
    }

    @Override
    protected void invokeService(Intent intent) {
        mTestCase.startService(intent);
    }
}
