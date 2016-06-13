package com.voodoo.PRMS_MiBand;



import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MessageActivity extends AppCompatActivity {

    private EditText messageBodyField;
    private String messageBody;
    private MessageAdapter messageAdapter;
    private ListView messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    //get previous messages from parse & display
    private void populateMessageHistory() {


        messageAdapter.addMessage("Heloooz", MessageAdapter.DIRECTION_OUTGOING);

        messageAdapter.addMessage("voodoo", MessageAdapter.DIRECTION_INCOMING);


    }

    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        messageAdapter.addMessage(messageBody, MessageAdapter.DIRECTION_OUTGOING);
        messageBodyField.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
