package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ManualAttendance extends AppCompatActivity {

    TextInputEditText id1;
    Button login;
    TextView message;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_attendance);

        id1 = findViewById(R.id.textMessage3);
        login = findViewById(R.id.buttonmaualattend);
        message = findViewById(R.id.successmessage);
        progressBar = findViewById(R.id.progressBar8);

        login.setOnClickListener(v ->{
            progressBar.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
            if(validate()){
                checkNurse(id1.getText().toString());
            }
        });

    }

    private void checkNurse(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Nurses");
        reference.orderByChild("nurseID").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    checksigninNextdate(id);
                }
                else{
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Doctors");
                    reference.orderByChild("userid").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                checksigninNextdate(id);
                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                                message.setVisibility(View.VISIBLE);
                                message.setTextColor(getResources().getColor(R.color.darkred));
                                message.setText("ID does not exist");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                            message.setVisibility(View.VISIBLE);
                            message.setTextColor(getResources().getColor(R.color.darkred));
                            message.setText(error.getMessage().toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.INVISIBLE);
                message.setVisibility(View.VISIBLE);
                message.setTextColor(getResources().getColor(R.color.darkred));
                message.setText(error.getMessage().toString());
            }
        });

    }

    private void checksigninNextdate(String id){
        Date date = new Date();
        final Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        String currentDate = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(calendar.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(currentDate).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    SignOut(id,currentDate);
                }else{
                    IsvalidateSignIn(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                message.setVisibility(View.VISIBLE);
                message.setTextColor(getResources().getColor(R.color.darkred));
                message.setText(error.getMessage().toString());
            }
        });
    }

    private void SignIn(String id,String currentDate){

        DatabaseReference reference;

        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        reference = FirebaseDatabase.getInstance().getReference("Attendance").child(currentDate).child(id);

        HashMap<String, Object> user = new HashMap<>();
        user.put("id",id);
        user.put("SignIn",currentTime);
        user.put("status","in");

        reference.setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        id1.setText("");
                        login.setVisibility(View.VISIBLE);
                        message.setVisibility(View.VISIBLE);
                        message.setTextColor(getResources().getColor(R.color.ten));
                        message.setText("Successfully Signed In");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        id1.setText("");
                        progressBar.setVisibility(View.INVISIBLE);
                        login.setVisibility(View.VISIBLE);
                        message.setVisibility(View.VISIBLE);
                        message.setTextColor(getResources().getColor(R.color.darkred));
                        message.setText(e.getMessage().toString());
                    }
                });
    }

    private void SignOut(String id,String currentDate) {

        DatabaseReference reference;

        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        reference = FirebaseDatabase.getInstance().getReference("Attendance").child(currentDate).child(id);

        HashMap<String, Object> user = new HashMap<>();
        user.put("SignOut",currentTime);
        user.put("status","out");

        reference.updateChildren(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        id1.setText("");
                        progressBar.setVisibility(View.INVISIBLE);
                        login.setVisibility(View.VISIBLE);
                        message.setVisibility(View.VISIBLE);
                        message.setTextColor(getResources().getColor(R.color.ten));
                        message.setText("Successfully Signed Out");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        id1.setText("");
                        progressBar.setVisibility(View.INVISIBLE);
                        login.setVisibility(View.VISIBLE);
                        message.setVisibility(View.VISIBLE);
                        message.setTextColor(getResources().getColor(R.color.darkred));
                        message.setText(e.getMessage().toString());
                    }
                });
    }

    private void IsvalidateSignIn(String id){

        String currentDate = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(currentDate).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    SignOut(id,currentDate);
                }else{
                    SignIn(id,currentDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                message.setTextColor(getResources().getColor(R.color.darkred));
                message.setText(error.getMessage().toString());
            }
        });
    }

    private boolean validate(){
        if(id1.getText().toString().trim().isEmpty()){
            id1.setError("Enter id");
            return false;
        }else{
            return true;
        }
    }

}