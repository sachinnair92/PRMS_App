package com.voodoo.PRMS_MiBand;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;




public class splashscreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);






                SQLiteDatabase db;
                db = openOrCreateDatabase("Credentials.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

                try {
                    Cursor cursor = db.rawQuery("select * from Credentails", null);


                    String uname;

                    String tou;
                    String ambulance_id;
                    String hospital_name;

                    if (cursor.moveToFirst()) {

                        while (cursor.isAfterLast() == false) {
                            uname = cursor.getString(cursor.getColumnIndex("uname"));
                            tou = cursor.getString(cursor.getColumnIndex("tou"));
                            ambulance_id = cursor.getString(cursor.getColumnIndex("ambulance_id"));
                            hospital_name = cursor.getString(cursor.getColumnIndex("hospital_name"));

                            if (ambulance_id.equals("null")) {
                                Intent intent = new Intent(getApplicationContext(), Doctor_homePage.class);
                                intent.putExtra("tou", tou);
                                intent.putExtra("hospital_name", hospital_name);
                                intent.putExtra("uname", uname);

                                startActivity(intent);
                                finish();
                                return;
                            } else {
                                Intent intent = new Intent(getApplicationContext(), Add_New_Patient_Activity.class);
                                intent.putExtra("tou", tou);
                                intent.putExtra("ambulance_id", ambulance_id);
                                intent.putExtra("hospital_name", hospital_name);
                                intent.putExtra("uname", uname);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }
                    }
                }catch (Exception e) {

                }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(mainIntent);
                finish();
            }
        }, 7000);
    }
}
