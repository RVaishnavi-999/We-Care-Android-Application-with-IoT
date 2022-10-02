package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class edit_patient extends AppCompatActivity {
    DatabaseReference dref;
    int h, m;
    TextInputEditText etfname, etlname, etemail, etage, etadd, et_ename, et_ephone, et_erel,
    et_admdate, et_admtime, et_cu, et_w, et_dr;
    Spinner spi_dr;
    String ppf, ppl, ppemail, ppage, ppadd, pename, pephone, perel, pdate, ptime, pcu, pw, pdr;
    ImageView iv_deluxroom, iv_splroom, iv_generalroom;
    Button change_d, change_t, update_btn;

    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (edit_patient.this, view_patient.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_edit_patient);

        PreferenceManage pref = new PreferenceManage (getApplicationContext ());

        TextView tvpid = findViewById (R.id.tvpid);
        TextView tvpfname = findViewById (R.id.tvpfname);
        TextView tvplname = findViewById (R.id.tvplname);
        TextView tvpphone = findViewById (R.id.tvpphone);

        tvpid.setText (pref.getString (Constants.KEY_USERID1));
        tvpfname.setText (pref.getString (Constants.KEY_USERNAME1));
        tvplname.setText (pref.getString (Constants.KEY_USERLLASTNAMEP));
        tvpphone.setText (pref.getString (Constants.KEY_PPHONE));

        etfname = findViewById (R.id.pfname);
        etfname.setEnabled (false);
        etlname = findViewById (R.id.plname);
        etlname.setEnabled (false);
        etemail = findViewById (R.id.pemail);
        etemail.setEnabled (false);
        etage = findViewById (R.id.page);
        etage.setEnabled (false);
        etadd = findViewById (R.id.padd);
        etadd.setEnabled (false);
        et_ename = findViewById (R.id.p_pemename);
        et_ename.setEnabled (false);
        et_ephone = findViewById (R.id.p_pemephone);
        et_ephone.setEnabled (false);
        et_erel = findViewById (R.id.p_pemerelative);
        et_erel.setEnabled (false);
        et_admdate = findViewById (R.id.et_admission_date);
        et_admdate.setEnabled (false);
        et_admtime = findViewById (R.id.et_admission_time);
        et_admtime.setEnabled (false);
        et_cu = findViewById (R.id.pcareunit);
        et_cu.setEnabled (false);
        et_w = findViewById (R.id.ppward);
        et_w.setEnabled (false);
        et_dr = findViewById (R.id.p_dr);
        et_dr.setEnabled (false);

        spi_dr = findViewById (R.id.spinner_dr);
        spinnerList = new ArrayList<> ();
        adapter = new ArrayAdapter<String> (getApplicationContext (), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, spinnerList);
        spi_dr.setAdapter (adapter);
        spinnerdr ();

//        spi_dr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
//            et_dr.setText(spi_dr.getSelectedItem().toString()); //this is taking the first value of the spinner by default.
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });



        Button get_data = findViewById (R.id.get_data_btn);
        get_data.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String pt_phone = tvpphone.getText ().toString ();
                if (!pt_phone.isEmpty()){
                    readData(pt_phone);
                }else{
                    showerror ("Oops !", "Some Internal error occurred" , "OK");
                }
            }
        });


        change_d = findViewById (R.id.btn_adm_date);
        change_d.setBackgroundColor (Color.parseColor("#D3D3D3"));
        change_d.setEnabled (false);
        change_d.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance ();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog picker = new DatePickerDialog (edit_patient.this, new DatePickerDialog.OnDateSetListener () {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_admdate.setText (dayOfMonth + "/" +(month+1)+ "/" + year);
                    }
                },  year, month, day);
                picker.show();
            }
        });

        change_t  = findViewById (R.id.btn_adm_time);
        change_t.setBackgroundColor (Color.parseColor("#D3D3D3"));
        change_t.setEnabled (false);
        change_t.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog (edit_patient.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener () {
                    @Override
                    public void onTimeSet(TimePicker view, int hoD, int min) {
                        h = hoD;
                        m = min;
                        String time = h + ":" + min;
                        SimpleDateFormat t24 = new SimpleDateFormat ("HH:mm");
                        try {
                            Date date = t24.parse (time);
                            SimpleDateFormat t12 = new SimpleDateFormat ("hh:mm aa");
                            et_admtime.setText (t12.format (date));
                        } catch (ParseException e) {
                            e.printStackTrace ();
                        }
                    }
                }, 12, 0, false);
                timePickerDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
                timePickerDialog.updateTime (h, m);
                timePickerDialog.show ();
            }
        });

        iv_deluxroom = findViewById (R.id.delux);
        iv_splroom = findViewById (R.id.special_room);
        iv_generalroom = findViewById (R.id.general_ward);
        iv_deluxroom.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                et_w.setText ("Deluxe Room");
            }
        });
        iv_splroom.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                et_w.setText ("Special Room");
            }
        });
        iv_generalroom.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                et_w.setText ("General Ward");
            }
        });

        update_btn = findViewById (R.id.update_btn);
        update_btn.setBackgroundColor (Color.parseColor ("#D3D3D3"));
        update_btn.setEnabled (false);
        update_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String pt_phone = tvpphone.getText ().toString ();
                updatedata (pt_phone);
            }
        });
    }


    private void updatedata(String pt_phone) {
         ppf = etfname.getText().toString();
         ppl = etlname.getText().toString();
         ppemail = etemail.getText().toString();
         ppage = etage.getText().toString();
         ppadd = etadd.getText().toString();
        pename = et_ename.getText().toString();
         pephone = et_ephone.getText().toString();
         perel = et_erel.getText().toString();
         pdate = et_admdate.getText().toString();
         ptime = et_admtime.getText().toString();
         pcu = et_cu.getText().toString();
         pw = et_w.getText().toString();
         pdr = spi_dr.getSelectedItem ().toString();

        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher2 ;
        Pattern mobilePattern = Pattern.compile (mobileRegex);
        mobileMatcher2 = mobilePattern.matcher (pephone);
        if (TextUtils.isEmpty (ppf))
        {
            Toast.makeText (edit_patient.this, "Please enter first name", Toast.LENGTH_SHORT).show ();
            etfname.setError ("First Name is required");
            etfname.requestFocus ();
        }
        else if(!ppf.matches("[a-zA-Z ]+"))
        {
            etfname.requestFocus();
            etfname.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else if(TextUtils.isEmpty (ppl))
        {
            Toast.makeText (edit_patient.this, "Please enter last name", Toast.LENGTH_SHORT).show ();
            etlname.setError ("Last Name is required");
            etlname.requestFocus ();
        }
        else if(!ppl.matches("[a-zA-Z ]+"))
        {
            etlname.requestFocus();
            etlname.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else if (TextUtils.isEmpty (ppemail))
        {
            Toast.makeText (edit_patient.this, "Please enter Email Address", Toast.LENGTH_SHORT).show ();
            etemail.setError ("Email ID  is required");
            etemail.requestFocus ();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher (ppemail).matches ())
        {
            Toast.makeText (edit_patient.this, "Please re-enter Email Address", Toast.LENGTH_SHORT).show ();
            etemail.setError ("Valid Email ID  is required");
            etemail.requestFocus ();
        }
        else if (TextUtils.isEmpty (ppage)) {
            Toast.makeText (edit_patient.this, "Please enter age", Toast.LENGTH_SHORT).show ();
            etage.setError ("Age is required");
            etage.requestFocus ();
        }
        else if (TextUtils.isEmpty (ppadd))
        {
            Toast.makeText (edit_patient.this, "Please enter address", Toast.LENGTH_SHORT).show ();
            etadd.setError ("Address is required");
            etadd.requestFocus ();
        }
        else if (TextUtils.isEmpty (pename))
        {
            Toast.makeText (edit_patient.this, "Please enter full name", Toast.LENGTH_SHORT).show ();
            et_ename.setError ("Full name is required");
            et_ename.requestFocus ();
        }
        else if(!pename.matches("[a-zA-Z ]+"))
        {
            et_ename.requestFocus();
            et_ename.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else if (TextUtils.isEmpty (pephone))
        {
            Toast.makeText (edit_patient.this, "Please enter contact number", Toast.LENGTH_SHORT).show ();
            et_ephone.setError ("Contact Number  is required");
            et_ephone.requestFocus ();
        }
        else if (pephone.length () != 10)
        {
            Toast.makeText (edit_patient.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
            et_ephone.setError ("Contact Number  should be 10 digits");
            et_ephone.requestFocus ();
        }
        else if (!mobileMatcher2.find ()) {
            Toast.makeText (edit_patient.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
            et_ephone.setError ("Contact Number  is not valid");
            et_ephone.requestFocus ();
        }
        else if (TextUtils.isEmpty (perel))
        {
            Toast.makeText (edit_patient.this, "Please enter relationship to the patient", Toast.LENGTH_SHORT).show ();
            et_erel.setError ("Relationship to patient is required");
            et_erel.requestFocus ();
        }
        else if(!perel.matches("[a-zA-Z ]+"))
        {
            et_erel.requestFocus();
            et_erel.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else if (TextUtils.isEmpty (pdate))
        {
            Toast.makeText (edit_patient.this, "Please click to generate Admission Date", Toast.LENGTH_SHORT).show ();
            et_admdate.setError ("Admission Date  is required");
            et_admdate.requestFocus ();
        }
        else if (TextUtils.isEmpty (ptime))
        {
            Toast.makeText (edit_patient.this, "Please click to generate Admission Time", Toast.LENGTH_SHORT).show ();
            et_admtime.setError ("Admission time  is required");
            et_admtime.requestFocus ();
        }
        else if (TextUtils.isEmpty (pcu))
        {
            Toast.makeText (edit_patient.this, "Please select a Unit", Toast.LENGTH_SHORT).show ();
            et_cu.setError ("This field cannot be empty");
            et_cu.requestFocus ();
        }
        else if(!pcu.matches("[a-zA-Z ]+"))
        {
            et_ename.requestFocus();
            et_ename.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else if (TextUtils.isEmpty (pw))
        {
            Toast.makeText (edit_patient.this, "Please select a Ward / Room", Toast.LENGTH_SHORT).show ();
            et_w.setError ("This field cannot be empty");
            et_w.requestFocus ();
        }
        else if(!pw.matches("[a-zA-Z ]+"))
        {
            et_w.requestFocus();
            et_w.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else
        {
            DatabaseReference dref = FirebaseDatabase.getInstance ().getReference ("Registered Patients");

            HashMap<String, Object> updatep = new HashMap<> ();
            updatep.put("fname", ppf);
            updatep.put ("lname", ppl);
            updatep.put ("email", ppemail);
            updatep.put ("age", ppage);
            updatep.put ("address", ppadd);
            updatep.put ("emergencyname", pename);
            updatep.put ("emergencyphone", pephone);
            updatep.put ("relationship", perel);
            updatep.put ("adm_date", pdate);
            updatep.put ("adm_time", ptime);
            updatep.put ("careunit", pcu);
            updatep.put ("ward", pw);
            updatep.put ("treating_dr_name", pdr);

            dref.child (pt_phone).updateChildren (updatep).addOnCompleteListener (new OnCompleteListener () {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        showsuccess ("Success", "Successfully Updated", "OK");
                        TextView tvpfname = findViewById (R.id.tvpfname);
                        TextView tvplname = findViewById (R.id.tvplname);
                        tvpfname.setText (ppf+ "  " );
                        tvplname.setText (ppl );

                        readData (pt_phone);
                    }
                    else
                    {
                        try
                        {
                            throw task.getException ();
                        }  catch (Exception e)
                        {
                            Toast.makeText (edit_patient.this, e.getMessage (), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    private void readData(String pt_phone) {
        dref = FirebaseDatabase.getInstance().getReference("Registered Patients");
        dref.child (pt_phone).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                UserPatients p = snapshot.getValue (UserPatients.class);
                if (p != null)
                {
                     ppf = p.fname;
                     ppl = p.lname;
                     ppemail = p.email;
                     ppage = p.age;
                     ppadd = p.address;
                     pename = p.emergencyname;
                     pephone = p.emergencyphone;
                     perel = p.relationship;
                     pdate = p.adm_date;
                     ptime = p.adm_time;
                     pcu = p.careunit;
                     pw = p.ward;
                     pdr = p.treating_dr_name;

                    etfname.setText (ppf);
                    etlname.setText (ppl);
                    etemail.setText (ppemail);
                    etage.setText (ppage);
                    etadd.setText (ppadd);
                    et_ename.setText (pename);
                    et_ephone.setText (pephone);
                    et_erel.setText (perel);
                    et_admdate.setText (pdate);
                    et_admtime.setText (ptime);
                    et_cu.setText (pcu);
                    et_w.setText (pw);
                    et_dr.setText (pdr);


                    etfname.setEnabled (true);
                    etlname.setEnabled (true);
                    etemail.setEnabled (true);
                    etage.setEnabled (true);
                    etadd.setEnabled (true);
                    et_ename.setEnabled (true);
                    et_ephone.setEnabled (true);
                    et_erel.setEnabled (true);
                    et_admdate.setEnabled (true);
                    change_d.setEnabled (true);
                    change_d.setBackgroundResource(R.drawable.shadow_button_);
                    et_admtime.setEnabled (true);
                    change_t.setEnabled (true);
                    change_t.setBackgroundResource(R.drawable.shadow_button_);
                    et_cu.setEnabled (true);
                    et_w.setEnabled (true);
                    update_btn.setEnabled (true);
                    update_btn.setBackgroundColor (Color.parseColor ("#FF018786"));
                }
                else
                {
                    showerror ("Oops !", "Something went wrong !", "OK");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror ("Oops !", "Something went wrong !", "OK");
            }
        });
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
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
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

}