package com.voodoo.PRMS_MiBand;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import com.voodoo.GadgetBridgeFiles.activities.GBActivity;
import com.voodoo.GadgetBridgeFiles.adapter.DeviceCandidateAdapter;
import com.voodoo.GadgetBridgeFiles.devices.DeviceCoordinator;
import com.voodoo.GadgetBridgeFiles.devices.miband.MiBandPairingActivity;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.impl.GBDeviceCandidate;
import com.voodoo.GadgetBridgeFiles.util.DeviceHelper;
import com.voodoo.GadgetBridgeFiles.util.GB;

public class PRMS_DiscoverActivity extends GBActivity implements AdapterView.OnItemClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(PRMS_DiscoverActivity.class);
    private static final long SCAN_DURATION = 60000; // 60s

    private final Handler handler = new Handler();
    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            handleDeviceFound(device, (short) rssi);
        }
    };

    private enum Scanning {
        SCANNING_BT,
        SCANNING_BTLE,
        SCANNING_OFF
    }

    private final Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            stopDiscovery();
        }
    };

    private ProgressBar progressView;
    private BluetoothAdapter adapter;
    private final ArrayList<GBDeviceCandidate> deviceCandidates = new ArrayList<>();
    private DeviceCandidateAdapter cadidateListAdapter;
    private Button startButton;
    private Scanning isScanning = Scanning.SCANNING_OFF;
    private String bondingAddress;
    ListView BtDeviceNamelist;
    private List<String> BtDeviceNamelist_file;
    ArrayList<BluetoothDevice> BtDeviceList;
    ArrayList DeviceRssiList;


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    discoveryStarted(Scanning.SCANNING_BT);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    // continue with LE scan, if available
                    if (isScanning == Scanning.SCANNING_BT) {
                        startDiscovery(Scanning.SCANNING_BTLE);
                    } else {
                        discoveryFinished();
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);
                    int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                    bluetoothStateChanged(oldState, newState);
                    break;
                case BluetoothDevice.ACTION_FOUND: {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, GBDevice.RSSI_UNKNOWN);
                    handleDeviceFound(device, rssi);
                    break;
                }
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null && device.getAddress().equals(bondingAddress)) {
                        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                        if (bondState == BluetoothDevice.BOND_BONDED) {
                            GB.toast(PRMS_DiscoverActivity.this, "Successfully bonded with: " + bondingAddress, Toast.LENGTH_SHORT, GB.INFO);
                            finish();
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("deviceCandidates", deviceCandidates);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Parcelable> restoredCandidates = savedInstanceState.getParcelableArrayList("deviceCandidates");
        if (restoredCandidates != null) {
            deviceCandidates.clear();
            for (Parcelable p : restoredCandidates) {
                deviceCandidates.add((GBDeviceCandidate) p);
            }
        }
    }

    public void onStartButtonClick(View button) {
        LOG.debug("Start Button clicked");
        if (isScanning()) {
            stopDiscovery();
        } else {
            startDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }

    private void handleDeviceFound(BluetoothDevice device, short rssi) {
        GBDeviceCandidate candidate = new GBDeviceCandidate(device, rssi);



        if (DeviceHelper.getInstance().isSupported(candidate)) {
            int index = deviceCandidates.indexOf(candidate);
            if (index >= 0) {
                deviceCandidates.set(index, candidate); // replace
            } else {
                deviceCandidates.add(candidate);
                BtDeviceList.add(device);
                DeviceRssiList.add(rssi);
                BtDeviceNamelist_file.add(String.valueOf(device.getName()));
                BtDeviceNamelist.setAdapter(new ArrayAdapter<String>(PRMS_DiscoverActivity.this, android.R.layout.simple_list_item_1, BtDeviceNamelist_file));
            }
            cadidateListAdapter.notifyDataSetChanged();
        }
    }

    private void startDiscovery() {
        if (isScanning()) {
            LOG.warn("Not starting discovery, because already scanning.");
            return;
        }
        startDiscovery(Scanning.SCANNING_BT);
    }

    private void startDiscovery(Scanning what) {
        LOG.info("Starting discovery: " + what);
        discoveryStarted(what); // just to make sure
        if (ensureBluetoothReady()) {
            if (what == Scanning.SCANNING_BT) {
                startBTDiscovery();
            } else if (what == Scanning.SCANNING_BTLE) {
                if (GB.supportsBluetoothLE()) {
                    startBTLEDiscovery();
                } else {
                    discoveryFinished();
                }
            }
        } else {
            discoveryFinished();
            //Toast.makeText(this, "Enable Bluetooth to discover devices.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isScanning() {
        return isScanning != Scanning.SCANNING_OFF;
    }

    private void stopDiscovery() {
        LOG.info("Stopping discovery");
        if (isScanning()) {
            Scanning wasScanning = isScanning;
            // unfortunately, we don't always get a call back when stopping the scan, so
            // we do it manually; BEFORE stopping the scan!
            discoveryFinished();

            if (wasScanning == Scanning.SCANNING_BT) {
                stopBTDiscovery();
            } else if (wasScanning == Scanning.SCANNING_BTLE) {
                stopBTLEDiscovery();
            }
            handler.removeMessages(0, stopRunnable);
        }
    }

    private void stopBTLEDiscovery() {
        adapter.stopLeScan(leScanCallback);
    }

    private void stopBTDiscovery() {
        adapter.cancelDiscovery();
    }

    private void bluetoothStateChanged(int oldState, int newState) {
        discoveryFinished();
        if (newState == BluetoothAdapter.STATE_ON) {
            this.adapter = BluetoothAdapter.getDefaultAdapter();
            startButton.setEnabled(true);
        } else {
            this.adapter = null;
            startButton.setEnabled(false);
        }
    }

    private void discoveryFinished() {
        isScanning = Scanning.SCANNING_OFF;
        progressView.setVisibility(View.GONE);
        startButton.setText(getString(R.string.discovery_start_scanning));
    }

    private void discoveryStarted(Scanning what) {
        isScanning = what;
        progressView.setVisibility(View.VISIBLE);
        startButton.setText(getString(R.string.discovery_stop_scanning));
    }

    private boolean ensureBluetoothReady() {
        boolean available = checkBluetoothAvailable();
        startButton.setEnabled(available);
        if (available) {
            adapter.cancelDiscovery();
            // must not return the result of cancelDiscovery()
            // appears to return false when currently not scanning
            return true;
        }
        return false;
    }

    private boolean checkBluetoothAvailable() {
        BluetoothManager bluetoothService = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (bluetoothService == null) {
            LOG.warn("No bluetooth available");
            this.adapter = null;
            return false;
        }
        BluetoothAdapter adapter = bluetoothService.getAdapter();
        if (adapter == null) {
            LOG.warn("No bluetooth available");
            this.adapter = null;
            return false;
        }
        if (!adapter.isEnabled()) {
            LOG.warn("Bluetooth not enabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            this.adapter = null;
            return false;
        }
        this.adapter = adapter;
        return true;
    }

    private void startBTLEDiscovery() {
        LOG.info("Starting BTLE Discovery");
        handler.removeMessages(0, stopRunnable);
        handler.sendMessageDelayed(getPostMessage(stopRunnable), SCAN_DURATION);
        adapter.startLeScan(leScanCallback);
    }

    private void startBTDiscovery() {
        LOG.info("Starting BT Discovery");
        handler.removeMessages(0, stopRunnable);
        handler.sendMessageDelayed(getPostMessage(stopRunnable), SCAN_DURATION);
        adapter.startDiscovery();
    }

    private Message getPostMessage(Runnable runnable) {
        Message m = Message.obtain(handler, runnable);
        m.obj = runnable;
        return m;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GBDeviceCandidate deviceCandidate = deviceCandidates.get(position);
        if (deviceCandidate == null) {
            LOG.error("Device candidate clicked, but item not found");
            return;
        }

        stopDiscovery();
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(deviceCandidate);
        Class<? extends Activity> pairingActivity = coordinator.getPairingActivity();
        if (pairingActivity != null) {
            Intent intent = new Intent(this, MiBandPairingActivity.class);
            intent.putExtra(DeviceCoordinator.EXTRA_DEVICE_MAC_ADDRESS, deviceCandidate.getMacAddress());
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
            LOG.info("Going to pair to "+BtDeviceNamelist_file.get(position));
        } else {
            try {
                BluetoothDevice btDevice = adapter.getRemoteDevice(deviceCandidate.getMacAddress());
                if (btDevice.createBond()) {
                    // async, wait for bonding event to finish this activity
                    bondingAddress = btDevice.getAddress();
                }
            } catch (Exception e) {
                LOG.error("Error pairing device: " + deviceCandidate.getMacAddress());
            }
        }
    }

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
        setContentView(R.layout.activity_prms__discover_activity);

        Intent i = getIntent();
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

        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        startButton = (Button) findViewById(R.id.PRMS_ScanButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartButtonClick(startButton);
            }
        });


        BtDeviceNamelist_file =new ArrayList<String>();
        BtDeviceNamelist = (ListView)findViewById(R.id.ScannedDeviceListView);

        BtDeviceNamelist.setAdapter(new ArrayAdapter<String>(PRMS_DiscoverActivity.this, android.R.layout.simple_list_item_1, BtDeviceNamelist_file));
        BtDeviceNamelist.setOnItemClickListener(this);
        BtDeviceList=new ArrayList<BluetoothDevice>();
        DeviceRssiList=new ArrayList();

        progressView = (ProgressBar) findViewById(R.id.PRMS_Discover_Progress_Bar);
        progressView.setProgress(0);
        progressView.setIndeterminate(true);
        progressView.setVisibility(View.GONE);


        cadidateListAdapter = new DeviceCandidateAdapter(this, deviceCandidates);
/*        deviceCandidatesView.setAdapter(cadidateListAdapter);
        deviceCandidatesView.setOnItemClickListener(this);*/

        IntentFilter bluetoothIntents = new IntentFilter();
        bluetoothIntents.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothIntents.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bluetoothIntents.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothIntents.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothIntents.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(bluetoothReceiver, bluetoothIntents);

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
    }


}
