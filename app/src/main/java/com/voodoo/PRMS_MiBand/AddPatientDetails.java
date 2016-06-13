package com.voodoo.PRMS_MiBand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

class SoapCall_AddPatient extends AsyncTask<String, Integer, Long> {


    ProgressDialog dialog;
    AddPatientDetails apd;

    String pt_name;
    String pt_hosp_name;
    String pt_amb_id;
    String pt_gender;
    String pt_bloodgrp;
    String pt_cond;
    String pt_prob;
    String pt_policecase;
    String uname;
    String type_of_user;


    SoapCall_AddPatient(AddPatientDetails apd,String type_of_user, String uname,String pt_hosp_name,String pt_amb_id,String pt_name,String pt_gender ,String pt_bloodgrp,String pt_cond,String pt_prob,String pt_policecase){

       dialog = new ProgressDialog(apd);
        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        this.type_of_user=type_of_user;
        this.uname=uname;
        this.pt_hosp_name=pt_hosp_name;
        this.pt_amb_id=pt_amb_id;
        this.pt_name=pt_name;
        this.pt_gender=pt_gender;
        this.pt_bloodgrp=pt_bloodgrp;
        this.pt_cond=pt_cond;
        this.pt_prob=pt_prob;
        this.pt_policecase=pt_policecase;
        this.apd=apd;
    }

    @Override
    protected Long doInBackground(String... params) {
        try{
            Check();
        }
        catch(Exception e){
            apd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(apd.getApplicationContext(), "Some error occurred. Please try again", Toast.LENGTH_LONG);
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

    Document add_patient(){

        String NAMESPACE = "http://service.webservers.voodoo.com/";
        String URL = "http://prms-jaxwebserver.herokuapp.com/api?wsdl";
        String SOAP_ACTION = "http://prms-jaxwebserver.herokuapp.com/api/add_new_patient";
        String METHOD_NAME = "add_new_patient";


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("hospital_name", pt_hosp_name);
        request.addProperty("ambulance_id", pt_amb_id);
        request.addProperty("p_name", pt_name);
        request.addProperty("gender", pt_gender);
        request.addProperty("blood_grp", pt_bloodgrp);
        request.addProperty("condition", pt_cond);
        request.addProperty("problem", pt_prob);
        request.addProperty("police_case", pt_policecase);
        request.addProperty("is_enabled", "Yes");



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
                = (ConnectivityManager) apd.getSystemService(apd.getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void Check()
    {

        if(!isNetworkAvailable())
        {
            apd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(apd.getApplicationContext(), "Please check ur Internet Connection and try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            return;
        }

        if(pt_name.equals(""))
        {
          pt_name="null";
        }

        if(pt_bloodgrp.equals(""))
        {
            pt_bloodgrp="null";
        }

        if(pt_prob.equals(""))
        {
            pt_prob="null";
        }


        final Document doc = add_patient();
        apd.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result="null";
                try{
                   result =doc.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (result.equals("true")) {
                    String P_id =doc.getElementsByTagName("P_id").item(0).getChildNodes().item(0).getNodeValue();
                    Toast toast = Toast.makeText(apd.getApplicationContext(), "Patient "+P_id+" Added", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(apd.getApplicationContext(), PRMS_MainActivity.class);
                    intent.putExtra("P_id", P_id);
                    intent.putExtra("tou", type_of_user);
                    intent.putExtra("ambulance_id", pt_amb_id);
                    intent.putExtra("hospital_name",pt_hosp_name);
                    intent.putExtra("uname",uname);
                    intent.putExtra("pt_name",pt_name);
                    intent.putExtra("pt_bloodgrp",pt_bloodgrp);
                    intent.putExtra("pt_prob",pt_prob);
                    intent.putExtra("pt_gender",pt_gender);
                    intent.putExtra("pt_cond",pt_cond);
                    intent.putExtra("pt_policecase",pt_policecase);
                    apd.startActivity(intent);
                    apd.finish();
                }else
                {
                    Toast toast = Toast.makeText(apd.getApplicationContext(), "Some error Occurred. Please try again", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });


    }

}

public class AddPatientDetails extends AppCompatActivity {

    String pt_hosp_name;
    String pt_amb_id;
    String type_of_user;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_patient_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>New Patient</font>"));



        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<String> list;
        final Spinner spinner = (Spinner) findViewById(R.id.Conditionspinner);

        list = new ArrayList<String>();
        list.add("Undetermined");
        list.add("Stable");
        list.add("Serious");
        list.add("Critical");
        list.add("Dead");


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





        final Spinner spinner1 = (Spinner) findViewById(R.id.pt_bloodgrp_spinner);

        list = new ArrayList<String>();
        list.add("Not Known");
        list.add("O+");
        list.add("O-");
        list.add("A+");
        list.add("A-");
        list.add("B+");
        list.add("B-");
        list.add("AB+");
        list.add("AB-");


        ArrayAdapter<String> adapter1 =
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
        spinner1.setAdapter(adapter1);


        Intent i=getIntent();
        pt_hosp_name=i.getStringExtra("hospital_name");
        pt_amb_id=i.getStringExtra("ambulance_id");
        type_of_user=i.getStringExtra("tou");
        uname=i.getStringExtra("uname");


        final EditText pt_prob1 = (EditText) findViewById(R.id.pt_prob);
        pt_prob1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != pt_prob1.getLayout() && pt_prob1.getLayout().getLineCount() > 3) {
                    pt_prob1.getText().delete(pt_prob1.getText().length() - 1, pt_prob1.getText().length());
                }
            }
        });

        final EditText pt_name1 = (EditText) findViewById(R.id.pt_name);


        final RadioGroup pt_radioSex1 = (RadioGroup) findViewById(R.id.pt_radioSex);

        final RadioGroup pt_policecasegrp1 = (RadioGroup) findViewById(R.id.pt_policecasegrp);






        
        Button butt = (Button) findViewById(R.id.AddPatientbutton);

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioButton rb1 = (RadioButton) findViewById(pt_radioSex1.getCheckedRadioButtonId());
                final RadioButton rb2 = (RadioButton) findViewById(pt_policecasegrp1.getCheckedRadioButtonId());
                final String pt_name=String.valueOf(pt_name1.getText());
                final String pt_bloodgrp=spinner1.getSelectedItem().toString();
                final String pt_prob=String.valueOf(pt_prob1.getText());
                final String pt_gender=String.valueOf(rb1.getText());
                final String pt_policecase=String.valueOf(rb2.getText());
                final String pt_cond = spinner.getSelectedItem().toString();

                  AsyncTask task = new SoapCall_AddPatient(AddPatientDetails.this,String.valueOf(type_of_user),String.valueOf(uname),String.valueOf(pt_hosp_name),String.valueOf(pt_amb_id),String.valueOf(pt_name),String.valueOf(pt_gender) ,String.valueOf(pt_bloodgrp),String.valueOf(pt_cond),String.valueOf(pt_prob),String.valueOf(pt_policecase)).execute();
                //AsyncTask task = new SoapCall_AddPatient().execute();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), Add_New_Patient_Activity.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("ambulance_id", pt_amb_id);
                intent.putExtra("hospital_name",pt_hosp_name);
                intent.putExtra("uname",uname);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Add_New_Patient_Activity.class);
        intent.putExtra("tou", type_of_user);
        intent.putExtra("ambulance_id", pt_amb_id);
        intent.putExtra("hospital_name",pt_hosp_name);
        intent.putExtra("uname",uname);
        startActivity(intent);
        finish();
    }
}
