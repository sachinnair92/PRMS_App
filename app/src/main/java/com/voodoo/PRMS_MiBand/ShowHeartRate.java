package com.voodoo.PRMS_MiBand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

class SoapCall_GetHeartRate extends AsyncTask<String, Integer, Long> {

    String Hospital_Name;
    ShowHeartRate shr;
    ProgressDialog dialog;
    String p_id;
    String Ambulance_id;
    GifDrawable gifDrawable;

    final ShowHeartRate shr1;

    SoapCall_GetHeartRate(ShowHeartRate shr,String Hospital_Name,String Ambulance_id,String p_id){
        dialog = new ProgressDialog(shr);
        // dialog.setTitle("Processing...");
        dialog.setMessage(Html.fromHtml("Retrieving HeartRate. Please wait..."));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        this.Ambulance_id=Ambulance_id;
        this.p_id=p_id;
        this.Hospital_Name=Hospital_Name;

        this.shr=shr;
        shr1=shr;

        GifImageView gifImageView = (GifImageView) shr1.findViewById(R.id.Circle_gif);
        gifDrawable = (GifDrawable) gifImageView.getDrawable();

        ShowHeartRate.Stop_Sync_heartrate();


    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
            e.printStackTrace();
            shr.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(shr.getApplicationContext(), "Some error occurred. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            e.printStackTrace();
        }

        return null;
    }

    protected void onPreExecute() {
        dialog.show();
    }

    protected void onPostExecute(Long result) {
        dialog.dismiss();


        shr.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        gifDrawable.reset();
        gifDrawable.start();
        ShowHeartRate.Sync_heartrate();

    }

    Document show_heartrate(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/get_heartrate";
        String METHOD_NAME = "get_heartrate";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("hospital_name", Hospital_Name);
        request.addProperty("ambulance_id", Ambulance_id);
        request.addProperty("p_id", p_id);


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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) shr.getSystemService(shr.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    ArrayList<String> HeartRate_List;
    ArrayList<String> TimeStamp_List;


    void Check()
    {

        if(!isNetworkAvailable())
        {
            shr.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(shr.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            return;
        }


        final Document doc = show_heartrate();
        HeartRate_List=new ArrayList<>();
        TimeStamp_List=new ArrayList<>();

        shr.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result = "null";
                try {
                    result = doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result.equals("true")) {
                    int count;
                    count=Integer.parseInt(doc.getElementsByTagName("count").item(0).getChildNodes().item(0).getNodeValue());
                    String hr =String.valueOf(doc.getElementsByTagName("heartrate").item(0).getChildNodes().item(0).getNodeValue());
                    String tm =String.valueOf(doc.getElementsByTagName("timestamp").item(0).getChildNodes().item(0).getNodeValue());

                    System.out.println(convertDocumentToString(doc));
                    if(count>0)
                    {
                        String[] hr1=hr.split(";");
                        String[] tm1=tm.split(";");

                        if(count<11)
                        {
                            TextView tx=(TextView) shr.findViewById(R.id.HeartRateText);

                            tx.setText(hr1[count-1]);

                            for(int i=count-2;i>=0;i--)
                            {
                                HeartRate_List.add(hr1[i]);
                                TimeStamp_List.add(tm1[i]);
                            }
                        }else
                        {
                            int cnt1=count;
                            cnt1=cnt1%10;
                            cnt1--;


                            if(cnt1==-1)
                            {
                                cnt1=9;
                            }
                            TextView tx=(TextView) shr.findViewById(R.id.HeartRateText);

                            tx.setText(hr1[cnt1]);


                            for(int i=cnt1-1;i!=cnt1;i--)
                            {
                                if(i==-1)
                                {
                                    i=9;
                                    if(i==cnt1)
                                    {
                                        break;
                                    }
                                }

                                HeartRate_List.add(hr1[i]);
                                TimeStamp_List.add(tm1[i]);


                            }

                        }


                        ListView lv=(ListView) shr.findViewById(R.id.heartrate_list);
                        lv.setAdapter(new Heartrate_List_CustomAdapter(shr, HeartRate_List,TimeStamp_List));
                    }
                } else {
                    Toast toast = Toast.makeText(shr.getApplicationContext(), "Some error Occurred. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });


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


public class ShowHeartRate extends AppCompatActivity {


    String type_of_user;
    String hospital_name;
    String uname;
    String P_id;
    String string_doc;
    String ambulance_id;
    static Thread hrThread=null;
    static GifDrawable gifDrawable;
    static ShowHeartRate shr_main;
    static String P_id1;
    static String hospital_name1;
    static String ambulance_id1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_heart_rate);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.show_heartrate_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Heart Rate </font>"));


        GifImageView gifImageView = (GifImageView) findViewById(R.id.Circle_gif);
        gifDrawable = (GifDrawable) gifImageView.getDrawable();

        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        type_of_user = intent.getStringExtra("tou");
        hospital_name = intent.getStringExtra("hospital_name");
        uname = intent.getStringExtra("uname");
        P_id = intent.getStringExtra("P_id");
        string_doc = intent.getStringExtra("string_doc");
        ambulance_id = intent.getStringExtra("ambulance_id");

        hospital_name1=hospital_name;
        ambulance_id1=ambulance_id;
        P_id1=P_id;

        AsyncTask task = new SoapCall_GetHeartRate(ShowHeartRate.this, String.valueOf(hospital_name), ambulance_id, P_id).execute();

        shr_main=ShowHeartRate.this;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), Show_Selected_Patient_Details.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("hospital_name", hospital_name);
                intent.putExtra("uname", uname);
                intent.putExtra("P_id", P_id);
                intent.putExtra("string_doc", string_doc);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Show_Selected_Patient_Details.class);
        intent.putExtra("tou", type_of_user);
        intent.putExtra("hospital_name", hospital_name);
        intent.putExtra("uname", uname);
        intent.putExtra("P_id", P_id);
        intent.putExtra("string_doc", string_doc);
        startActivity(intent);
        finish();
    }

    static void Sync_heartrate()
    {
        hrThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {

                        if (gifDrawable.isRunning() == false) {
                            shr_main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AsyncTask task = new SoapCall_GetHeartRate(shr_main, hospital_name1, ambulance_id1, P_id1).execute();
                                }
                            });
                            break;
                        }
                        //   handler.post(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        hrThread.start();
    }

    static void Stop_Sync_heartrate()
    {
        if(hrThread==null)
        {

        }
        else {
            try {
                hrThread.interrupt();
            } catch (Exception e) {
            }
        }
    }
}
