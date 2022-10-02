package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

public class AdminEditProfile extends AppCompatActivity {

    private TextView name, email, type, phone, address;
    private PreferenceManage preferenceManage;
    private RoundedImageView imageProfile;
    private ProgressBar progressBar;
    private ImageView output;
    private AppCompatImageView logout;

    BottomNavigationView btmnav;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), adminMain.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);

        preferenceManage = new PreferenceManage(getApplicationContext());

        logout = findViewById(R.id.logout1);
        imageProfile = findViewById(R.id.imageProfile4);
        name = findViewById(R.id.firstname1);
        email = findViewById(R.id.textemail1);
        type = findViewById(R.id.type1);
        phone = findViewById(R.id.phone1);
        address = findViewById(R.id.address1);
        progressBar = findViewById(R.id.progressBar17);
        output = findViewById(R.id.imageBar1);

        logout.setOnClickListener(view -> signOut());

        btmnav = findViewById(R.id.nav_view);
        btmnav.setSelectedItemId(R.id.user1);

        btmnav.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.user1:
                    return true;

                case R.id.home1:
                    startActivity(new Intent(getApplicationContext(), adminMain.class));
                    overridePendingTransition(0, 0);
                    return true;

            }
            return false;
        });

        viewprofile();
    }

    private void viewprofile(){
        String useremail = preferenceManage.getString(Constants.KEY_ADMINID);
        name.setText(preferenceManage.getString(Constants.KEY_ADMINNAME));
        byte[] bytes = Base64.decode(preferenceManage.getString(Constants.KEY_ADMINIMG),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        imageProfile.setImageBitmap(bitmap);
        email.setText(useremail);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Admin");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String email = dataSnapshot.child("email").getValue(String.class);

                    if(email.equals(useremail)) {
                        phone.setText(dataSnapshot.child("phone").getValue(String.class));
                        address.setText(dataSnapshot.child("address").getValue(String.class));
                        type.setText(dataSnapshot.child("type").getValue(String.class));



                        String txt = dataSnapshot.child("id").getValue(String.class);
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void signOut() {
        showToast("Signing Out");
        FirebaseAuth.getInstance().signOut();
        preferenceManage.clear();
        startActivity(new Intent(getApplicationContext(), AdminLogin.class));
        finish();
    }
}