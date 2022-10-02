package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class add_patient1 extends AppCompatActivity {

    TextInputEditText etfname, etlname, etcon_num, etemail, etage, etaddress;
    RadioGroup radioGroupRegisterGender;
    RadioButton radioButtonRegisterGenderSelected;
    Button btn1, btn2, btn3, save_btn ;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (add_patient1.this, popup_window_patient.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_patient1);

        Toast.makeText (this, "You can add patients details now", Toast.LENGTH_SHORT).show ();


        btn1 = findViewById (R.id.step1);
        btn1.setBackgroundResource (R.drawable.selected_btn);

        btn2 = (Button) findViewById (R.id.step2);
        btn2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                tab2 ();
            }
        });

        btn3 = (Button) findViewById (R.id.step3);
        btn3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                tab3 ();
            }
        });

        etfname = findViewById (R.id.et_firstname);
        etlname = findViewById (R.id.et_lastname);
        etcon_num = findViewById (R.id.et_contact);
        etemail = findViewById (R.id.et_email);
        etage = findViewById (R.id.et_age);
        etaddress = findViewById (R.id.et_address);

        radioGroupRegisterGender = findViewById (R.id.radiogroup_p);
        radioGroupRegisterGender.clearCheck ();
        progressBar = findViewById (R.id.progressbar);

        save_btn = findViewById (R.id.save_btn);
        save_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                int selectedGenderID = radioGroupRegisterGender.getCheckedRadioButtonId ();
                radioButtonRegisterGenderSelected = findViewById (selectedGenderID);

                String pfname = etfname.getText ().toString ();
                String plname = etlname.getText ().toString ();
                String pcontactnumber = etcon_num.getText ().toString ();
                String pemail = etemail.getText ().toString ();
                String pgender;
                String page = etage.getText ().toString ();
                String paddress = etaddress.getText ().toString ();

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile (mobileRegex);

                mobileMatcher = mobilePattern.matcher (pcontactnumber);
                if (TextUtils.isEmpty (pfname))
                {
                    Toast.makeText (add_patient1.this, "Please enter first name", Toast.LENGTH_SHORT).show ();
                    etfname.setError ("FirstName is required");
                    etfname.requestFocus ();
                }
                else if(!pfname.matches("[a-zA-Z ]+"))
                {
                    etfname.requestFocus();
                    etfname.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else if (TextUtils.isEmpty (plname))
                {
                    Toast.makeText (add_patient1.this, "Please enter last name", Toast.LENGTH_SHORT).show ();
                    etlname.setError ("Last Name is required");
                    etlname.requestFocus ();
                }
                else if(!plname.matches("[a-zA-Z ]+"))
                {
                    etlname.requestFocus();
                    etlname.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else if (TextUtils.isEmpty (pcontactnumber))
                {
                    Toast.makeText (add_patient1.this, "Please enter contact number", Toast.LENGTH_SHORT).show ();
                    etcon_num.setError ("Contact Number  is required");
                    etcon_num.requestFocus ();
                }

                    else if (pcontactnumber.length () != 10)
                {
                    Toast.makeText (add_patient1.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    etcon_num.setError ("Contact Number  should be 10 digits");
                    etcon_num.requestFocus ();
                }
                    else if (!mobileMatcher.find ())      {
                    Toast.makeText (add_patient1.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    etcon_num.setError ("Contact Number  is not valid");
                    etcon_num.requestFocus ();
                }
                else if (TextUtils.isEmpty (pemail))
                {
                    Toast.makeText (add_patient1.this, "Please enter Email Address", Toast.LENGTH_SHORT).show ();
                    etemail.setError ("Email ID  is required");
                    etemail.requestFocus ();
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher (pemail).matches ())
                {
                    Toast.makeText (add_patient1.this, "Please re-enter Email Address", Toast.LENGTH_SHORT).show ();
                    etemail.setError ("Valid Email ID  is required");
                    etemail.requestFocus ();
                }
                else if (radioGroupRegisterGender.getCheckedRadioButtonId () == -1)
                {
                    Toast.makeText (add_patient1.this, "Please select Gender", Toast.LENGTH_SHORT).show ();

                }

                else if (TextUtils.isEmpty (page)) {
                    Toast.makeText (add_patient1.this, "Please enter age", Toast.LENGTH_SHORT).show ();
                    etage.setError ("Age is required");
                    etage.requestFocus ();
                }

                else if (TextUtils.isEmpty (paddress))
                {
                    Toast.makeText (add_patient1.this, "Please enter address", Toast.LENGTH_SHORT).show ();
                    etaddress.setError ("Address is required");
                    etaddress.requestFocus ();
                }
                else
                {
                    pgender = radioButtonRegisterGenderSelected.getText ().toString ();

                    Intent i1 = new Intent (getApplicationContext (), add_patient2.class);
                        i1.putExtra ("fname", pfname);
                    i1.putExtra ("lname", plname);
                    i1.putExtra ("con_num", pcontactnumber);
                    i1.putExtra ("email", pemail);
                    i1.putExtra ("gender", pgender);
                    i1.putExtra ("age", page);
                    i1.putExtra ("address", paddress);
                    Toast.makeText (add_patient1.this, "Saving data", Toast.LENGTH_SHORT).show ();
                    startActivity(i1);


                }
            }
        });


    }

    public void tab2() {
        Intent intent = new Intent (this, add_patient2.class);
        startActivity (intent);
    }

    public void tab3() {
        Intent intent = new Intent (this, add_patient3.class);
        startActivity (intent);
    }
}