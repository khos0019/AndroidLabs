package com.example.andriodlabstest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {

    private ArrayList<Message> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
    private Button sendBtn, receiveBtn;
    private EditText editText;
    SQLiteDatabase database;
    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String ITEM_ISSEND = "ITEM_ISSEND";
    public static final String ITEM_ID = "ITEM_ID";
    private DetailsFragment dFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter( myAdapter = new MyListAdapter());

        sendBtn = findViewById(R.id.sendBtn);
        receiveBtn = findViewById(R.id.receiveBtn);
        editText = findViewById(R.id.chatText);

        loadDataFromDatabase();

        sendBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {

                ContentValues newRowValues = new ContentValues();

                newRowValues.put(MyOpener.COL_ISSEND, 1); // 1 for true 0 for false
                newRowValues.put(MyOpener.COL_MESSAGE, message);

                long newId = database.insert(MyOpener.TABLE_NAME, null, newRowValues);

                Message addMessage = new Message(message, true, newId);
                elements.add(addMessage);
                myAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } );


        receiveBtn.setOnClickListener((click) -> {
            String message = editText.getText().toString();
            if (!message.equals("")) {

                ContentValues newRowValues = new ContentValues();

                newRowValues.put(MyOpener.COL_ISSEND, 0);
                newRowValues.put(MyOpener.COL_MESSAGE, message);

                long newId = database.insert(MyOpener.TABLE_NAME, null, newRowValues);

                Message addMessage = new Message(message, false, newId);
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
                        deleteMessage(elements.get(pos));
                        elements.remove(pos);
                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();


                        myAdapter.notifyDataSetChanged();
                    })

                    .setNegativeButton("No", (click, arg) -> { })

                    .create().show();
            return true;
        });

        //Checks if the FrameLayout is on a tablet or phone. Null being phone and !null being tablet.
        boolean isTablet = findViewById(R.id.frameLayout) != null;

        myList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, elements.get(position).getMessage() ); //getting message from elements
            dataToPass.putBoolean(ITEM_ISSEND, elements.get(position).getIsSend());
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet) {
                dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            } else { // using Phone device
                Intent nextActivity = new Intent(this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

        });

        SwipeRefreshLayout refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener( () -> refresher.setRefreshing(false)  );

    }

    private class MyListAdapter extends BaseAdapter{

        public int getCount() { return elements.size(); }

        public Object getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return elements.get(position).getId(); }

        public View getView(int position, View old, ViewGroup parent)
        {

            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            if(newView == null) {
                if (elements.get(position).getIsSend())
                    newView = inflater.inflate(R.layout.send_message, parent, false);
                else
                    newView = inflater.inflate(R.layout.receive_message, parent, false);
            }

            TextView tView = newView.findViewById(R.id.textGoesHere);
            tView.setText( elements.get(position).getMessage() );

            return newView;
        }
    }

    private void loadDataFromDatabase() {

        MyOpener dbOpener = new MyOpener(this);
        database = dbOpener.getWritableDatabase();

        String [] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE, MyOpener.COL_ISSEND};

        Cursor results = database.query(false,MyOpener.TABLE_NAME,columns,null,null,null,null,null,null);

        int messageColumnIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
        int isSendColIndex = results.getColumnIndex(MyOpener.COL_ISSEND);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);

        while(results.moveToNext()) {
            String message = results.getString(messageColumnIndex);
            boolean isSend = results.getInt(isSendColIndex) ==1 ? true : false;
            long id = results.getLong(idColIndex);

            elements.add(new Message(message, isSend, id));
        }

        printCursor(results, database.getVersion());
    }

    protected void deleteMessage(Message message) {
        database.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(message.getId())});
    }

    protected void printCursor(Cursor c, int version) {
        Log.d("Version ", Integer.toString(version));
        Log.d("Number of Columns ", Integer.toString(c.getColumnCount()));
        Log.d("Column Names: ", Arrays.toString(c.getColumnNames()));
        Log.d("Number of Rows: ", Integer.toString(c.getCount()));

        c.moveToFirst();
        for (int i=0; i<c.getCount(); i++) {
            Log.d("Results: ", c.getString(0) + " | " +
                    c.getString(1) + " | " +
                    c.getString(2));
            c.moveToNext();
        }
    }

    private class Message {
        private String message;
        private boolean send;
        private long Id;

        Message(String message, boolean send, long Id) {
            this.message = message;
            this.send = send;
            this.Id = Id;
        }

        public String getMessage() {
            return message;
        }

        public boolean getIsSend() {
            return send;
        }

        public long getId() {
            return Id;
        }
    }
}