package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class C_DrContactAdmin extends AppCompatActivity {
    private ImageView callAdmin_imgview, back_iv;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (C_DrContactAdmin.this, C_DrPatientsList.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cdr_contact_admin);

        callAdmin_imgview = findViewById (R.id.callAdmin_imgView);
        callAdmin_imgview.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData (Uri.parse("tel:9900814353"));
                startActivity(callIntent);
            }
        });


        back_iv = findViewById (R.id.back_iv);
        back_iv.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (C_DrContactAdmin.this, C_DrPatientsList.class);
                startActivity (i);
            }
        });

    }
}