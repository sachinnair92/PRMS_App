package com.voodoo.PRMS_MiBand;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.voodoo.GadgetBridgeFiles.GBApplication;
import com.voodoo.GadgetBridgeFiles.activities.GBActivity;
import com.voodoo.GadgetBridgeFiles.adapter.GBDeviceAdapter;
import com.voodoo.GadgetBridgeFiles.devices.DeviceCoordinator;
import com.voodoo.GadgetBridgeFiles.impl.GBDevice;
import com.voodoo.GadgetBridgeFiles.model.DeviceService;
import com.voodoo.GadgetBridgeFiles.util.DeviceHelper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class SoapCall_UpdatePatientStatus extends AsyncTask<String, Integer, Long> {

    String edit_pt_name;
    String pt_hosp_name;
    String pt_amb_id;
    String pt_gender;
    String edit_pt_bloodgrp;
    String pt_cond;
    String edit_pt_prob;
    String pt_policecase;
    String uname;
    String type_of_user;
    String edit_pt_id;


    SoapCall_UpdatePatientStatus(String type_of_user, String uname,String pt_hosp_name,String pt_amb_id,String edit_pt_name,String edit_pt_id,String pt_gender ,String edit_pt_bloodgrp,String pt_cond,String edit_pt_prob,String pt_policecase){

        this.type_of_user=type_of_user;
        this.uname=uname;
        this.pt_hosp_name=pt_hosp_name;
        this.pt_amb_id=pt_amb_id;
        this.edit_pt_name=edit_pt_name;
        this.pt_gender=pt_gender;
        this.edit_pt_bloodgrp=edit_pt_bloodgrp;
        this.pt_cond=pt_cond;
        this.edit_pt_prob=edit_pt_prob;
        this.pt_policecase=pt_policecase;
        this.edit_pt_id=edit_pt_id;

    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
        }
        return null;
    }

    protected void onPreExecute() {
    }


    protected void onPostExecute(Long result) {
    }

    Document Update_patient(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/update_patient";
        String METHOD_NAME = "update_patient";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("hospital_name", pt_hosp_name);
        request.addProperty("ambulance_id", pt_amb_id);
        request.addProperty("p_name", edit_pt_name);
        request.addProperty("p_id", edit_pt_id);
        request.addProperty("gender", pt_gender);
        request.addProperty("blood_grp", edit_pt_bloodgrp);
        request.addProperty("condition", pt_cond);
        request.addProperty("problem", edit_pt_prob);
        request.addProperty("police_case", pt_policecase);
        request.addProperty("is_enabled", "No");



        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            String resp= String .valueOf(envelope.getResponse());
            Document doc=convertStringToDocument(resp);

            return doc;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    void Check()
    {


        if(edit_pt_name.equals(""))
        {
            edit_pt_name="null";
        }

        if(edit_pt_bloodgrp.equals(""))
        {
            edit_pt_bloodgrp="null";
        }

        if(edit_pt_prob.equals(""))
        {
            edit_pt_prob="null";
        }


        final Document doc = Update_patient();
    }

}

class SoapCall_HR_Update extends AsyncTask<String, Integer, Long> {


    String pt_hosp_name;
    String pt_amb_id;
    String pt_id;
    String pt_heartrate;


