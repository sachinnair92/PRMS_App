package com.voodoo.PRMS_MiBand;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class Add_New_Patient_Activity extends AppCompatActivity {

    String type_of_user;
    String hospital_name;
    String ambulance_id;
    String uname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__new__patient);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'><small>Ambulance Staff Homepage</small></font>"));

        Intent intent = getIntent();

        type_of_user=intent.getStringExtra("tou");
        hospital_name=intent.getStringExtra("hospital_name");
        ambulance_id=intent.getStringExtra("ambulance_id");
        uname=intent.getStringExtra("uname");

        TextView wt = (TextView) findViewById(R.id.Welcome_Text);
        wt.setText("Welcome, "+uname);

        ImageView iv=(ImageView) findViewById(R.id.addPatientImage);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPatientDetails.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("ambulance_id", ambulance_id);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname",uname);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, 0, 0, "Information").setIcon(R.drawable.f_info)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 0, "Log Out").setIcon(R.drawable.f_logout1)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            if(item.getItemId()==0)
            {
                Intent intent = new Intent(getApplicationContext(), Show_Info.class);

                intent.putExtra("tou", type_of_user);
                intent.putExtra("ambulance_id", ambulance_id);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname",uname);

                startActivity(intent);
                finish();
            }
            if(item.getItemId()==1)
            {
                new AlertDialog.Builder(this)
                        .setTitle("Logout ?")
                        .setMessage("Are you sure you want to Logout ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), LoginPage.class);

                                SQLiteDatabase db;
                                db = openOrCreateDatabase("Credentials.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                                db.execSQL("DROP TABLE IF EXISTS Credentails");
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
/*
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

*/


}



