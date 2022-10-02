package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgetpassword extends AppCompatActivity {

    ImageView bk;
    TextView em;
    Button change;

    FirebaseAuth auth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AdminLogin.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        bk = findViewById(R.id.back);
        bk.setOnClickListener(v -> {
            Intent intent = new Intent(forgetpassword.this, AdminLogin.class);
            startActivity(intent);
        });

        em = findViewById(R.id.email);
        change = findViewById(R.id.reset);

        auth = FirebaseAuth.getInstance();

        change.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                validate();
            }
        });
    }

    private void validate() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String email = snapshot1.child("email").getValue(String.class);
                    if (email.equalsIgnoreCase(em.getText().toString())) {
                        auth.sendPasswordResetEmail(em.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showsuccess("Successful", "Password sent to your mail", "Done");
                            } else {
                                showerror("Error", "Try Again Later", "Ok");
                            }
                        });
                    } else {
                        showerror("Email Error", "Email not found", "Ok");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showerror(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
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

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void showsuccess(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_success_dialog
                , findViewById(R.id.layoutdialogsuccessContainer));
        builder.setView(view);

        TextView title, message;
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

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AdminLogin.class));
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private Boolean isValidSignUpDetails() {
        if (em.getText().toString().trim().isEmpty()) {
            em.setError("Email is empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(em.getText().toString()).matches()) {
            em.setError("Enter valid email");
            return false;
        } else if (!em.getText().toString().trim().isEmpty()) {
            final String[] returns = new String[1];
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        String email = snapshot1.child("email").getValue(String.class);
                        if(!email.equalsIgnoreCase(em.getText().toString())){
                            returns[0] = "false";
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(returns[0] == "false"){
                showerror("Email Error","Email does not exist","Ok");
                return false;
            }else{
                return true;
            }
        } else {
            return true;
        }
    }
}