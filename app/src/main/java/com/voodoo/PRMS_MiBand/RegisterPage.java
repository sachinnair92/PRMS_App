package com.voodoo.PRMS_MiBand;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class SoapCall_Register extends AsyncTask<String, Integer, Long> {


    String Uname ;
    String Pass;
    String RPass;
    String tou_spinner ;
    String HospitalSpinner;
    TextView Rerror;
    ProgressDialog dialog;
    RegisterPage rp;


    SoapCall_Register(RegisterPage rp,String Uname,String Pass,String RPass,String tou_spinner,String HospitalSpinner,TextView Rerror){
        dialog = new ProgressDialog(rp);
        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        this.Uname=Uname;
        this.Pass=Pass;
        this.RPass=RPass;
        this.tou_spinner=tou_spinner;
        this.HospitalSpinner=HospitalSpinner;
        this.Rerror=Rerror;
        this.rp=rp;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
            rp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Rerror.setText("Some error occurred. Please try Again");
                    Rerror.setVisibility(View.VISIBLE);
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

    String add_user(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/register_user";
        String METHOD_NAME = "register_user";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("username", Uname);
        request.addProperty("password", Pass);
        request.addProperty("hospital_name", HospitalSpinner);
        request.addProperty("type_of_user", tou_spinner);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            String resp= String .valueOf(envelope.getResponse());
            Document doc=convertStringToDocument(resp);

            String value=doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();

            System.out.println("value is " + value);
            return value;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
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
                = (ConnectivityManager) rp.getSystemService(rp.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void Check()
    {

        rp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Rerror.setVisibility(View.INVISIBLE);
            }
        });

        System.out.println("tou is " + tou_spinner);


        if(Uname.equals("") || Pass.equals("") || RPass.equals("")|| tou_spinner.equals("")|| HospitalSpinner.equals(""))
        {
            rp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Rerror.setText("Please enter all the details");
                    Rerror.setVisibility(View.VISIBLE);
                }
            });


        }else if(Uname.contains(" ") || Pass.contains(" "))
        {
            rp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Rerror.setText("Username or Password cannot contain spaces");
                    Rerror.setVisibility(View.VISIBLE);
                }
            });

            return;
        }else
         {
            if(Pass.equals(RPass)) {

                if(!isNetworkAvailable())
                {
                    rp.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(rp.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    return;
                }

                final String result = add_user();
                rp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Rerror.setVisibility(View.INVISIBLE);

                        if (result.equals("true")) {
                            Toast toast = Toast.makeText(rp.getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG);
                            toast.show();
                            Intent intent = new Intent(rp.getApplicationContext(), LoginPage.class);
                            rp.startActivity(intent);
                            rp.finish();
                        }else if(result.equals("false"))
                        {
                            Rerror.setText("The Username is already Taken.");
                            Rerror.setVisibility(View.VISIBLE);
                        }else
                        {
                            Rerror.setText("Some error occurred. Please try Again");
                            Rerror.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
            else {
                rp.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Rerror.setText("Passwords dont match");
                        Rerror.setVisibility(View.VISIBLE);
                    }
                });
            }

        }
    }

}


public class RegisterPage extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        List<String> list;
        Spinner spinner = (Spinner) findViewById(R.id.tou_spinner);
        list = new ArrayList<String>();
        list.add("Ambulance Staff");
        list.add("Doctor");


        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list)
                {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        ((TextView) v).setTextSize(16);
                        ((TextView) v).setTextColor(Color.parseColor("#b3325e"));

                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {

                        View v = super.getDropDownView(position, convertView, parent);
                        // v.setBackgroundResource(R.drawable.spinner_bg);

                        ViewGroup.LayoutParams p = v.getLayoutParams();

                        p.height=200;
                        v.setLayoutParams(p);
                        ((TextView) v).setTextColor(Color.parseColor("#b3325e"));

                        //((TextView) v).setTypeface(fontStyle);
                        ((TextView) v).setGravity(Gravity.CENTER);

                        return v;
                    }
                };

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        List<String> list1;
        Spinner spinner1 = (Spinner) findViewById(R.id.HospitalSpinner);
        list1 = new ArrayList<String>();
        list1.add("Manipal Hospital");
        list1.add("Apollo Hospital");
        list1.add("M.S.Ramaiah Memorial Hospital");
        list1.add("Fortis Hospital");
        list1.add("Chaitanya Hospital");
        list1.add("Narayana Nethralya");
        list1.add("Gangothri Hospital");


        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list1)
                {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        ((TextView) v).setTextSize(16);
                        ((TextView) v).setTextColor(Color.parseColor("#b3325e"));

                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {

                        View v = super.getDropDownView(position, convertView, parent);
                        // v.setBackgroundResource(R.drawable.spinner_bg);

                        ViewGroup.LayoutParams p = v.getLayoutParams();

                        p.height=200;
                        v.setLayoutParams(p);
                        ((TextView) v).setTextColor(Color.parseColor("#b3325e"));

                        //((TextView) v).setTypeface(fontStyle);
                                ((TextView) v).setGravity(Gravity.CENTER);

                        return v;
                    }
                };

        spinner1.setAdapter(adapter1);

        ImageButton btn1 = (ImageButton) findViewById(R.id.BackButton);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        Button btn2 = (Button) findViewById(R.id.DoRegisterButton);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText Uname = (EditText) findViewById(R.id.R_Username);
                final EditText Pass = (EditText) findViewById(R.id.R_Password);
                final EditText RPass = (EditText) findViewById(R.id.R_RePassword);
                Spinner spinner1=(Spinner) findViewById(R.id.tou_spinner);
                String tou_spinner = spinner1.getSelectedItem().toString();
                Spinner spinner2=(Spinner) findViewById(R.id.HospitalSpinner);
                String HospitalSpinner = spinner2.getSelectedItem().toString();
                final TextView Rerror = (TextView) findViewById(R.id.RegisterErrorText);
                AsyncTask task = new SoapCall_Register(RegisterPage.this, Uname.getText().toString(), Pass.getText().toString(),RPass.getText().toString(),tou_spinner,HospitalSpinner,Rerror).execute();

            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();
    }
}
