package com.voodoo.PRMS_MiBand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.activities.GBActivity;
import com.voodoo.GadgetBridgeFiles.model.DeviceService;
import com.voodoo.GadgetBridgeFiles.util.GB;

public class PRMS_MeasureHR extends GBActivity {
    private static final Logger LOG = LoggerFactory.getLogger(PRMS_MeasureHR.class);

    private static final String EXTRA_REPLY = "reply";
    private static final String ACTION_REPLY
            = "com.voodoo.PRMS_MiBand.DebugActivity.action.reply";

    private Button HeartRateButton;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case DeviceService.ACTION_HEARTRATE_MEASUREMENT: {
                    int hrValue = intent.getIntExtra(DeviceService.EXTRA_HEART_RATE_VALUE, -1);

                    TextView tx=(TextView) findViewById(R.id.HRText);
                    tx.setText("Heart Rate measured: "+hrValue);
                    //toast(DebugActivity.this, "Heart Rate measured: " + hrValue, Toast.LENGTH_LONG, GB.INFO);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prms__measure_hr);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GBApplication.ACTION_QUIT);
        filter.addAction(ACTION_REPLY);
        filter.addAction(DeviceService.ACTION_HEARTRATE_MEASUREMENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        registerReceiver(mReceiver, filter); // for ACTION_REPLY

        TextView tx=(TextView) findViewById(R.id.HRText);
        tx.setText("Click to measure heart Rate");

        HeartRateButton = (Button) findViewById(R.id.HRButton);
        HeartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tx=(TextView) findViewById(R.id.HRText);
                tx.setText("Measuring Heart Rate please Wait...");
                GB.toast("Measuring heart rate, please wait...", Toast.LENGTH_LONG, GB.INFO);
                GBApplication.deviceService().onHeartRateTest();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        unregisterReceiver(mReceiver);
    }

}
