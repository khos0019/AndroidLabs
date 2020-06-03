package com.example.andriodlabstest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button button;
    private LinearLayout linearLayout;
    private CheckBox checkBox;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_grid);

        button = findViewById(R.id.Button);
        button.setOnClickListener(this);

        linearLayout = findViewById(R.id.linearLayout);
        checkBox = findViewById(R.id.checkBox);
        aSwitch = findViewById(R.id.Switch);

        checkBox.setOnCheckedChangeListener(this);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, getResources().getString(R.string.toast_message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if(isChecked){
            Snackbar snackbar = Snackbar.make(linearLayout, getResources().getString(R.string.switch_on), Snackbar.LENGTH_LONG)
                    .setAction("UNDO", click -> buttonView.setChecked(!isChecked));

            snackbar.show();

        } else {
            Snackbar snackbar = Snackbar.make(linearLayout, getResources().getString(R.string.switch_off), Snackbar.LENGTH_LONG)
                    .setAction("UNDO", click -> buttonView.setChecked(!isChecked));

            snackbar.show();
        }
    }
}