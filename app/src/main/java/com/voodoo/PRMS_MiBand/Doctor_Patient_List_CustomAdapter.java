package com.voodoo.PRMS_MiBand;

/**
 * Created by voodoo on 6/6/16.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Doctor_Patient_List_CustomAdapter extends BaseAdapter{

    ArrayList<String> PatientName;
    ArrayList<String> PatientId;
    ArrayList<String> PatientCond;
    ArrayList<String> AmbulanceId;
    String type_of_user;
    String hospital_name;
    String uname;
    String string_doc;
    Context context;
    Doctor_homePage dh;

    int Is_empty=0;

    private static LayoutInflater inflater=null;
    public Doctor_Patient_List_CustomAdapter(Doctor_homePage mainActivity, ArrayList<String> PatientName,ArrayList<String> PatientCond, ArrayList<String> AmbulanceId,ArrayList<String> PatientId,String type_of_user,String hospital_name,String uname,String string_doc) {
        // TODO Auto-generated constructor stub
        this.PatientName=PatientName;
        this.PatientCond=PatientCond;
        this.AmbulanceId=AmbulanceId;
        this.type_of_user=type_of_user;
        this.hospital_name=hospital_name;
        this.string_doc=string_doc;
        this.uname=uname;
        this.PatientId=PatientId;
        context=mainActivity;
        this.dh=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Is_empty=0;
    }




    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PatientName.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView name_tv;
        TextView cond_tv;
        TextView amb_tv;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.patient_list, null);
        holder.name_tv=(TextView) rowView.findViewById(R.id.list_patientname);
        holder.name_tv.setText(PatientName.get(position));
        holder.cond_tv=(TextView) rowView.findViewById(R.id.list_patientcond);
        holder.cond_tv.setText(PatientCond.get(position));
        holder.amb_tv=(TextView) rowView.findViewById(R.id.list_ambname);
        holder.amb_tv.setText(AmbulanceId.get(position));
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(dh.getApplicationContext(),Show_Selected_Patient_Details.class);
                intent.putExtra("tou", type_of_user);
                intent.putExtra("hospital_name",hospital_name);
                intent.putExtra("uname", uname);
                intent.putExtra("P_id",PatientId.get(position));
                intent.putExtra("string_doc",string_doc);
                dh.startActivity(intent);
                dh.finish();
            }
        });
        return rowView;
    }



}