package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLogin extends AppCompatActivity {

    private PreferenceManage preferenceManage;
    EditText email, pass;
    Button login;
    String pat = "[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+";
    ProgressDialog pd;
    TextView forget;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Home_Activity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        email = findViewById(R.id.emailid);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login);
        forget = findViewById(R.id.tv_forgotPassword);
        progressBar = findViewById(R.id.progressBar20);

        pd = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        preferenceManage = new PreferenceManage(getApplicationContext());

        forget.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLogin.this, forgetpassword.class);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                progressBar.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);
                performlogin();
            }
        });

    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), adminMain.class);
//            startActivity(intent);
//        }
//    }

    private void performlogin() {

        String email1 = email.getText().toString();
        String pass1 = pass.getText().toString();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email1)
                .whereEqualTo(Constants.KEY_DESIGNATION, "Admin")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().getDocuments().size() > 0) {
                        mAuth.signInWithEmailAndPassword(email1, pass1)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            AuthResult result = task.getResult();

                                            DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                                            preferenceManage.putBoolean(Constants.KEY_IS_SIGNED_IN1, true);
                                            preferenceManage.putString(Constants.KEY_USER_ID1, documentSnapshot.getId());
                                            preferenceManage.putString(Constants.KEY_ADMINID, email1);
                                            preferenceManage.putString(Constants.KEY_ADMINNAME, documentSnapshot.getString(Constants.KEY_NAME));
                                            preferenceManage.putString(Constants.KEY_ADMINIMG, documentSnapshot.getString(Constants.KEY_IMAGE));

                                            progressBar.setVisibility(View.INVISIBLE);
                                            login.setVisibility(View.VISIBLE);

                                            Intent intent = new Intent(getApplicationContext(), adminMain.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            login.setVisibility(View.VISIBLE);
                                            showerror("Logged in Failed", "Invalid password or email id", "Ok");
                                        }
                                    }
                                });

                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        login.setVisibility(View.VISIBLE);
                        showerror("Failed", "Only Admin can login", "Ok");
                    }
                });
    }

    private void showerror(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminLogin.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(AdminLogin.this).inflate(R.layout.layout_error_dialog, (ConstraintLayout) findViewById(R.id.layoutdialogerrorContainer));
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

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private Boolean isValidSignUpDetails() {
        if (email.getText().toString().trim().isEmpty()) {
            email.setError("Email is empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Enter valid email");
            return false;
        } else if (pass.getText().toString().trim().isEmpty()) {
            pass.setError("Enter Password");
            return false;
        } else {
            return true;
        }
    }
}


