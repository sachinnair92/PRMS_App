package com.voodoo.PRMS_MiBand;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

class SoapCall_GetPatients extends AsyncTask<String, Integer, Long> {

    String Hospital_Name;
    Doctor_homePage dhp;
    ProgressDialog dialog;
    String type_of_user;
    String hospital_name;
    String uname;

    SoapCall_GetPatients(Doctor_homePage dhp,String Hospital_Name,String type_of_user,String hospital_name,String uname){
        dialog = new ProgressDialog(dhp);
        // dialog.setTitle("Processing...");
        dialog.setMessage(Html.fromHtml("Retrieving Patient List. Please wait..."));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        this.Hospital_Name=Hospital_Name;
        this.type_of_user=type_of_user;
        this.hospital_name=hospital_name;
        this.uname=uname;
        this.dhp=dhp;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
            dhp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(dhp.getApplicationContext(), "Some error occurred. Please try again", Toast.LENGTH_LONG);
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

    Document get_patient(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/get_Patient_List";
        String METHOD_NAME = "get_Patient_List";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("hospital_name", Hospital_Name);




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
                = (ConnectivityManager) dhp.getSystemService(dhp.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    ArrayList<String> PatientName;
    ArrayList<String> PatientCond;
    ArrayList<String> AmbulanceId;
    ArrayList<String> PatientId;

    void Check()
    {

        if(!isNetworkAvailable())
        {
            dhp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(dhp.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            return;
        }


        final Document doc = get_patient();
        PatientName=new ArrayList<>();
        PatientId=new ArrayList<>();
        PatientCond=new ArrayList<>();
        AmbulanceId=new ArrayList<>();
        dhp.runOnUiThread(new Runnable() {
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
                       // ListView lv=(ListView) dhp.findViewById(R.id.DoctorlistView);
                       // lv.setAdapter(new Doctor_Patient_List_CustomAdapter(dhp, PatientNameList, PatientCondList, AmbulanceList));
                        for(int i=0;i<count;i++)
                        {
                            AmbulanceId.add(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(1).getChildNodes().item(0).getNodeValue()));
                            PatientName.add(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(2).getChildNodes().item(0).getNodeValue()));
                            PatientCond.add(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(6).getChildNodes().item(0).getNodeValue()));
                            PatientId.add(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(3).getChildNodes().item(0).getNodeValue()));
                        }

                        for(int i=0;i<count;i++)
                        {
                            for(int j=0;j<count-1;j++)
                            {
                                if(PatientCond.get(j).equals("Dead") && !PatientCond.get(j+1).equals("Dead"))
                                {
                                    String name_temp=PatientName.get(j);
                                    String amb_temp=AmbulanceId.get(j);
                                    String id_temp=PatientId.get(j);
                                    String cond_temp=PatientCond.get(j);

                                    PatientName.set(j, PatientName.get(j + 1));
                                    AmbulanceId.set(j, AmbulanceId.get(j + 1));
                                    PatientId.set(j, PatientId.get(j + 1));
                                    PatientCond.set(j, PatientCond.get(j + 1));

                                    PatientName.set(j+1,name_temp);
                                    AmbulanceId.set(j+1,amb_temp);
                                    PatientId.set(j+1,id_temp);
                                    PatientCond.set(j+1,cond_temp);

                                }
                            }
                        }

                        ListView lv=(ListView) dhp.findViewById(R.id.DoctorlistView);
                        lv.setAdapter(new Doctor_Patient_List_CustomAdapter(dhp, PatientName, PatientCond, AmbulanceId,PatientId,type_of_user, hospital_name,uname,convertDocumentToString(doc)));
                    }
                }else
                {
                    Toast toast = Toast.makeText(dhp.getApplicationContext(), "Some error Occurred. Please try again", Toast.LENGTH_LONG);
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



public class Doctor_homePage extends AppCompatActivity {

    Context context;


    String type_of_user;
    String hospital_name;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.doctor_toolbar);
        setSupportActionBar(myToolbar);
        Intent intent = getIntent();

        type_of_user=intent.getStringExtra("tou");
        hospital_name=intent.getStringExtra("hospital_name");
        uname=intent.getStringExtra("uname");

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Doctor Homepage</font>"));
        context=this;
        TextView wt = (TextView) findViewById(R.id.Doctor_WelcomeText);

        wt.setText("Welcome "+uname+",");


        AsyncTask task = new SoapCall_GetPatients(Doctor_homePage.this,String.valueOf(hospital_name),type_of_user,hospital_name,uname).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, 0, 0, "Log Out").setIcon(R.drawable.f_logout1)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            if(item.getItemId()==0)
            {
                new AlertDialog.Builder(this)
                        .setTitle("Logout ?")
                        .setMessage("Are you sure you want to Logout ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
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
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

}
