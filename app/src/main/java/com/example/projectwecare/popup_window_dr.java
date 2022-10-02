package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class popup_window_dr extends AppCompatActivity {
    Button adddoctor, viewdoctor;
    public TextView closetv;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (popup_window_dr.this, adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_popup_window_dr);

        adddoctor = (Button) findViewById (R.id.adddoctor);
        viewdoctor = (Button) findViewById (R.id.viewdoctor);

        closetv = (TextView) findViewById (R.id.closeIcon);

        closetv.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (popup_window_dr.this, adminMain.class);
                startActivity (intent);
            }
        });


        adddoctor.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
               Intent i1 = new Intent (popup_window_dr.this, add_dr.class);
               startActivity (i1);
            }
        });


        viewdoctor.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent (popup_window_dr.this, view_dr.class);
                startActivity (i2);
            }
        });
    }




}