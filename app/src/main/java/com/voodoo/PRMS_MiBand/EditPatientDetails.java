package com.voodoo.PRMS_MiBand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

 

class SoapCall_EditPatient extends AsyncTask<String, Integer, Long> {


    ProgressDialog dialog;
    EditPatientDetails apd;

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


    SoapCall_EditPatient(EditPatientDetails apd,String type_of_user, String uname,String pt_hosp_name,String pt_amb_id,String edit_pt_name,String edit_pt_id,String pt_gender ,String edit_pt_bloodgrp,String pt_cond,String edit_pt_prob,String pt_policecase){

        dialog = new ProgressDialog(apd);
        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
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
                    Toast toast = Toast.makeText(apd.getApplicationContext(), "Patient Data Updated", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(apd.getApplicationContext(), PRMS_MainActivity.class);
                    intent.putExtra("P_id", edit_pt_id);
                    intent.putExtra("tou", type_of_user);
                    intent.putExtra("ambulance_id", pt_amb_id);
                    intent.putExtra("hospital_name",pt_hosp_name);
                    intent.putExtra("uname",uname);
                    intent.putExtra("pt_name",edit_pt_name);
                    intent.putExtra("pt_bloodgrp",edit_pt_bloodgrp);
                    intent.putExtra("pt_prob",edit_pt_prob);
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

public class EditPatientDetails extends AppCompatActivity {




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
        setContentView(R.layout.activity_edit_patient_details);


        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Edit Patient</font>"));






        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<String> list;
        final Spinner spinner = (Spinner) findViewById(R.id.edit_Conditionspinner);
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

        final Spinner spinner1 = (Spinner) findViewById(R.id.edit_pt_bloodgrp_spinner);

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



        final EditText edit_pt_prob1 = (EditText) findViewById(R.id.edit_pt_prob);
        edit_pt_prob1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != edit_pt_prob1.getLayout() && edit_pt_prob1.getLayout().getLineCount() > 3) {
                    edit_pt_prob1.getText().delete(edit_pt_prob1.getText().length() - 1, edit_pt_prob1.getText().length());
                }
            }
        });

        final EditText edit_pt_name1 = (EditText) findViewById(R.id.edit_pt_name);



        final RadioGroup edit_pt_radioSex1 = (RadioGroup) findViewById(R.id.edit_pt_radioSex);


        final RadioGroup edit_pt_policecasegrp1 = (RadioGroup) findViewById(R.id.edit_pt_policecasegrp);



        if(!pt_name.equals("null"))
        edit_pt_name1.setText(pt_name);


        if(!pt_prob.equals("null"))
        edit_pt_prob1.setText(pt_prob);

        if(pt_gender.equals("male"))
        {
            RadioButton rb3 = (RadioButton) findViewById(R.id.edit_pt_radioMale);
            rb3.setChecked(true);
        }
        else{
            RadioButton rb3 = (RadioButton) findViewById(R.id.edit_pt_radioFemale);
            rb3.setChecked(true);
        }

        if(pt_policecase.equals("Yes"))
        {
            RadioButton rb3 = (RadioButton) findViewById(R.id.edit_pt_YesButton);
            rb3.setChecked(true);
        }
        else{
            RadioButton rb3 = (RadioButton) findViewById(R.id.edit_pt_NoButton);
            rb3.setChecked(true);
        }

        spinner.setSelection(adapter.getPosition(pt_cond));
        spinner1.setSelection(adapter1.getPosition(pt_bloodgrp));

        Button butt = (Button) findViewById(R.id.EditPatientbutton);

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioButton rb1 = (RadioButton) findViewById(edit_pt_radioSex1.getCheckedRadioButtonId());
                final RadioButton rb2 = (RadioButton) findViewById(edit_pt_policecasegrp1.getCheckedRadioButtonId());
                final String edit_pt_name=String.valueOf(edit_pt_name1.getText());
                final String edit_pt_bloodgrp= spinner1.getSelectedItem().toString();
                final String edit_pt_prob=String.valueOf(edit_pt_prob1.getText());
                final String edit_pt_gender=String.valueOf(rb1.getText());
                final String edit_pt_policecase=String.valueOf(rb2.getText());
                final String edit_pt_cond = spinner.getSelectedItem().toString();



                if(edit_pt_name.equals(pt_name) && edit_pt_gender.equals(pt_gender) && edit_pt_bloodgrp.equals(pt_bloodgrp) && edit_pt_cond.equals(pt_cond) && edit_pt_prob.equals(pt_prob) && edit_pt_policecase.equals(pt_policecase)){
                    Toast toast = Toast.makeText(getApplicationContext(), "Edit values before Saving", Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    AsyncTask task = new SoapCall_EditPatient(EditPatientDetails.this,String.valueOf(tou),String.valueOf(uname),String.valueOf(hospital_name),String.valueOf(ambulance_id),String.valueOf(edit_pt_name),String.valueOf(P_id),String.valueOf(edit_pt_gender) ,String.valueOf(edit_pt_bloodgrp),String.valueOf(edit_pt_cond),String.valueOf(edit_pt_prob),String.valueOf(edit_pt_policecase)).execute();
                }
                //AsyncTask task = new SoapCall_AddPatient().execute();
            }
        });

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
}
