package com.voodoo.PRMS_MiBand;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;





class SoapCall_Amb_GetMessages extends AsyncTask<String, Integer, Long> {

    String Hospital_Name;
    Amb_MessageActivity ama;
    ProgressDialog dialog;
    String ambulance_id;
    String P_id;

    String S_uname;
    String S_time;
    String R_hosp_name;
    String R_amb_id;
    String R_pid;
    String Is_amb;
    String msg;
    String Uname;

    MessageAdapter messageAdapter;


    SoapCall_Amb_GetMessages(Amb_MessageActivity ama,String Hospital_Name,String ambulance_id,String P_id,String Uname,MessageAdapter messageAdapter){
        dialog = new ProgressDialog(ama);
        // dialog.setTitle("Processing...");
        dialog.setMessage(Html.fromHtml("Retrieving Messages. Please wait..."));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        this.Hospital_Name=Hospital_Name;
        this.ambulance_id=ambulance_id;
        this.P_id=P_id;
        this.ama=ama;
        this.messageAdapter=messageAdapter;
        this.Uname=Uname;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
            ama.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(ama.getApplicationContext(), "Some error occurred. Please try again", Toast.LENGTH_LONG);
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
    }

    Document get_Messages(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/get_Messages";
        String METHOD_NAME = "get_Messages";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("hospital_name", Hospital_Name);

        request.addProperty("ambulance_id", ambulance_id);

        request.addProperty("p_id", P_id);



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
                = (ConnectivityManager) ama.getSystemService(ama.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    void Check()
    {

        if(!isNetworkAvailable())
        {
            ama.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(ama.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            return;
        }


        final Document doc = get_Messages();

        ama.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result="null";
                try{
                    result =doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (result.equals("true")) {

                    int count =Integer.parseInt(doc.getElementsByTagName("count").item(0).getChildNodes().item(0).getNodeValue());
                    if(count>0)
                    {

                        // ListView lv=(ListView) ama.findViewById(R.id.DoctorlistView);
                        // lv.setAdapter(new Doctor_Patient_List_CustomAdapter(ama, PatientNameList, PatientCondList, AmbulanceList));
                        for(int i=0;i<count;i++)
                        {
                            S_uname=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
                            S_time=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                            R_hosp_name=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
                            R_amb_id=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
                            R_pid=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(4).getChildNodes().item(0).getNodeValue();
                            msg=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(5).getChildNodes().item(0).getNodeValue();
                            Is_amb=doc.getElementsByTagName("Message_" + i).item(0).getChildNodes().item(6).getChildNodes().item(0).getNodeValue();

                            if(S_uname.equals(Uname))
                            {
                                messageAdapter.addMessage(msg,S_uname,S_time,Is_amb,MessageAdapter.DIRECTION_OUTGOING);
                            }
                            else
                            {

                                messageAdapter.addMessage(msg,S_uname,S_time,Is_amb,MessageAdapter.DIRECTION_INCOMING);
                            }


                            System.out.println("\nS_uname: "+S_uname+" S_time: "+S_time+" R_hosp_name: "+R_hosp_name+" R_amb_id: "+R_amb_id+" R_pid: "+R_pid+" msg: "+msg+" Is_amb: "+Is_amb);
                        }

                    }
                }else
                {
                    Toast toast = Toast.makeText(ama.getApplicationContext(), "Some error Occurred. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });


    }


}

class SoapCall_Amb_SendMessage extends AsyncTask<String, Integer, Long> {

    String Hospital_Name;
    Amb_MessageActivity ama;
    String ambulance_id;
    String P_id;

    String msg;
    String Uname;

    String timestamp;

    MessageAdapter messageAdapter;


    SoapCall_Amb_SendMessage(Amb_MessageActivity ama,String Hospital_Name,String ambulance_id,String P_id,String Uname,String msg,MessageAdapter messageAdapter){

        this.Hospital_Name=Hospital_Name;
        this.ambulance_id=ambulance_id;
        this.P_id=P_id;
        this.ama=ama;
        this.messageAdapter=messageAdapter;
        this.msg=msg;
        this.Uname=Uname;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
            ama.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(ama.getApplicationContext(), "Some error occurred. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            e.printStackTrace();
        }
        return null;
    }

    protected void onPreExecute() {

    }


    protected void onPostExecute(Long result) {

    }

    Document set_message(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/set_Message";
        String METHOD_NAME = "set_Message";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("S_uname", Uname);


        SimpleDateFormat s = new SimpleDateFormat("hh:mm a");
        timestamp=s.format(new Date());

        request.addProperty("S_time", timestamp);

        request.addProperty("R_hosp_name", Hospital_Name);

        request.addProperty("R_amb_id", ambulance_id);

        request.addProperty("R_pid", P_id);

        request.addProperty("msg", msg);

        request.addProperty("Is_amb", "Yes");



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
                = (ConnectivityManager) ama.getSystemService(ama.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    void Check()
    {

        if(!isNetworkAvailable())
        {
            ama.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(ama.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            return;
        }


        final Document doc = set_message();

        ama.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result="null";
                try{
                    result =doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (result.equals("true")) {
                    System.out.println("added");
                    messageAdapter.addMessage(msg, Uname,timestamp,"Yes",MessageAdapter.DIRECTION_OUTGOING);
                }else
                {
                    System.out.println("not added");
                    Toast toast = Toast.makeText(ama.getApplicationContext(), "Some error Occurred. Please try again", Toast.LENGTH_LONG);
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






public class Amb_MessageActivity extends AppCompatActivity {

    private EditText messageBodyField;
    private String messageBody;
    private MessageAdapter messageAdapter;
    private ListView messagesList;


    String uname;
    String P_id;
    String tou;
    String ambulance_id;
    String hospital_name;
    String pt_name;
    String pt_bloodgrp;
    String pt_prob;
    String pt_gender;
    String pt_cond;
    String pt_policecase;

   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amb__message);


        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);


        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent i=getIntent();

        uname=i.getStringExtra("uname");
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

        populateMessageHistory();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
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

    //get previous messages from parse & display
    private void populateMessageHistory() {

        messageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.listMessages);
        messagesList.setAdapter(messageAdapter);

        AsyncTask task = new SoapCall_Amb_GetMessages(Amb_MessageActivity.this,hospital_name,ambulance_id,P_id,uname,messageAdapter).execute();


    }

    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        AsyncTask task = new SoapCall_Amb_SendMessage(Amb_MessageActivity.this,hospital_name,ambulance_id,P_id,uname,messageBody,messageAdapter).execute();
        messageBodyField.setText("");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

