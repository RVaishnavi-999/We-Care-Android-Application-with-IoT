package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home_Activity extends AppCompatActivity implements View.OnClickListener {

    public CardView cardAdmin, cardDoctor, cardNurse, cardPatient;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i1 = new Intent();
        i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardAdmin = (CardView) findViewById(R.id.cardAdmin);
        cardDoctor = (CardView) findViewById(R.id.cardDoctor);
        cardNurse = (CardView) findViewById(R.id.cardNurse);
        cardPatient = (CardView) findViewById(R.id.cardPatient);

        cardAdmin.setOnClickListener(this);
        cardDoctor.setOnClickListener(this);
        cardNurse.setOnClickListener(this);
        cardPatient.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.cardAdmin:
                i = new Intent(this, AdminLogin.class);
                startActivity(i);
                break;

            case R.id.cardDoctor:
                i = new Intent(this, DoctorLogin.class);
                startActivity(i);
                break;

            case R.id.cardNurse:
                i = new Intent(this, NurseLogin.class);
                startActivity(i);
                break;

            case R.id.cardPatient:
                i = new Intent(this, PatientLogin.class);
                startActivity(i);
                break;

        }

    }
}