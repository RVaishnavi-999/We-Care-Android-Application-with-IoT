package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class popup_window_patient extends AppCompatActivity {
    Button addpatient, viewpatient, editpatient;
    public TextView closetv;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (popup_window_patient.this, adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_popup_window_patient);

        addpatient = (Button) findViewById (R.id.addpatient);
        viewpatient = (Button) findViewById (R.id.viewpatient);

        closetv = (TextView) findViewById (R.id.closeIcon);

        closetv.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                closeMethod();
            }
        });


        addpatient.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                add_patient_activity ();
            }
        });


        viewpatient.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                view_patient_activity ();
            }
        });


    }


    private void closeMethod() {
        Intent intent = new Intent (this, adminMain.class);
        startActivity (intent);
    }


    private void add_patient_activity() {
        Intent intent = new Intent (this, add_patient1.class);
        startActivity (intent);
    }


    private void view_patient_activity() {
        Intent intent = new Intent (this, view_patient.class);
        startActivity (intent);
    }

}



