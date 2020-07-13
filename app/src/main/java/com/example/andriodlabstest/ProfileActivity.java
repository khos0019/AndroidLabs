package com.example.andriodlabstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton mImageButton;
    private EditText emailText;
    private Button btn, weatherBtn;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String ACTIVITY_NAME = "PROFILE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImageButton = findViewById(R.id.imageBtn);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dispatchTakePictureIntent();
            }
        });

        Log.e(ACTIVITY_NAME, "In function: onCreate()");

        Intent fromMain = getIntent();
        String email = fromMain.getStringExtra("EMAIL");

        emailText = findViewById(R.id.email);

        emailText.setText(email);

        btn = findViewById(R.id.chatButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToChat =  new Intent(ProfileActivity.this, ChatRoomActivity.class);
                startActivity(goToChat);
            }
        });

        weatherBtn = findViewById(R.id.weatherButton);

        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToChat =  new Intent(ProfileActivity.this, WeatherForecast.class);
                startActivity(goToChat);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.e(ACTIVITY_NAME, "In function: onStart()");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e(ACTIVITY_NAME, "In function: onResume()");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.e(ACTIVITY_NAME, "In function: onPause()");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e(ACTIVITY_NAME, "In function: onStop()");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.e(ACTIVITY_NAME, "In function: onDestroy()");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageButton.setImageBitmap(imageBitmap);
        }

        Log.e(ACTIVITY_NAME, "In function: onActivityResult()");

    }


}