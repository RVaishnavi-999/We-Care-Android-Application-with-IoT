package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorLogin extends AppCompatActivity {
    public EditText editTextLoginEmail, editTextLoginPwd;
    ProgressBar progressBar;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    String TAG = "DoctorLogin";
    TextView forgot_pwd_tv;

    //r
    private PreferenceManage preferenceManage;
    private FirebaseAuth mAuth;



    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (DoctorLogin.this, Home_Activity.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_doctor_login);

        editTextLoginEmail = findViewById (R.id.email);
        editTextLoginPwd = findViewById (R.id.pwd);
        progressBar = findViewById (R.id.progressbar);
        authProfile = FirebaseAuth.getInstance ();
        firebaseUser = authProfile.getCurrentUser ();

        //r
        mAuth = FirebaseAuth.getInstance();
        preferenceManage = new PreferenceManage(getApplicationContext());


        ImageView imageViewShowHidePwd = findViewById (R.id.imgview_show_hide_pwd);
        imageViewShowHidePwd.setImageResource (R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (editTextLoginPwd.getTransformationMethod ().equals (HideReturnsTransformationMethod.getInstance ()))
                {
                    editTextLoginPwd.setTransformationMethod (PasswordTransformationMethod.getInstance ());
                    imageViewShowHidePwd.setImageResource (R.drawable.ic_hide_pwd);  //hides pwd
                } else
                {
                    editTextLoginPwd.setTransformationMethod (HideReturnsTransformationMethod.getInstance ());
                    imageViewShowHidePwd.setImageResource (R.drawable.ic_show_pwd); //shows pwd
                }
            }
        });
        Button buttonLogin = findViewById (R.id.login_btn);
        buttonLogin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText ().toString ();
                String textPwd = editTextLoginPwd.getText ().toString ();
                if (TextUtils.isEmpty (textEmail))
                {
                    Toast.makeText (DoctorLogin.this, "Please enter your email", Toast.LENGTH_LONG).show ();
                    editTextLoginEmail.setError ("Email is required");
                    editTextLoginEmail.requestFocus ();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher (textEmail).matches ())
                {
                    Toast.makeText (DoctorLogin.this, "Please re-enter your email", Toast.LENGTH_LONG).show ();
                    editTextLoginEmail.setError ("Valid Email is required");
                    editTextLoginEmail.requestFocus ();
                }
                else if (TextUtils.isEmpty (textPwd))
                {
                    Toast.makeText (DoctorLogin.this, "Please enter your password", Toast.LENGTH_LONG).show ();
                    editTextLoginPwd.setError ("Password is required");
                    editTextLoginPwd.requestFocus ();
                }
                else
                {
                    progressBar.setVisibility (View.VISIBLE);
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance ().getReference ("Registered Doctors");
                    databaseReference1.orderByChild ("email").equalTo (editTextLoginEmail.getText ().toString ())
                            .addListenerForSingleValueEvent (new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren ())
                            {
                                String e1 = dataSnapshot.child ("email").getValue().toString ();
                                String e2 = textEmail;
                                String speciality_filed = dataSnapshot.child("speciality").getValue ().toString ();
                                if(e2.equals (e1) && speciality_filed.equals ("Cardiologist"))
                                {
                                    loginDoctor (textEmail, textPwd);
                                    signin1 ();
                                }
                                else
                                {
                                    loginDoctorGenPhy (textEmail, textPwd);
                                    signin2 ();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            showerror ("Oops !", error.getMessage () + "Try Again", "OK");

                        }
                    });
                }
            }
        });
        forgot_pwd_tv = findViewById (R.id.forgot_pwd_click_tv);
        forgot_pwd_tv.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Toast.makeText (DoctorLogin.this, "You can change your password", Toast.LENGTH_SHORT).show();
                startActivity (new Intent (DoctorLogin.this, ForgotPWD.class));
            }
        });
    }


    private void loginDoctor(String email, String pwd) {
        authProfile.signInWithEmailAndPassword (email, pwd)
                .addOnCompleteListener (new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful ())
                {
                    FirebaseUser firebaseUser = authProfile.getCurrentUser ();
                    assert firebaseUser != null;
                    if (firebaseUser.isEmailVerified ())
                    {
                        showsuccess ("Success", "You are logged in now", "OK");
                    } else {
                        firebaseUser.sendEmailVerification ();
                        authProfile.signOut ();
                        showAlertDialog();
                    }
                }
                else
                {
                    try {
                        throw task.getException ();
                    }
                    catch (FirebaseAuthInvalidUserException e)
                    {
                        editTextLoginEmail.setError ("User does not exist or is no longer valid. Please contact admin");
                        editTextLoginEmail.requestFocus ();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginEmail.setError ("Invalid credentials. Kindly, check and register");
                        editTextLoginEmail.requestFocus ();
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage ());
                        showerror ("Oops !", e.getMessage (), "OK");
}
                }
                progressBar.setVisibility (View.GONE);
            }
        });
    }

    private void loginDoctorGenPhy(String email, String pwd) {
        authProfile.signInWithEmailAndPassword (email, pwd).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful ())
                {
                    FirebaseUser firebaseUser = authProfile.getCurrentUser ();
                    if (firebaseUser.isEmailVerified ())
                    {
                        showsuccess2 ("Success", "You are logged in now", "OK");
                        } else {
                        firebaseUser.sendEmailVerification ();
                        authProfile.signOut ();
                        showAlertDialog();
                    }

                }
                else
                {
                    try {
                        throw task.getException ();
                    } catch (FirebaseAuthInvalidUserException e)
                    {
                        editTextLoginEmail.setError ("User does not exist or is no longer valid. Please contact admin");
                        editTextLoginEmail.requestFocus ();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginEmail.setError ("Invalid credentials. Kindly, check and register");
                        editTextLoginEmail.requestFocus ();
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage ());
                        Toast.makeText (DoctorLogin.this, e.getMessage (), Toast.LENGTH_LONG).show ();
 }

                }
                progressBar.setVisibility (View.GONE);
            }
        });
    }




    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (DoctorLogin.this);
        builder.setTitle ("Email not verified !");
        builder.setMessage ("Please verify your email now. You cannot login without email verification.");

        builder.setPositiveButton ("Continue", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent = new Intent (Intent.ACTION_MAIN);
                intent.addCategory (Intent.CATEGORY_APP_EMAIL);
                intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (intent);
            }
        });

        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
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
            Intent i1  = new Intent (DoctorLogin.this, C_DrPatientsList.class);
            i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity (i1);
            finish ();
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void showsuccess2(String title1,String message1,String action1){
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
            Intent i2 = new Intent (DoctorLogin.this, GenPhy_DrHomepg.class);
            i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity (i2);
            finish ();
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    //r
    private void signin1 () {
        String email = editTextLoginEmail.getText().toString();
        String pass = editTextLoginPwd.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();
        preferenceManage.putString(Constants.KEY_AUTHUSER_ID, String.valueOf(user));
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .whereEqualTo(Constants.KEY_DESIGNATION, "Senior Doctor")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().getDocuments().size() > 0) {

                        mAuth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                                            preferenceManage.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                            preferenceManage.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                            preferenceManage.putString(Constants.KEY_EMAIL, editTextLoginEmail.getText().toString());
                                            preferenceManage.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                            preferenceManage.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                        }
//                                        else {
//                                            showerror("Failed !", "Loggin Failed", "Ok");
//                                        }
                                    }
                                });

                    }
//                    else {
//                        showerror("Failed", "Only Nurse can login", "Ok");
//                    }
                });
    }

    private void signin2 () {
        String email = editTextLoginEmail.getText().toString();
        String pass = editTextLoginPwd.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();
        preferenceManage.putString(Constants.KEY_AUTHUSER_ID, String.valueOf(user));
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .whereEqualTo(Constants.KEY_DESIGNATION, "Junior Doctor")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().getDocuments().size() > 0) {

                        mAuth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                                            preferenceManage.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                            preferenceManage.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                            preferenceManage.putString(Constants.KEY_EMAIL, editTextLoginEmail.getText().toString());
                                            preferenceManage.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                            preferenceManage.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));

                                        }
//                                        else {
//                                            showerror("Failed !", "Loggin Failed", "Ok");
//                                        }
                                    }
                                });

                    }
//                    else {
//                        showerror("Failed", "Only Nurse can login", "Ok");
//                    }
                });
    }
}