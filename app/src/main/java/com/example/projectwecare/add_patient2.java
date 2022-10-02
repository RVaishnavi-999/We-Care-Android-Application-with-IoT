package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class add_patient2 extends AppCompatActivity {
    
    TextInputEditText et_eme_name, et_eme_phone, et_eme_relationship;
    Button btn1, btn2, btn3, save_btn ;


    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (add_patient2.this, popup_window_patient.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_patient2);

        btn1 = (Button) findViewById (R.id.step1);
        btn1.setBackgroundResource (R.drawable.selected_btn);
        btn2 = findViewById (R.id.step2);
        btn2.setBackgroundResource (R.drawable.selected_btn);
        btn3 = (Button) findViewById (R.id.step3);

        btn1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                tab1 ();
            }
        });

        btn3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                tab3();
            }
        });

        et_eme_name = findViewById (R.id.et_family_name);
        et_eme_phone = findViewById (R.id.et_family_phone);
        et_eme_relationship = findViewById (R.id.et_relationship);

        save_btn = findViewById (R.id.save_btn2);
        save_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                String p_eme_name = et_eme_name.getText ().toString ();
                String p_eme_phone = et_eme_phone.getText ().toString ();
                String p_eme_relative = et_eme_relationship.getText ().toString ();

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile (mobileRegex);

                mobileMatcher = mobilePattern.matcher (p_eme_phone);

                if (TextUtils.isEmpty (p_eme_name))
                {
                    Toast.makeText (add_patient2.this, "Please enter full name", Toast.LENGTH_SHORT).show ();
                    et_eme_name.setError ("Full name is required");
                    et_eme_name.requestFocus ();
                }
                else if(!p_eme_name.matches("[a-zA-Z ]+"))
                {
                    et_eme_name.requestFocus();
                    et_eme_name.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }

                else if (TextUtils.isEmpty (p_eme_phone))
                {
                    Toast.makeText (add_patient2.this, "Please enter contact number", Toast.LENGTH_SHORT).show ();
                    et_eme_phone.setError ("Contact Number  is required");
                    et_eme_phone.requestFocus ();
                }

                else if (p_eme_phone.length () != 10)
                {
                    Toast.makeText (add_patient2.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    et_eme_phone.setError ("Contact Number  should be 10 digits");
                    et_eme_phone.requestFocus ();
                }
                else if (!mobileMatcher.find ())  {
                    Toast.makeText (add_patient2.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    et_eme_phone.setError ("Contact Number  is not valid");
                    et_eme_phone.requestFocus ();
                }
                else if (TextUtils.isEmpty (p_eme_relative))
                {
                    Toast.makeText (add_patient2.this, "Please enter relationship to the patient", Toast.LENGTH_SHORT).show ();
                    et_eme_relationship.setError ("Relationship to patient is required");
                    et_eme_relationship.requestFocus ();
                }
                else if(!p_eme_relative.matches("[a-zA-Z ]+"))
                {
                    et_eme_relationship.requestFocus();
                    et_eme_relationship.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else
                {
                    Intent i21 =getIntent ();
                    String p_fname =i21.getStringExtra ("fname");
                    String p_lname =i21.getStringExtra ("lname");
                    String p_con_num =i21.getStringExtra ("con_num");
                    String p_email =i21.getStringExtra ("email");
                    String p_gender =i21.getStringExtra ("gender");
                    String p_age =i21.getStringExtra ("age");
                    String p_address =i21.getStringExtra ("address");

                    Intent i22 = new Intent (getApplicationContext (), add_patient3.class);
                    i22.putExtra("Pfname", p_fname);
                    i22.putExtra("Plname", p_lname);
                    i22.putExtra("Pconnum", p_con_num);
                    i22.putExtra("Pemail", p_email);
                    i22.putExtra("Pgender", p_gender);
                    i22.putExtra("Page", p_age);
                    i22.putExtra("Paddress", p_address);

                    i22.putExtra("P_eme_name", p_eme_name);
                    i22.putExtra("P_eme_phone", p_eme_phone);
                    i22.putExtra("P_eme_relative", p_eme_relative);
                    Toast.makeText (add_patient2.this, "Saving...", Toast.LENGTH_SHORT).show ();
                    startActivity(i22);
                }
            }
        });
        
    }


    public void tab1() {
        Intent intent = new Intent (this, add_patient1.class);
        startActivity (intent);
    }


    public void tab3() {
        Intent intent = new Intent (this, add_patient3.class);
        startActivity (intent);
    }
}