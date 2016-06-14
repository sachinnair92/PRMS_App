package com.voodoo.PRMS_MiBand;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<String, Integer>> messages;
    private ArrayList<String> messages_time;
    private ArrayList<String> messages_uname;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<String, Integer>>();
        messages_time=new ArrayList<String>();
        messages_uname=new ArrayList<String>();
    }

    public void addMessage(String message,String Uname,String time, int direction) {
        messages.add(new Pair(message, direction));
        messages_time.add(time);
        messages_uname.add(Uname);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);

        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_right;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_left;
            }
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }

        String message = messages.get(i).first;

        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message);

        TextView txtMessage2 = (TextView) convertView.findViewById(R.id.msg_time);
        txtMessage2.setText(messages_time.get(i));

        if (direction == DIRECTION_INCOMING) {
            TextView txtMessage1 = (TextView) convertView.findViewById(R.id.doc_name);
            txtMessage1.setText("Dr. "+messages_uname.get(i).toUpperCase());


        }


        return convertView;
    }
}