    SoapCall_HR_Update(String pt_hosp_name,String pt_amb_id,String pt_id,String pt_heartrate){

        this.pt_hosp_name=pt_hosp_name;
        this.pt_amb_id=pt_amb_id;
        this.pt_id=pt_id;
        this.pt_heartrate=pt_heartrate;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    protected void onPreExecute() {


    }


    protected void onPostExecute(Long result) {
    }

    Document updateHR(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/update_heartrate";
        String METHOD_NAME = "update_heartrate";

        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String hr_timestamp=s.format(new Date());

        System.out.println("Time stamp is "+hr_timestamp);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("hospital_name", pt_hosp_name);
        request.addProperty("ambulance_id", pt_amb_id);
        request.addProperty("p_id", pt_id);
        request.addProperty("heartrate", pt_heartrate);
        request.addProperty("timestamp", hr_timestamp);



        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            String resp= String .valueOf(envelope.getResponse());
            Document doc=convertStringToDocument(resp);

            return doc;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    void Check()
    {

        final Document doc = updateHR();

        String result="null";

        try{
            result =doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();
        }catch(Exception e){
            e.printStackTrace();
        }

        if (result.equals("true")) {
            System.out.println("successful + "+pt_heartrate);
        }
        else
        {
            System.out.println("err : "+convertDocumentToString(doc));
            System.out.println("not successful");
        }
    }
    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

}


public class PRMS_MainActivity extends GBActivity {

    private final BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case DeviceService.ACTION_HEARTRATE_MEASUREMENT: {
                    int hrValue = intent.getIntExtra(DeviceService.EXTRA_HEART_RATE_VALUE, -1);
                    pt_heartrate=String.valueOf(hrValue);

                    TextView last_measured_hr = (TextView) findViewById(R.id.last_measured_hr);
                    last_measured_hr.setText("Last Measured Heart Rate: "+pt_heartrate+" BPM");
                    last_measured_hr.setVisibility(View.VISIBLE);

                    AsyncTask task = new SoapCall_HR_Update(hospital_name, ambulance_id, P_id,pt_heartrate).execute();
                    break;
                }
            }
        }
    };

    String pt_heartrate="0";
    private static final Logger LOG = LoggerFactory.getLogger(PRMS_MainActivity.class);

    public static final String ACTION_REFRESH_DEVICELIST
            = "com.voodoo.PRMS_MiBand.controlcenter.action.set_version";


    private static final String ACTION_REPLY
            = "com.voodoo.PRMS_MiBand.DebugActivity.action.reply";

    static private int is_connected=0;
    static Thread hrThread=null;

    private GBDeviceAdapter mGBDeviceAdapter;
    private GBDevice selectedDevice = null;
    private static final int REQUEST_ENABLE_BT = 1;
    private final List<GBDevice> deviceList = new ArrayList<>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case GBApplication.ACTION_QUIT:
                    finish();
                    break;
                case ACTION_REFRESH_DEVICELIST:
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    refreshPairedDevices();
                    break;
                case GBDevice.ACTION_DEVICE_CHANGED:
                    GBDevice dev = intent.getParcelableExtra(GBDevice.EXTRA_DEVICE);
                    if (dev.getAddress() != null) {
                        int index = deviceList.indexOf(dev); // search by address
                        if (index >= 0) {
                            deviceList.set(index, dev);
                        } else {
                            deviceList.add(dev);
                        }
                    }
                    updateSelectedDevice(dev);
                    refreshPairedDevices();

                    break;
            }
        }
    };

    public static int getIs_connected() {
        return is_connected;
    }

    public static void setIs_connected(int is_connected) {
        PRMS_MainActivity.is_connected = is_connected;
    }

    private void updateSelectedDevice(GBDevice dev) {
        if (selectedDevice == null) {
            selectedDevice = dev;
        } else {
            if (!selectedDevice.equals(dev)) {
                if (selectedDevice.isConnected() && dev.isConnected()) {
                    LOG.warn("multiple connected devices -- this is currently not really supported");
                    selectedDevice = dev; // use the last one that changed
                }
                if (!selectedDevice.isConnected()) {
                    selectedDevice = dev; // use the last one that changed
                }
            }
        }
    }


    private void findDevice(boolean start) {
        GBApplication.deviceService().onFindDevice(start);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver1);
        unregisterReceiver(mReceiver1);
        super.onDestroy();
    }

    private void refreshPairedDevices() {
        Set<GBDevice> availableDevices = DeviceHelper.getInstance().getAvailableDevices(this);
        deviceList.retainAll(availableDevices);
        for (GBDevice availableDevice : availableDevices) {
            if (!deviceList.contains(availableDevice)) {
                deviceList.add(availableDevice);
            }
        }
        boolean connected = false;
        for (GBDevice device : deviceList) {
            if (device.isConnected() || device.isConnecting()) {
                connected = true;
                break;
            }
        }

        if (deviceList.isEmpty()) {
           // background.setVisibility(View.VISIBLE);
        } else {
           // background.setVisibility(View.INVISIBLE);
        }

        if (connected) {
            DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(selectedDevice);
        } else if (!deviceList.isEmpty()) {
        }

        mGBDeviceAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_prms_main);
        ListView deviceListView = (ListView) findViewById(R.id.PairedDeviceListView);


        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(GBApplication.ACTION_QUIT);
        filter1.addAction(ACTION_REPLY);
        filter1.addAction(DeviceService.ACTION_HEARTRATE_MEASUREMENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver1, filter1);
        registerReceiver(mReceiver1, filter1); // for ACTION_REPLY


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



        TextView last_measured_hr = (TextView) findViewById(R.id.last_measured_hr);
        last_measured_hr.setVisibility(View.INVISIBLE);

        System.out.println("Is_connected is "+is_connected);


            if(hrThread==null)
            {

            }
            else {
                try {
                    hrThread.interrupt();
                } catch (Exception e) {
                }
            }
            hrThread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {

                            if(is_connected==1) {
                                GBApplication.deviceService().onHeartRateTest();
                                System.out.println("thread updating");
                            }
                            sleep(15000);
                            //   handler.post(this);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            hrThread.start();


        TextView PatientIdText=(TextView) findViewById(R.id.PatientIdText);
        PatientIdText.setText("Patient Id : "+P_id);
        Button bt=(Button) findViewById(R.id.EditDetailsbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditPatientDetails.class);
                intent.putExtra("P_id", P_id);
                intent.putExtra("tou", tou);
                intent.putExtra("ambulance_id", ambulance_id);
                intent.putExtra("hospital_name", hospital_name);
                intent.putExtra("uname", uname);
                intent.putExtra("pt_name", pt_name);
                intent.putExtra("pt_bloodgrp", pt_bloodgrp);
                intent.putExtra("pt_prob", pt_prob);
                intent.putExtra("pt_gender", pt_gender);
                intent.putExtra("pt_cond", pt_cond);
                intent.putExtra("pt_policecase", pt_policecase);
                is_connected=0;
                startActivity(intent);
                finish();

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PRMS_DiscoverActivity.class);
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
                is_connected=0;
                startActivity(intent);
                finish();
            }
        });



        getSupportActionBar().setTitle(Html.fromHtml("<small>Patient Realtime Monitoring System</small>"));

        mGBDeviceAdapter = new GBDeviceAdapter(this, deviceList);
        deviceListView.setAdapter(this.mGBDeviceAdapter);


        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                GBDevice gbDevice = deviceList.get(position);
                if (gbDevice.isInitialized()) {
                   /* DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(gbDevice);
                    Class<? extends Activity> primaryActivity = coordinator.getPrimaryActivity();
                    if (primaryActivity != null) {
                        GBApplication.deviceService().connect(deviceList.get(position));
                        Intent startIntent = new Intent(getApplicationContext(), PRMS_MeasureHR.class);
                        startIntent.putExtra(GBDevice.EXTRA_DEVICE, gbDevice);
                        startActivity(startIntent);
                    }*/
                } else {
                    GBApplication.deviceService().connect(deviceList.get(position));
                }
            }
        });



        registerForContextMenu(deviceListView);

        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBApplication.ACTION_QUIT);
        filterLocal.addAction(ACTION_REFRESH_DEVICELIST);
        filterLocal.addAction(GBDevice.ACTION_DEVICE_CHANGED);
        filterLocal.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);

        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        refreshPairedDevices();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }


        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

