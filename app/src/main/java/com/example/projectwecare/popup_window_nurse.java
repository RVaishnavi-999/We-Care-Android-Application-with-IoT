package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class popup_window_nurse extends AppCompatActivity {
    Button addnurse, viewnurse;
    public TextView closetv;


    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (popup_window_nurse.this, adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_popup_window_nurse);
        addnurse = (Button) findViewById (R.id.addnurse);
        viewnurse = (Button) findViewById (R.id.viewnurse);
        closetv = (TextView) findViewById (R.id.closeIcon);
        closetv.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (popup_window_nurse.this, adminMain.class);
                startActivity (intent);
            }
        });


        addnurse.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent (popup_window_nurse.this, add_nurse.class);
                startActivity (i1);
            }
        });

        viewnurse.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent (popup_window_nurse.this, view_nurse.class);
                startActivity (i2);
            }
        });
    }




}