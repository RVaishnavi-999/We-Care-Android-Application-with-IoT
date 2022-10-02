package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView tv_Authenticated;
    private String drOldEmail, drNewEmail, drPwd;
    private Button btnUpdateEmail;
    private EditText etNewEmail, etPWD;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (UpdateEmailActivity.this, C_DrPatientsList.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_update_email);

        tv_Authenticated = findViewById (R.id.tv_update_email_authenticated);
        etPWD = findViewById (R.id.et_verify_pwd);
        etNewEmail = findViewById (R.id.et_update_email_new);
        btnUpdateEmail = findViewById (R.id.btn_update_email);
        progressBar = findViewById (R.id.progressbar);

        btnUpdateEmail.setEnabled (false);
        etNewEmail.setEnabled (false);

        authProfile = FirebaseAuth.getInstance ();
        firebaseUser = authProfile.getCurrentUser ();
        drOldEmail = firebaseUser.getEmail ();
        TextView tvOldEmail = findViewById (R.id.tv_update_email_old);
        tvOldEmail.setText (drOldEmail);

        if (firebaseUser.equals (" ")) {
            showerror ("Error", "Something went wrong! Doctor's details not available.", "OK");
        } else {
            reAuthenticate(firebaseUser);
        }

    }
    public void ClickBack(View view)
    {
        C_DrPatientsList.redirectActivity(this, C_DrEditProfile.class);
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button btnVerifyDr = findViewById (R.id.btn_authenticate_dr);
        btnVerifyDr.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                drPwd = etPWD.getText ().toString ();

                if (TextUtils.isEmpty (drPwd)) {
                    Toast.makeText (UpdateEmailActivity.this, "Password is needed to continue !", Toast.LENGTH_SHORT).show ();
                    etPWD.setError ("Please enter your password for authentication");
                    etPWD.requestFocus ();
                }
                else
                {
                    AuthCredential credential = EmailAuthProvider.getCredential (drOldEmail, drPwd);

                    firebaseUser.reauthenticate (credential).addOnCompleteListener (new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful ())
                            {
                                progressBar.setVisibility (View.GONE);
                                Toast.makeText (UpdateEmailActivity.this, "Password has been verified. You can update email now.", Toast.LENGTH_LONG).show ();

                                tv_Authenticated.setText ("You are authenticated. You can update your email now.");
                                etPWD.setEnabled (false);
                                btnVerifyDr.setEnabled (false);
                                etNewEmail.setEnabled (true);
                                btnUpdateEmail.setEnabled (true);
                                btnUpdateEmail.setBackgroundTintList (ContextCompat.getColorStateList (UpdateEmailActivity.this, R.color.dark_green));

                                btnUpdateEmail.setOnClickListener (new View.OnClickListener () {
                                    @Override
                                    public void onClick(View view) {
                                        drNewEmail = etNewEmail.getText ().toString ();
                                        if (TextUtils.isEmpty (drNewEmail)) {
                                            Toast.makeText (UpdateEmailActivity.this, "New email is required !", Toast.LENGTH_SHORT).show ();
                                            etNewEmail.setError ("Please enter new Email Address");
                                            etNewEmail.requestFocus ();
                                        }
                                        else if (!Patterns.EMAIL_ADDRESS.matcher (drNewEmail).matches ()) {
                                            Toast.makeText (UpdateEmailActivity.this, "Please enter valid email !", Toast.LENGTH_SHORT).show ();
                                            etNewEmail.setError ("Please enter valid Email Address");
                                            etNewEmail.requestFocus ();
                                        }
                                        else if (drOldEmail.matches (drNewEmail)) {
                                            Toast.makeText (UpdateEmailActivity.this, "New email cannot be same as old email  !", Toast.LENGTH_SHORT).show ();
                                            etNewEmail.setError ("Please enter new Email Address");
                                            etNewEmail.requestFocus ();
                                        }
                                        else {
                                            progressBar.setVisibility (View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                try {
                                    throw task.getException ();
                                } catch (Exception e) {
                                    Toast.makeText (UpdateEmailActivity.this, e.getMessage (), Toast.LENGTH_SHORT).show ();
                                }
                            }

                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail (drNewEmail).addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful ()) {
                    firebaseUser.sendEmailVerification ();
                    showsuccess ("Success", "Email has been updated. Please  verify your new email !", "OK");
                }
                else
                {
                    try
                    {
                        throw task.getException ();
                    } catch (Exception e)
                    {
                        Toast.makeText (UpdateEmailActivity.this, e.getMessage (), Toast.LENGTH_LONG).show ();
                    }
                }
                progressBar.setVisibility (View.GONE);
            }
        });
    }
    private void showerror(String title1, String message1, String action1) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_error_dialog, (ConstraintLayout) findViewById(R.id.layoutdialogerrorContainer));
        builder.setView(view);

        TextView title, message;
        Button action;
        ImageView image;

        title = view.findViewById(R.id.textTitle2);
        message = view.findViewById(R.id.textMessage2);
        action = view.findViewById(R.id.buttonAction2);
        image = view.findViewById(R.id.imageIcon2);

        title.setText(title1);
        message.setText(message1);
        action.setText(action1);
        image.setImageResource(R.drawable.ic_error);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable (0));
        }

        alertDialog.show();
    }

    private void showsuccess(String title1,String message1,String action1){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_success_dialog,(ConstraintLayout)findViewById(R.id.layoutdialogsuccessContainer));
        builder.setView(view);

        TextView title,message;
        Button action;
        ImageView image;

        title = view.findViewById(R.id.textTitle1);
        message = view.findViewById(R.id.textMessage1);
        action = view.findViewById(R.id.buttonAction1);
        image = view.findViewById(R.id.imageIcon1);

        title.setText(title1);
        message.setText(message1);
        action.setText(action1);
        image.setImageResource(R.drawable.ic_success);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {

            Intent i = new Intent (UpdateEmailActivity.this, C_DrEditProfile.class);
            startActivity (i);
            finish();
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
}