*/
        GBApplication.deviceService().start();
/*
        if (GB.isBluetoothEnabled() && deviceList.isEmpty() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startActivity(new Intent(this, DiscoveryActivity.class));
        } else {
            GBApplication.deviceService().requestDeviceInfo();
        }*/

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermissions() {
        List<String> wantedPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.BLUETOOTH);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (!wantedPermissions.isEmpty())
            ActivityCompat.requestPermissions(this, wantedPermissions.toArray(new String[wantedPermissions.size()]), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Main Activity", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover bluetooth devices");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:


                new AlertDialog.Builder(this)
                        .setTitle("Going back will end current patient Session")
                        .setMessage("Go Back ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AsyncTask task = new SoapCall_UpdatePatientStatus(String.valueOf(tou),String.valueOf(uname),String.valueOf(hospital_name),String.valueOf(ambulance_id),String.valueOf(pt_name),String.valueOf(P_id),String.valueOf(pt_gender) ,String.valueOf(pt_bloodgrp),String.valueOf(pt_cond),String.valueOf(pt_prob),String.valueOf(pt_policecase)).execute();
                                Intent intent = new Intent(getApplicationContext(), Add_New_Patient_Activity.class);
                                intent.putExtra("tou", tou);
                                intent.putExtra("ambulance_id", ambulance_id);
                                intent.putExtra("hospital_name",hospital_name);
                                intent.putExtra("uname",uname);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                break;
        }

        return true;
    }



}
