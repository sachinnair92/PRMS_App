package com.voodoo.PRMS_MiBand;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class Show_Info extends AppCompatActivity {

    String type_of_user;
    String hospital_name;
    String ambulance_id;
    String uname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__info);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);


        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'><small>Ambulance Information</small></font>"));
        final Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primarytext_dark), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        type_of_user=intent.getStringExtra("tou");
        hospital_name=intent.getStringExtra("hospital_name");
        ambulance_id=intent.getStringExtra("ambulance_id");
        uname=intent.getStringExtra("uname");
        TextView info_Uname = (TextView) findViewById(R.id.info_Uname);
        info_Uname.setText(Html.fromHtml("<font color='#b3325e'>User Name : </font><small>"+uname+"</small>"));
        TextView info_AmbId = (TextView) findViewById(R.id.info_AmbId);
        info_AmbId.setText(Html.fromHtml("<font color='#b3325e'>Ambulance Id : </font><small>"+ambulance_id+"</small>"));
        TextView info_HospId = (TextView) findViewById(R.id.info_HospId);
        info_HospId.setText(Html.fromHtml("<font color='#b3325e'>Hospital Name : </font><small>"+hospital_name+"</small>"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
            Intent intent = new Intent(getApplicationContext(), Add_New_Patient_Activity.class);
            intent.putExtra("tou", type_of_user);
            intent.putExtra("ambulance_id", ambulance_id);
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
        Intent intent = new Intent(getApplicationContext(), Add_New_Patient_Activity.class);
        intent.putExtra("tou", type_of_user);
        intent.putExtra("ambulance_id", ambulance_id);
        intent.putExtra("hospital_name",hospital_name);
        intent.putExtra("uname",uname);
        startActivity(intent);
        finish();
    }

}
