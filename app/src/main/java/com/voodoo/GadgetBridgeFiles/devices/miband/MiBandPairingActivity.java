package com.voodoo.GadgetBridgeFiles.devices.miband;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.activities.GBActivity;
import com.voodoo.PRMS_MiBand.PRMS_DiscoverActivity;
import com.voodoo.PRMS_MiBand.PRMS_MainActivity;
import com.voodoo.PRMS_MiBand.R;

import com.voodoo.GadgetBridgeFiles.activities.DiscoveryActivity;
import com.voodoo.GadgetBridgeFiles.devices.DeviceCoordinator;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.util.GB;
import com.voodoo.GadgetBridgeFiles.util.Prefs;

public class MiBandPairingActivity extends GBActivity {
    private static final Logger LOG = LoggerFactory.getLogger(MiBandPairingActivity.class);

    private static final int REQ_CODE_USER_SETTINGS = 52;
    private static final String STATE_MIBAND_ADDRESS = "mibandMacAddress";
    private static final long DELAY_AFTER_BONDING = 1000; // 1s
    private TextView message;
    private boolean isPairing;
    private String macAddress;
    private String bondingMacAddress;

    private final BroadcastReceiver mPairingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GBDevice.ACTION_DEVICE_CHANGED.equals(intent.getAction())) {
                GBDevice device = intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                LOG.debug("pairing activity: device changed: " + device);
                if (macAddress.equals(device.getAddress()) && device.isInitialized()) {
                    pairingFinished(true);
                }
            }
        }
    };

    private final BroadcastReceiver mBondingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bondingMacAddress != null && bondingMacAddress.equals(device.getAddress())) {
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        LOG.info("Bonded with " + device.getAddress());
                        bondingMacAddress = null;
                        Looper mainLooper = Looper.getMainLooper();
                        new Handler(mainLooper).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                performPair();
                            }
                        }, DELAY_AFTER_BONDING);
                    }
                }
            }
        }
    };
    String P_id;
    String tou;
    String ambulance_id;
    String hospital_name;
    String uname;
    String pt_name;
    String pt_bloodgrp;
    String pt_prob;
    String pt_gender;
    String pt_cond;
    String pt_policecase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_band_pairing);


        getSupportActionBar().setTitle(Html.fromHtml("<small>Mi Band</small>"));


        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        message = (TextView) findViewById(R.id.miband_pair_message);
        Intent i = getIntent();
        macAddress = i.getStringExtra(DeviceCoordinator.EXTRA_DEVICE_MAC_ADDRESS);
        P_id=i.getStringExtra("P_id");
        tou=i.getStringExtra("tou");
        ambulance_id=i.getStringExtra("ambulance_id");
        hospital_name=i.getStringExtra("hospital_name");
        uname=i.getStringExtra("uname");
        pt_name=i.getStringExtra("pt_name");
        pt_bloodgrp=i.getStringExtra("pt_bloodgrp");
        pt_prob=i.getStringExtra("pt_prob");
        pt_gender=i.getStringExtra("pt_gender");
        pt_cond=i.getStringExtra("pt_cond");
        pt_policecase=i.getStringExtra("pt_policecase");

        if (macAddress == null && savedInstanceState != null) {
            macAddress = savedInstanceState.getString(STATE_MIBAND_ADDRESS, null);
        }
        if (macAddress == null) {
            Toast.makeText(this, getString(R.string.message_cannot_pair_no_mac), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DiscoveryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("P_id", P_id);
            intent.putExtra("tou", tou);
            intent.putExtra("ambulance_id", ambulance_id);
            intent.putExtra("hospital_name",hospital_name);
            intent.putExtra("uname",uname);
            intent.putExtra("pt_name",pt_name);
            intent.putExtra("pt_bloodgrp",pt_bloodgrp);
            intent.putExtra("pt_prob",pt_prob);
            intent.putExtra("pt_gender",pt_gender);
            intent.putExtra("pt_cond", pt_cond);
            intent.putExtra("pt_policecase", pt_policecase);
            startActivity(intent);
            finish();
            return;
        }



        /*if (!MiBandCoordinator.hasValidUserInfo()) {
            Intent userSettingsIntent = new Intent(this, MiBandPreferencesActivity.class);
            startActivityForResult(userSettingsIntent, REQ_CODE_USER_SETTINGS, null);
            return;
        }*/
        message.setText("pairing with "+String.valueOf(macAddress));

        // already valid user info available, use that and pair
        startPairing();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_MIBAND_ADDRESS, macAddress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        macAddress = savedInstanceState.getString(STATE_MIBAND_ADDRESS, macAddress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // start pairing immediately when we return from the user settings
        if (requestCode == REQ_CODE_USER_SETTINGS) {
            if (!MiBandCoordinator.hasValidUserInfo()) {
                GB.toast(this, getString(R.string.miband_pairing_using_dummy_userdata), Toast.LENGTH_LONG, GB.WARN);
            }
            startPairing();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            // just to be sure, remove the receivers -- might actually be already unregistered
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mPairingReceiver);
            unregisterReceiver(mBondingReceiver);
        } catch (IllegalArgumentException ex) {
            // already unregistered, ignore
        }
        if (isPairing) {
            stopPairing();
        }
        super.onDestroy();
    }

    private void startPairing() {
        isPairing = true;
        message.setText(getString(R.string.miband_pairing, macAddress));

        IntentFilter filter = new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPairingReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBondingReceiver, filter);

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress);
        if (device != null) {
            performBluetoothPair(device);
        } else {
            GB.toast(this, "No such Bluetooth Device: " + macAddress, Toast.LENGTH_LONG, GB.ERROR);
        }
    }

    private void pairingFinished(boolean pairedSuccessfully) {
        LOG.debug("pairingFinished: " + pairedSuccessfully);
        if (!isPairing) {
            // already gone?
            return;
        }

        isPairing = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPairingReceiver);
        unregisterReceiver(mBondingReceiver);

        if (pairedSuccessfully) {

            Toast.makeText(this,"Pairing Successful.",Toast.LENGTH_LONG).show();
            Prefs prefs = GBApplication.getPrefs();
            prefs.getPreferences().edit().putString(MiBandConst.PREF_MIBAND_ADDRESS, macAddress).apply();
        }

        Intent intent = new Intent(this, PRMS_MainActivity.class);
        intent.putExtra("P_id", P_id);
        intent.putExtra("tou", tou);
        intent.putExtra("ambulance_id", ambulance_id);
        intent.putExtra("hospital_name",hospital_name);
        intent.putExtra("uname",uname);
        intent.putExtra("pt_name",pt_name);
        intent.putExtra("pt_bloodgrp",pt_bloodgrp);
        intent.putExtra("pt_prob",pt_prob);
        intent.putExtra("pt_gender",pt_gender);
        intent.putExtra("pt_cond",pt_cond);
        intent.putExtra("pt_policecase",pt_policecase);
        startActivity(intent);
        finish();
    }

    private void stopPairing() {
        // TODO
        isPairing = false;
    }

    protected void performBluetoothPair(BluetoothDevice device) {
        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_BONDED) {
            LOG.info("Already bonded: " + device.getAddress());
            performPair();
            return;
        }

        bondingMacAddress = device.getAddress();
        if (bondState == BluetoothDevice.BOND_BONDING) {
            GB.toast(this, "Bonding in progress: " + bondingMacAddress, Toast.LENGTH_LONG, GB.INFO);
            return;
        }

        GB.toast(this, "Creating bond with" + bondingMacAddress, Toast.LENGTH_LONG, GB.INFO);
        if (!device.createBond()) {
            GB.toast(this, "Unable to pair with " + bondingMacAddress, Toast.LENGTH_LONG, GB.ERROR);
        }
    }

    private void performPair() {
        GBApplication.deviceService().connect(macAddress, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), PRMS_MainActivity.class);
                intent.putExtra("P_id",P_id);
                intent.putExtra("tou", tou);
                intent.putExtra("ambulance_id", ambulance_id);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname",uname);
                intent.putExtra("pt_name",pt_name);
                intent.putExtra("pt_bloodgrp",pt_bloodgrp);
                intent.putExtra("pt_prob",pt_prob);
                intent.putExtra("pt_gender",pt_gender);
                intent.putExtra("pt_cond",pt_cond);
                intent.putExtra("pt_policecase",pt_policecase);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PRMS_MainActivity.class);
        intent.putExtra("P_id", P_id);
        intent.putExtra("tou", tou);
        intent.putExtra("ambulance_id", ambulance_id);
        intent.putExtra("hospital_name",hospital_name);
        intent.putExtra("uname",uname);
        intent.putExtra("pt_name",pt_name);
        intent.putExtra("pt_bloodgrp",pt_bloodgrp);
        intent.putExtra("pt_prob",pt_prob);
        intent.putExtra("pt_gender",pt_gender);
        intent.putExtra("pt_cond",pt_cond);
        intent.putExtra("pt_policecase",pt_policecase);
        startActivity(intent);
        finish();
    }
}
