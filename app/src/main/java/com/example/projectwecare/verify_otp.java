package com.example.projectwecare;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verify_otp extends AppCompatActivity {

    private EditText ip1,ip2,ip3,ip4,ip5,ip6;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        TextView textmobile = findViewById(R.id.mobileno);
        textmobile.setText((String.format("+91-%s", getIntent().getStringExtra("mobile"))));
        ip1 = findViewById(R.id.inputcode1);
        ip2 = findViewById(R.id.inputcode2);
        ip3 = findViewById(R.id.inputcode3);
        ip4 = findViewById(R.id.inputcode4);
        ip5 = findViewById(R.id.inputcode5);
        ip6 = findViewById(R.id.inputcode6);

        setupOTPinputs();

        final ProgressBar progressBar = findViewById(R.id.progressbar2);
        final Button verify = findViewById(R.id.btnverify);

        verificationId = getIntent().getStringExtra("verificationId");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ip1.getText().toString().trim().isEmpty()
                        ||ip2.getText().toString().trim().isEmpty()
                        ||ip3.getText().toString().trim().isEmpty()
                        ||ip4.getText().toString().trim().isEmpty()
                        ||ip5.getText().toString().trim().isEmpty()
                        ||ip6.getText().toString().trim().isEmpty()
                ){
                    Toast.makeText(verify_otp.this,"Please Enter valid code",Toast.LENGTH_LONG).show();
                    return;
                }
                String code  = ip1.getText().toString() +
                        ip2.getText().toString() +
                        ip3.getText().toString() +
                        ip4.getText().toString() +
                        ip5.getText().toString() +
                        ip6.getText().toString() ;

                if(verificationId != null){
                    progressBar.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,code);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    verify.setVisibility(View.VISIBLE);
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(getApplicationContext(),Patient_home_pg.class);
                                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(verify_otp.this,"The Verification Code Entered is Invalid",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });

        findViewById(R.id.textresendotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip1.clearFocus();
                ip2.clearFocus();
                ip3.clearFocus();
                ip4.clearFocus();
                ip5.clearFocus();
                ip6.clearFocus();

                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" +
                                getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        verify_otp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(verify_otp.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String news, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = news;
                                Toast.makeText(verify_otp.this,"OTP sent successfully",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
    private void setupOTPinputs(){
        ip1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    ip2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ip2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    ip3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ip3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    ip4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ip4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    ip5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ip5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    ip6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}