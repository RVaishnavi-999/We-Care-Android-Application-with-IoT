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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Barattendance extends AppCompatActivity {

    String id;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        scanning();
    }
    private void scanning(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(Barattendance.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("For flash use volume up");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(capture.class);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            id = result.getContents();
            if (id != null) {
                // Here we need to handle scanned data...
                    checkNurse(id);
            }else {
                showerror("ID Error","Error while handling","Try Again");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                        showsuccess("Success",id + " Successfully Signed In","Ok");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showerror("Error",e.getMessage().toString(),"Try Again");
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
                        showsuccess("Success",id + " Successfully Signed Out","Ok");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showerror("Error",e.getMessage().toString(),"Try Again");
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
                showerror("Error",error.getMessage().toString(),"Try Again");
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
                showerror("Error",error.getMessage().toString(),"Try Again");
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
                                showsuccess("Error","ID does not exist","Try Again");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            showerror("Error",error.getMessage().toString(),"Try Again");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error",error.getMessage().toString(),"Try Again");
            }
        });

    }

    private void showsuccess(String title1,String message1,String action1){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
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

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            startActivity(new Intent(this, adminMain.class));
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void showerror(String title1,String message1,String action1){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_error_dialog,(ConstraintLayout)findViewById(R.id.layoutdialogerrorContainer));
        builder.setView(view);

        TextView title,message;
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
            startActivity(new Intent(this, adminMain.class));
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

}