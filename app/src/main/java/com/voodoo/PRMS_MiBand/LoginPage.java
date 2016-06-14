package com.voodoo.PRMS_MiBand;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


class SoapCall_Login extends AsyncTask<String, Integer, Long> {

    String uname;
    String pass;
    TextView Lerror;
    LoginPage lp;
    ProgressDialog dialog;

    SoapCall_Login(LoginPage lp,String uname,String pass,TextView Lerror){
        dialog = new ProgressDialog(lp);
       // dialog.setTitle("Processing...");
        dialog.setMessage(Html.fromHtml("Loading. Please wait..."));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        this.uname=uname;
        this.pass=pass;
        this.Lerror=Lerror;
        this.lp=lp;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check(uname,pass);
       }
        catch(Exception e){
            lp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Lerror.setText("Some error occurred. Please try Again");
                    Lerror.setVisibility(View.VISIBLE);
                }
            });
        }
        return null;
    }

    protected void onPreExecute() {
        dialog.show();
    }


    protected void onPostExecute(Long result) {
        dialog.dismiss();
    }

    Document Validate(String Username, String Password){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/validate_user";
        String METHOD_NAME = "validate_user";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("username", Username);
        request.addProperty("password", Password);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            String resp= String .valueOf(envelope.getResponse());
            Document doc=convertStringToDocument(resp);

            String value=doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();


            System.out.println("Response is " + resp);
            return doc;

            //System.out.println("resp is " + s);
            // SoapObject resultsString = (SoapObject) envelope.getResponse();
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
                = (ConnectivityManager) lp.getSystemService(lp.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void Check(final String Uname,String Pass)
    {

        if(Uname.contains(" ") || Pass.contains(" "))
        {
            lp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Lerror.setText("Username or Password cannot contain spaces");
                    Lerror.setVisibility(View.VISIBLE);
                }
            });

            return;
        }
        if(Uname.equals("") || Pass.equals(""))
        {
            lp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Lerror.setText("Username or Password cannot be empty");
                    Lerror.setVisibility(View.VISIBLE);
                }
            });


        }else {

            if(!isNetworkAvailable())
            {
                lp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(lp.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                return;
            }

            final Document doc = Validate(Uname, Pass);
            if(doc==null)
            {
                lp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {Lerror.setText("Some error occurred. Please try Again");
                        Lerror.setVisibility(View.VISIBLE);
                    }});
                return;
            }

            final String result=doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();

            lp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Lerror.setVisibility(View.INVISIBLE);
                   // Toast toast = Toast.makeText(lp.getApplicationContext(), "Result is " + result, Toast.LENGTH_LONG);
                   // toast.show();
                    if(result.equals("true"))
                    {
                        final String type_of_user=doc.getElementsByTagName("type_of_user").item(0).getChildNodes().item(0).getNodeValue();
                        final String hospital_name=doc.getElementsByTagName("hospital_name").item(0).getChildNodes().item(0).getNodeValue();

                        if(type_of_user.equals("Ambulance Staff")) {
                            final String ambulance_id=doc.getElementsByTagName("ambulance_id").item(0).getChildNodes().item(0).getNodeValue();
                            Intent intent = new Intent(lp.getApplicationContext(), Add_New_Patient_Activity.class);
                            intent.putExtra("tou", type_of_user);
                            intent.putExtra("ambulance_id", ambulance_id);
                            intent.putExtra("hospital_name",hospital_name);
                            intent.putExtra("uname", Uname);


                            SQLiteDatabase db;
                            db = lp.openOrCreateDatabase("Credentials.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                            db.execSQL("DROP TABLE IF EXISTS Credentails");
                            try {
                                final String CREATE_TABLE_CONTAIN = "CREATE TABLE IF NOT EXISTS Credentails ("
                                        + "uname TEXT,"
                                        + "tou TEXT,"
                                        + "ambulance_id TEXT,"
                                        + "hospital_name TEXT);";
                                db.execSQL(CREATE_TABLE_CONTAIN);
                                String sql = "INSERT or replace INTO Credentails (uname, tou, ambulance_id, hospital_name) VALUES('"+uname+"','"+type_of_user+"','"+ambulance_id+"','"+hospital_name+"')" ;
                                db.execSQL(sql);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            lp.startActivity(intent);
                            lp.finish();
                        }
                        else if(type_of_user.equals("Doctor")) {
                            Intent intent = new Intent(lp.getApplicationContext(), Doctor_homePage.class);
                            intent.putExtra("tou", type_of_user);
                            intent.putExtra("hospital_name",hospital_name);
                            intent.putExtra("uname", Uname);

                            SQLiteDatabase db;
                            db = lp.openOrCreateDatabase("Credentials.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                            db.execSQL("DROP TABLE IF EXISTS Credentails");
                            try {
                                final String CREATE_TABLE_CONTAIN = "CREATE TABLE IF NOT EXISTS Credentails ("
                                        + "uname TEXT,"
                                        + "tou TEXT,"
                                        + "ambulance_id TEXT,"
                                        + "hospital_name TEXT);";
                                db.execSQL(CREATE_TABLE_CONTAIN);
                                String sql = "INSERT or replace INTO Credentails (uname, tou, ambulance_id, hospital_name) VALUES('"+uname+"','"+type_of_user+"','null','"+hospital_name+"')" ;
                                db.execSQL(sql);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                            lp.startActivity(intent);
                            lp.finish();
                        }else
                        {
                            Lerror.setText("Some error occurred. Please try Again");
                            Lerror.setVisibility(View.VISIBLE);
                        }



                    }else if(result.equals("false"))
                    {
                        Lerror.setText("Invalid Username or Password");
                        Lerror.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Lerror.setText("Some error occurred. Please try Again");
                        Lerror.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

}



public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        Button btn = (Button) findViewById(R.id.Loginbutton);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final EditText Uname = (EditText) findViewById(R.id.editUsername);
                final EditText Pass = (EditText) findViewById(R.id.editPassword);
                final TextView Lerror = (TextView) findViewById(R.id.LoginErrorText);
                AsyncTask task = new SoapCall_Login(LoginPage.this, Uname.getText().toString(), Pass.getText().toString(), Lerror).execute();



            }
        });

        Button btn1 = (Button) findViewById(R.id.RegisterButton);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterPage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
