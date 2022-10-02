package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPWDNurse extends AppCompatActivity {
    private Button btn_reset_pwd;
    private EditText et_email;
    private FirebaseAuth authProfile;
    private final static String TAG = "ForgotPWDNurse";

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (ForgotPWDNurse.this, NurseLogin.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_forgot_pwdnurse);

        et_email = findViewById (R.id.et_pwd_reset_email);
        btn_reset_pwd = findViewById (R.id.btn_pwd_reset);
        btn_reset_pwd.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String email = et_email.getText ().toString ();

                if (TextUtils.isEmpty (email)) {
                    Toast.makeText (ForgotPWDNurse.this, "Please enter your registered email !", Toast.LENGTH_SHORT).show ();
                    et_email.setError ("Email is required");
                    et_email.requestFocus ();
                } else if (!Patterns.EMAIL_ADDRESS.matcher (email).matches ()) {
                    Toast.makeText (ForgotPWDNurse.this, "Please enter your registered email !", Toast.LENGTH_SHORT).show ();
                    et_email.setError ("Valid Email is required");
                    et_email.requestFocus ();
                } else
                {
                    resetPassword (email);
                }
            }
        });
    }

    public void ClickBack(View view)
    {
        Intent i = new Intent (this, NurseLogin.class);
        startActivity (i);
        finish ();
    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance ();
        authProfile.sendPasswordResetEmail (email).addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful ()) {
                    Toast.makeText (ForgotPWDNurse.this, "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show ();
                    Intent i = new Intent (ForgotPWDNurse.this, NurseLogin.class);
                    i.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (i);
                    finish ();
                }
                else {
                    try {
                        throw task.getException ();
                    }
                    catch (FirebaseAuthInvalidUserException e) {
                        et_email.setError ("User does not exists or is no longer valid. Please contact admin.");
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage ());
                        Toast.makeText (ForgotPWDNurse.this, e.getMessage (), Toast.LENGTH_SHORT).show ();
                    }

                }
            }
        });
    }
}