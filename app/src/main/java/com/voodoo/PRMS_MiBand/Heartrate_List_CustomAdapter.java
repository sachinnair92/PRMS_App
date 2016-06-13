package com.voodoo.PRMS_MiBand;

/**
 * Created by voodoo on 6/6/16.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;

public class Heartrate_List_CustomAdapter extends BaseAdapter{

    ArrayList<String> HeartrateList;
    ArrayList<String> TimeStamp_List;
    Context context;
   ShowHeartRate shr;
    private static LayoutInflater inflater=null;
    public Heartrate_List_CustomAdapter(ShowHeartRate mainActivity, ArrayList<String> HeartrateList,ArrayList<String> TimeStamp_List) {
        // TODO Auto-generated constructor stub
        this.HeartrateList=HeartrateList;
        this.TimeStamp_List= TimeStamp_List;
        context=mainActivity;
        this.shr=mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return HeartrateList.size();
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
        TextView timestamp_list_text;
        TextView cond_list_text;
        TextView heartrate_text;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.heartrate_list, null);
        holder.heartrate_text=(TextView) rowView.findViewById(R.id.heartrate_list_text);
        holder.cond_list_text=(TextView) rowView.findViewById(R.id.cond_list_text);
        holder.timestamp_list_text=(TextView) rowView.findViewById(R.id.timestamp_list_text);

        if(Integer.parseInt(HeartrateList.get(position))<60)
        {
            holder.cond_list_text.setText("Low");
        }else if(Integer.parseInt(HeartrateList.get(position))>110)
        {
            holder.cond_list_text.setText("High");
        }

        if(Integer.parseInt(HeartrateList.get(position))==0)
        {
            holder.heartrate_text.setText(" "+HeartrateList.get(position)+" ");
        }else if(Integer.parseInt(HeartrateList.get(position))<99)
        {
            holder.heartrate_text.setText("0"+HeartrateList.get(position));
        }else {
            holder.heartrate_text.setText(HeartrateList.get(position));
        }
        holder.timestamp_list_text.setText(TimeStamp_List.get(position));


        return rowView;
    }



}