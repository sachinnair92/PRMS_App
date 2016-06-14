package com.voodoo.PRMS_MiBand;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Show_Selected_Patient_Details extends AppCompatActivity {


    String type_of_user;
    String hospital_name;
    String uname;
    String P_id;
    String string_doc;
    String ambulance_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__selected__patient__details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.show_patient_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Patient Details </font>"));

        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();

        type_of_user=intent.getStringExtra("tou");
        hospital_name=intent.getStringExtra("hospital_name");
        uname=intent.getStringExtra("uname");
        P_id=intent.getStringExtra("P_id");
        string_doc=intent.getStringExtra("string_doc");

        Document doc=convertStringToDocument(string_doc);

        System.out.println("test " +String.valueOf(doc.getElementsByTagName("Patient_0" ).item(0).getChildNodes().item(3).getChildNodes().item(0).getNodeValue()));

        int count =Integer.parseInt(doc.getElementsByTagName("count").item(0).getChildNodes().item(0).getNodeValue());

        ImageView hrt_img=(ImageView) findViewById(R.id.measureheartrate_image);

        ImageView send_img=(ImageView) findViewById(R.id.sendmessage_image);

        System.out.println("P_id is " + P_id);
        ambulance_id=null;
        for(int i=0;i<count;i++)
        {
            String pid=String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(3).getChildNodes().item(0).getNodeValue());
            System.out.println("pid is "+pid);
            if(pid.equals(P_id))
            {
                TextView patient_name=(TextView) findViewById(R.id.show_patient_name);
                TextView patient_id=(TextView) findViewById(R.id.show_patient_id);
                TextView gender=(TextView) findViewById(R.id.show_gender);
                TextView bloodgrp=(TextView) findViewById(R.id.show_bloodgrp);
                TextView show_condition=(TextView) findViewById(R.id.show_condition);
                TextView policecase=(TextView) findViewById(R.id.show_policecase);
                TextView problem=(TextView) findViewById(R.id.show_problem);
                ambulance_id=doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();

                patient_name.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(2).getChildNodes().item(0).getNodeValue()));
                patient_id.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(3).getChildNodes().item(0).getNodeValue()));
                gender.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(4).getChildNodes().item(0).getNodeValue()));
                bloodgrp.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(5).getChildNodes().item(0).getNodeValue()));
                show_condition.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(6).getChildNodes().item(0).getNodeValue()));
                policecase.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(8).getChildNodes().item(0).getNodeValue()));
                problem.setText(String.valueOf(doc.getElementsByTagName("Patient_" + i).item(0).getChildNodes().item(7).getChildNodes().item(0).getNodeValue()));
            }
        }

        hrt_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ShowHeartRate.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname", uname);
                intent.putExtra("P_id",P_id);
                intent.putExtra("string_doc",string_doc);
                intent.putExtra("ambulance_id",ambulance_id);
                startActivity(intent);
                finish();

            }
        });

        send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MessageActivity.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname", uname);
                intent.putExtra("P_id",P_id);
                intent.putExtra("string_doc",string_doc);
                intent.putExtra("ambulance_id",ambulance_id);
                startActivity(intent);
                finish();
            }
        });

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), Doctor_homePage.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname",uname);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Doctor_homePage.class);
        intent.putExtra("tou", type_of_user);
        intent.putExtra("hospital_name", hospital_name);
        intent.putExtra("uname", uname);
        startActivity(intent);
        finish();
    }

}
