package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class add_patient3 extends AppCompatActivity {
    String TAG = "add_patient3";

    FirebaseDatabase db;
    DatabaseReference reference;
    EditText et_patient_id, et_admission_date, et_admission_time, et_selectedcareunit, et_selected_ward;
    Spinner illness_name, spi_t_dr;
    CardView cardView1, cardView2, cardView3, cardView4;
    ImageView iv_deluxroom, iv_splroom, iv_generalroom;
    Button save_btn;

    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;


    ArrayList<String> Lists;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (add_patient3.this, popup_window_patient.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_patient3);

        Lists = new ArrayList<>();
        careunit();

        Button btn1 = (Button) findViewById (R.id.step1);
        btn1.setBackgroundResource (R.drawable.selected_btn);
        btn1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                tab1 ();
            }
        });


        Button btn2 = (Button) findViewById (R.id.step2);
        btn2.setBackgroundResource (R.drawable.selected_btn);
        btn2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                tab2 ();
            }
        });

        Button btn3 = findViewById (R.id.step3);
        btn3.setBackgroundResource (R.drawable.selected_btn);


        Button patient_id_genearte_btn = findViewById (R.id.generate_id);
        patient_id_genearte_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                PATIENT_Id_GENERATE ();
            }
        });


        Button btn_adm_date = findViewById (R.id.btn_adm_date);
        btn_adm_date.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                ADMISSION_DATE ();
            }
        });


        Button btn_adm_time = findViewById (R.id.btn_adm_time);
        btn_adm_time.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                ADMISSION_TIME ();
            }
        });

        illness_name = findViewById (R.id.illness);
        String[] illname_list = {"Select one !", "Angioplasty", "Open-heart Surgery", "Bypass Surgery", "Aneurysm Surgery", "Heart transplant"};
        ArrayAdapter<String> adapter_ill = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, illname_list);
        adapter_ill.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        illness_name.setAdapter (adapter_ill);


        et_selectedcareunit = findViewById (R.id.et_care_unit_name);
        et_selected_ward = findViewById (R.id.et_ward_name);
        spi_t_dr = findViewById (R.id.spinner_dr);

        spinnerList = new ArrayList<> ();
        adapter = new ArrayAdapter<String> (getApplicationContext (), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, spinnerList);
        spi_t_dr.setAdapter (adapter);
        spinnerdr ();


        cardView1 = findViewById (R.id.spm_card1);
        cardView2 = findViewById (R.id.spm_card2);
        cardView3 = findViewById (R.id.spm_card3);
        cardView4 = findViewById (R.id.spm_card4);

        iv_deluxroom = findViewById (R.id.delux);
        iv_splroom = findViewById (R.id.special_room);
        iv_generalroom = findViewById (R.id.general_ward);

        cardView1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (cardView1.getCardBackgroundColor ().getDefaultColor () == -1) {

                    cardView1.setCardBackgroundColor (Color.parseColor ("#bcfebc"));
                    et_selectedcareunit.setText ("ICU");
                    cardView2.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView3.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView4.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                }
            }
        });
        cardView2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (cardView2.getCardBackgroundColor ().getDefaultColor () == -1) {

                    cardView2.setCardBackgroundColor (Color.parseColor ("#bcfebc"));
                    et_selectedcareunit.setText ("ICCU");
                    cardView1.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView3.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView4.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                }
            }
        });
        cardView3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (cardView3.getCardBackgroundColor ().getDefaultColor () == -1) {

                    cardView3.setCardBackgroundColor (Color.parseColor ("#bcfebc"));
                    et_selectedcareunit.setText ("SICU");
                    cardView1.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView2.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView4.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                }
            }
        });
        cardView4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (cardView4.getCardBackgroundColor ().getDefaultColor () == -1) {

                    cardView4.setCardBackgroundColor (Color.parseColor ("#bcfebc"));
                    et_selectedcareunit.setText ("PICU");
                    cardView1.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView2.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                    cardView3.setCardBackgroundColor (Color.parseColor ("#FFFFFF"));
                }
            }
        });

        iv_deluxroom.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                et_selected_ward.setText ("Deluxe Room");
            }
        });
        iv_splroom.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                et_selected_ward.setText ("Special Room");
            }
        });
        iv_generalroom.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                et_selected_ward.setText ("General Ward");
            }
        });

        save_btn = findViewById (R.id.save_btn);
        save_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i31 = getIntent ();
                String p_fname = i31.getStringExtra ("Pfname");
                String p_lname = i31.getStringExtra ("Plname");
                String p_con_num = i31.getStringExtra ("Pconnum");
                String p_email = i31.getStringExtra ("Pemail");
                String p_gender = i31.getStringExtra ("Pgender");
                String p_age = i31.getStringExtra ("Page");
                String p_address = i31.getStringExtra ("Paddress");

                String p_eme_name = i31.getStringExtra ("P_eme_name");
                String p_eme_phone = i31.getStringExtra ("P_eme_phone");
                String p_eme_relative = i31.getStringExtra ("P_eme_relative");

                String patient_id = et_patient_id.getText ().toString ();
                String adm_date = et_admission_date.getText ().toString ();
                String adm_time = et_admission_time.getText ().toString ();
                String illness = illness_name.getSelectedItem ().toString ();
                String careunit = et_selectedcareunit.getText ().toString ();
                String ward = et_selected_ward.getText ().toString ();
                String treating_dr_name = spi_t_dr.getSelectedItem ().toString ();

                if (TextUtils.isEmpty (patient_id)) {
                    Toast.makeText (add_patient3.this, "Please click to generate patient ID", Toast.LENGTH_SHORT).show ();
                    et_patient_id.setError ("Patient ID is required");
                    et_patient_id.requestFocus ();
                    et_patient_id.setFocusable (true);
                    et_patient_id.setClickable (true);
                    et_patient_id.setCursorVisible (true);
                    et_patient_id.setFocusableInTouchMode (true);

                } else if (TextUtils.isEmpty (adm_date)) {
                    Toast.makeText (add_patient3.this, "Please click to generate Admission Date", Toast.LENGTH_SHORT).show ();
                    et_admission_date.setError ("Admission Date  is required");
                    et_admission_date.requestFocus ();
                } else if (TextUtils.isEmpty (adm_time)) {
                    Toast.makeText (add_patient3.this, "Please click to generate Admission Time", Toast.LENGTH_SHORT).show ();
                    et_admission_time.setError ("Admission time  is required");
                    et_admission_time.requestFocus ();
                } else if (TextUtils.isEmpty (careunit)) {
                    Toast.makeText (add_patient3.this, "Please select a Unit", Toast.LENGTH_SHORT).show ();
                    et_selectedcareunit.setError ("This field cannot be empty");
                    et_selectedcareunit.requestFocus ();
                } else if (TextUtils.isEmpty (ward)) {
                    Toast.makeText (add_patient3.this, "Please select a Ward / Room", Toast.LENGTH_SHORT).show ();
                    et_selected_ward.setError ("This field cannot be empty");
                    et_selected_ward.requestFocus ();
                } else if (TextUtils.isEmpty (treating_dr_name)) {
                    Toast.makeText (add_patient3.this, "Please select Treating Doctor name", Toast.LENGTH_SHORT).show ();
                } else {


                    savePatient (p_fname, p_lname, p_con_num, p_email, p_gender, p_age, p_address, p_eme_name, p_eme_phone, p_eme_relative,
                            patient_id, adm_date, adm_time, illness, careunit, ward, treating_dr_name, "Admit");
                }

            }
        });


    }

    private void savePatient(String p_fname, String p_lname, String p_con_num, String p_email, String p_gender, String p_age, String p_address,
                             String p_eme_name, String p_eme_phone, String p_eme_relative,
                             String patient_id, String adm_date, String adm_time, String illness,
                             String careunit, String ward, String treating_dr_name, String status) {

        UserPatients userpatients = new UserPatients (p_fname, p_lname, p_con_num, p_email, p_gender, p_age, p_address,
                p_eme_name, p_eme_phone, p_eme_relative,
                patient_id, adm_date, adm_time,
                illness, careunit, ward, treating_dr_name, status);

        db = FirebaseDatabase.getInstance ();
        reference = db.getReference ("Registered Patients");
        reference.child (p_con_num).setValue (userpatients).addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful ()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CareUnit").child(careunit);
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("status", "blocked");
                    databaseReference.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showsuccess ("Success", "Patient added successfully", "OK");
                        }
                    });
                } else {
                    try {
                        throw task.getException ();
                    } catch (Exception e) {

                        Log.e (TAG, e.getMessage ());
                        Toast.makeText (add_patient3.this, e.getMessage (), Toast.LENGTH_SHORT).show ();
                    }
                    showerror ("Failed", "Failed to add data,, Please try again", "OK");

                }
            }
        });
    }

    public void tab1() {
        Intent intent = new Intent (this, add_patient1.class);
        startActivity (intent);
    }


    public void tab2() {
        Intent intent = new Intent (this, add_patient2.class);
        startActivity (intent);
    }


    private void PATIENT_Id_GENERATE() {
        RandomString randomString = new RandomString ();
        String result = randomString.generateAlphaNumeric (4);

        et_patient_id = findViewById (R.id.et_patient_id);
        et_patient_id.setEnabled (false);
        et_patient_id.setText (new StringBuilder ().append ("WCP").append (result).toString ());
    }


    private void ADMISSION_DATE() {
        Date c = Calendar.getInstance ().getTime ();

        SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy", Locale.getDefault ());
        String current_date = df.format (c);
        et_admission_date = findViewById (R.id.et_admission_date);
        et_admission_date.setEnabled (false);

        et_admission_date.setText (current_date);
    }


    private void ADMISSION_TIME() {
        et_admission_time = findViewById (R.id.et_admission_time);
        et_admission_time.setEnabled (false);

        Calendar calendar = Calendar.getInstance ();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("hh:mm:ss: a");

        String current_time = "" + simpleDateFormat.format (calendar.getTime ());

        et_admission_time.setText ("" + current_time);
    }

    private void showerror(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder (this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from (this).inflate (R.layout.layout_error_dialog, (ConstraintLayout) findViewById (R.id.layoutdialogerrorContainer));
        builder.setView (view);

        TextView title, message;
        Button action;
        ImageView image;

        title = view.findViewById (R.id.textTitle2);
        message = view.findViewById (R.id.textMessage2);
        action = view.findViewById (R.id.buttonAction2);
        image = view.findViewById (R.id.imageIcon2);

        title.setText (title1);
        message.setText (message1);
        action.setText (action1);
        image.setImageResource (R.drawable.ic_error);

        AlertDialog alertDialog = builder.create ();

        action.setOnClickListener (v -> {
            alertDialog.dismiss ();
        });

        if (alertDialog.getWindow () != null) {
            alertDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (0));
        }

        alertDialog.show ();
    }

    private void showsuccess(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder (this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from (this).inflate (R.layout.layout_success_dialog, (ConstraintLayout) findViewById (R.id.layoutdialogsuccessContainer));
        builder.setView (view);

        TextView title, message;
        Button action;
        ImageView image;

        title = view.findViewById (R.id.textTitle1);
        message = view.findViewById (R.id.textMessage1);
        action = view.findViewById (R.id.buttonAction1);
        image = view.findViewById (R.id.imageIcon1);

        title.setText (title1);
        message.setText (message1);
        action.setText (action1);
        image.setImageResource (R.drawable.ic_success);

        AlertDialog alertDialog = builder.create ();

        action.setOnClickListener (v -> {
            alertDialog.dismiss ();
        });

        if (alertDialog.getWindow () != null) {
            alertDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (0));
        }

        alertDialog.show ();
    }

    private void spinnerdr() {
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("Registered Doctors");
        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren ()) {

                    String spl = dataSnapshot1.child ("speciality").getValue (String.class);

                    if (spl.equalsIgnoreCase ("Cardiologist") ) {
                        spinnerList.add (dataSnapshot1.child ("fullname").getValue (String.class));
                    }
                }
                adapter.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // block care unit based on status
    private void careunit(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CareUnit");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String status = dataSnapshot.child("status").getValue(String.class);
                    if(status.equalsIgnoreCase("blocked")){
                        Lists.add(dataSnapshot.child("careunit").getValue(String.class));
                    }
                }
                careunitset(Lists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void careunitset(ArrayList<String> lists) {
        for (String list : Lists){
            if(list.equalsIgnoreCase("ICU")){
                cardView1.setEnabled(false);
                cardView1.setCardBackgroundColor (Color.parseColor ("#F75D59"));
            }
            else if(list.equalsIgnoreCase("ICCU")){
                cardView2.setEnabled(false);
                cardView2.setCardBackgroundColor (Color.parseColor ("#F75D59"));
            }
            else if(list.equalsIgnoreCase("SICU")){
                cardView3.setEnabled(false);
                cardView3.setCardBackgroundColor (Color.parseColor ("#F75D59"));
            }
            else{
                cardView4.setEnabled(false);
                cardView4.setCardBackgroundColor (Color.parseColor ("#F75D59"));
            }
        }
    }
}

