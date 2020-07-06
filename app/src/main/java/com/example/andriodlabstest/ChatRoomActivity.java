package com.example.andriodlabstest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    private ArrayList<Message> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    private Button sendBtn, receiveBtn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter( myAdapter = new MyListAdapter());

        sendBtn = findViewById(R.id.sendBtn);
        receiveBtn = findViewById(R.id.receiveBtn);
        editText = findViewById(R.id.chatText);


        sendBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {
                Message addMessage = new Message(message, true);
                elements.add(addMessage);
                myAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } );


        receiveBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {
                Message addMessage = new Message(message, false);
                elements.add(addMessage);
                myAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } );


        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this")

                    .setMessage("The selected row is: " + pos +
                            "\nThe database id: " + id)

                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })

                    .setNegativeButton("No", (click, arg) -> { })

                    .create().show();
            return true;
        });

        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false)  );

    }

    private class MyListAdapter extends BaseAdapter{

        public int getCount() { return elements.size(); }

        public Object getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent)
        {

            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            if(newView == null) {
                if (elements.get(position).isSend())
                    newView = inflater.inflate(R.layout.send_message, parent, false);
                else
                    newView = inflater.inflate(R.layout.receive_message, parent, false);
            }

            TextView tView = newView.findViewById(R.id.textGoesHere);
            tView.setText( elements.get(position).getMessage() );

            return newView;
        }
    }

    private class Message {
        private String message;
        private boolean send;

        Message(String message, boolean send) {
            this.message = message;
            this.send = send;
        }

        public String getMessage () { return message; }

        public boolean isSend () { return send; }
    }
}