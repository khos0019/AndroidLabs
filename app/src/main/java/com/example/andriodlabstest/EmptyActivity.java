package com.example.andriodlabstest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        // getting the Bundle from ChatRoomActivity
        Bundle dataToPass = getIntent().getExtras();

        // Getting the transaction details from ChatRoom Activity
        DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
        dFragment.setArguments( dataToPass ); //pass it a bundle for information
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, dFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment.

    }
}