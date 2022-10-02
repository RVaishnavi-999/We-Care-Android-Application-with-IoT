package com.example.projectwecare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PatientLogin extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (PatientLogin.this, Home_Activity.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);

        final EditText inputmobile = findViewById(R.id.inputmobile);
        final ProgressBar progressBar= findViewById(R.id.progressbar1);
        final Button getotp = findViewById(R.id.btnsend);

        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputmobile.getText().toString().trim().isEmpty()){
                    Toast.makeText(PatientLogin.this,"Enter Mobile",Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                getotp.setVisibility(View.INVISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + inputmobile.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        PatientLogin.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                getotp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                getotp.setVisibility(View.VISIBLE);
                                Toast.makeText(PatientLogin.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                getotp.setVisibility(View.VISIBLE);

                                Intent intent = new Intent(getApplicationContext(), verify_otp.class);
                                intent.putExtra("mobile",inputmobile.getText().toString());
                                intent.putExtra("verificationId", s);
                                startActivity(intent);

                            }
                        });
            }
        });
    }
}