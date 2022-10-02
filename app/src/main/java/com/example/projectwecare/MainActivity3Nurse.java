package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

public class MainActivity3Nurse extends AppCompatActivity {

    private TextView name,email,type,phone,address,lname,gender;
    private PreferenceManage preferenceManage;
    private RoundedImageView imageProfile;
    private ProgressBar progressBar;
    private ImageView output;
    private AppCompatImageView logout,edit;
    private TextView call,sendemail;
    private String em,cl;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3nurse);

        preferenceManage = new PreferenceManage(getApplicationContext());

        //edit = findViewById(R.id.edit);
        logout = findViewById(R.id.logout);
        imageProfile = findViewById(R.id.imageProfile1);
        name = findViewById(R.id.firstname);
        email = findViewById(R.id.textemail);
        type = findViewById(R.id.type);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        lname = findViewById (R.id.rlastname);
        gender = findViewById (R.id.rgender);
        edit = findViewById(R.id.edit);
        call = findViewById(R.id.calladmin);
        sendemail = findViewById(R.id.mailadmin);

        progressBar = findViewById(R.id.progressBar6);
        output = findViewById(R.id.imageBar);

        edit.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), EditProfile.class)));

        logout.setOnClickListener(view -> signOut());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admin");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    em = dataSnapshot.child("email").getValue(String.class);
                    cl = dataSnapshot.child("phone").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cl));
                startActivity(intent);
            }
        });

        sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + em));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        BottomNavigationView bnv = findViewById(R.id.btmnav);
        bnv.setSelectedItemId(R.id.user);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), MainActivityNurse.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity2Nurse.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.user:
                        return true;
                }
                return false;
            }
        });

        viewprofile();
    }

    private void viewprofile(){
        String useremail = preferenceManage.getString(Constants.KEY_EMAIL);
        email.setText(useremail);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Registered Nurses");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String email = dataSnapshot.child("email").getValue(String.class);

                    if(email.equals(useremail)) {
                        name.setText(dataSnapshot.child("fname").getValue(String.class));
                        lname.setText(dataSnapshot.child("lname").getValue(String.class));
                        gender.setText(dataSnapshot.child("gender").getValue(String.class));
                        phone.setText(dataSnapshot.child("contactnum").getValue(String.class));
                        address.setText(dataSnapshot.child("address").getValue(String.class));
                        type.setText(dataSnapshot.child("dutyshift").getValue(String.class));

                        byte[] bytes = Base64.decode(dataSnapshot.child("encodedImage").getValue(String.class),Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                        imageProfile.setImageBitmap(bitmap);

                        String txt = dataSnapshot.child("nurseID").getValue(String.class);
                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            BitMatrix matrix = writer.encode(txt, BarcodeFormat.QR_CODE, 150, 150);
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap1 = encoder.createBitmap(matrix);
                            output.setImageBitmap(bitmap1);

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(error.getMessage());
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        showToast("Signing Out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManage.getString(Constants.KEY_USER_ID));
        HashMap<String ,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManage.clear();
                    startActivity(new Intent(getApplicationContext(),Home_Activity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to Logout"));
    }
}