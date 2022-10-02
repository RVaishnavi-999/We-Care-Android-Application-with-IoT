package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientIotView extends AppCompatActivity {

    WebView url1, url2;
    AppCompatImageButton copy;
    PreferenceManage preferenceManage;
    ClipboardManager clipboardManager;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity2Nurse.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_iot_view);

        progressBar = findViewById(R.id.progressBar22);
        url1 = findViewById(R.id.url1);
        url2 = findViewById(R.id.url2);
        copy = findViewById(R.id.copy);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        progressBar.setVisibility(View.VISIBLE);

        if (!clipboardManager.hasPrimaryClip()) {
            //paste enabled false
        }

        preferenceManage = new PreferenceManage(getApplicationContext());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CareUnit").child(preferenceManage.getString(Constants.KEY_PATIENTCAREUNITIOT));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                load1(snapshot.child("URL1").getValue(String.class));
                load2(snapshot.child("URL2").getValue(String.class));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FirebaseIOT");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ecg = snapshot.child("ecg").getValue(String.class);
                        String temp = snapshot.child("temperature").getValue(String.class);
                        String txt = "\n PatientId: " + preferenceManage.getString(Constants.KEY_PATIENTIDIOT)
                                + "\n PatientName: " + preferenceManage.getString(Constants.KEY_PATIENTNAMEIOT)
                                + "\n PatientCareUnit: " + preferenceManage.getString(Constants.KEY_PATIENTCAREUNITIOT)
                                + "\n Temperature: " + temp
                                + "\n ECG: " + ecg;
                        ClipData clipData;
                        clipData = ClipData.newPlainText("View", txt);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(getApplicationContext(),"Copied",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void load1(String url) {
        url1.getSettings().setLoadsImagesAutomatically(true);
        url1.getSettings().setJavaScriptEnabled(true);
        url1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        url1.loadUrl(url);
        url1.setWebViewClient(new WebViewClient());

    }

    private void load2(String url) {
        url2.getSettings().setLoadsImagesAutomatically(true);
        url2.getSettings().setJavaScriptEnabled(true);
        url2.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        url2.loadUrl(url);
    }
}