package com.voodoo.GadgetBridgeFiles.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.PRMS_MiBand.R;


public class GBActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (GBApplication.isDarkThemeEnabled()) {
            setTheme(R.style.GadgetbridgeThemeDark);
        } else {
            setTheme(R.style.GadgetbridgeTheme);
        }

        super.onCreate(savedInstanceState);
    }
}
