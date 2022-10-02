package com.example.projectwecare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class editnurseshift extends AppCompatActivity {

    TextView name, shift, enddate, startdate, to;
    Spinner nursespinner;
    TextInputEditText reason;
    Button save;
    PreferenceManage preferenceManage;
    String id, email;
    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;
    String originalenddate;
    Date date = new Date();
    ProgressBar progressBar;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    String nurseemail1,nurseemail2;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), shiftdateshow.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swapnurseshift);

        String str = formatter.format(date);

        preferenceManage = new PreferenceManage(getApplicationContext());

        name = findViewById(R.id.shiftname1);
        shift = findViewById(R.id.previousshift);
        startdate = findViewById(R.id.datestart);
        enddate = findViewById(R.id.dateend);
        to = findViewById(R.id.showto1);
        progressBar = findViewById(R.id.progressBar10);

        nursespinner = findViewById(R.id.nursespinner);

        reason = findViewById(R.id.swapreason);

        save = findViewById(R.id.swapchanges);

        save.setOnClickListener(v -> {
            try {
                if (validate()) {
                    progressBar.setVisibility(View.VISIBLE);
                    save.setVisibility(View.INVISIBLE);
                    swapnurse();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        id = preferenceManage.getString(Constants.KEY_USERID);
        email = preferenceManage.getString(Constants.KEY_USEREMAIL);
        originalenddate = preferenceManage.getString(Constants.KEY_SHIFTEND);

        spinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, spinnerList);
        nursespinner.setAdapter(adapter);

        setprofile();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void setprofile() {
        name.setText(preferenceManage.getString(Constants.KEY_USERNAME));
        shift.setText(preferenceManage.getString(Constants.KEY_SHIFTYPE));
        startdate.setText(preferenceManage.getString(Constants.KEY_SHIFTCURRENT));
        enddate.setText(preferenceManage.getString(Constants.KEY_SHIFTEND));
        spinnernurse();
        //check();
    }

    private void spinnernurse() {

        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String str = formatter.format(date);

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("NewShiftScheduling");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot2.getChildren()) {
                        String newid = snapshot1.child("id").getValue(String.class);
                        String enddate1 = snapshot1.child("endShiftDate").getValue(String.class);
                        String startdate1 = snapshot1.child("startShiftDate").getValue(String.class);
                        String newshift = snapshot1.child("shiftType").getValue(String.class);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Nurses");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    String nurseid = dataSnapshot1.child("nurseID").getValue(String.class);

                                    if (nurseid.equalsIgnoreCase(id) || !nurseid.equalsIgnoreCase(newid) || newshift.equalsIgnoreCase(preferenceManage.getString(Constants.KEY_SHIFTYPE)) || !enddate1.equalsIgnoreCase(preferenceManage.getString(Constants.KEY_SHIFTEND))) {
                                        continue;
                                    } else {
                                        spinnerList.add(nurseid);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ");
            }
        });

    }

    private void swapnurse() throws ParseException {

        String selectedid = nursespinner.getSelectedItem().toString();

        String enddate = preferenceManage.getString(Constants.KEY_SHIFTEND);
        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(enddate);

        String type = preferenceManage.getString(Constants.KEY_SHIFTYPE);
        String newtype;

        if (type.equalsIgnoreCase("Night")) {
            newtype = "Day";
        } else {
            newtype = "Night";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String str = formatter.format(date1);

        SimpleDateFormat formatter1 = new SimpleDateFormat("dd");
        String str1 = formatter1.format(date1);

        String newid = str1 + "" + id;

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("NewShiftScheduling").child(str).child(newid);
        HashMap<String, Object> user = new HashMap<>();
        user.put("shiftType", newtype);
        reference1.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String newid1 = str1 + "" + selectedid;
                findemail1(newtype);
                swapnurseextra(type, str, newid1);
            }
        });
    }

    private void swapnurseextra(String type, String str, String newid1) {

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("NewShiftScheduling").child(str).child(newid1);
        HashMap<String, Object> user1 = new HashMap<>();
        user1.put("shiftType", type);
        reference2.updateChildren(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                findemail2(type);
                addreason(newid1);
            }
        });
    }

    private void findemail1(String newtype) {;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Nurses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String nursenewid = snapshot1.child("nurseID").getValue(String.class);
                    if (nursenewid.equals(id)) {
                        nurseemail1 = snapshot1.child("email").getValue(String.class);
                        sendemail(newtype,nurseemail1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findemail2(String type) {
        String nurseid = nursespinner.getSelectedItem().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Nurses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String nursenewid = snapshot1.child("nurseID").getValue(String.class);
                    if (nursenewid.equals(nurseid)) {
                        nurseemail2 = snapshot1.child("email").getValue(String.class);
                        sendemail(type,nurseemail2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addreason(String newid1) {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        String str = formatter.format(date);

        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("NurseShiftReason").child(str);
        HashMap<String, Object> user3 = new HashMap<>();
        user3.put("NurseId", id);
        user3.put("SwapNurseId", newid1);
        user3.put("Reason", reason.getText().toString());
        reference3.setValue(user3).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                showsuccess("Successful", "Nurse shift is changed", "Ok");
            }
        });
    }

    private void sendemail(String type, String useremail) {
        try {

            final String emailids = "rachana110298@gmail.com";
            final String pass = "cgxfgbzahgexftye";

            String host = "smtp.gmail.com";

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.ssl.enable", "true");

            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailids , pass);
                        }
                    });

            MimeMessage message = new MimeMessage(session);

            String mess =
                    "Shift type :" + type +
                            "\n Starting Date :" + preferenceManage.getString(Constants.KEY_SHIFTCURRENT) +
                            "\n Ending Date :" + preferenceManage.getString(Constants.KEY_SHIFTEND) +
                            " \n\nThank you, \n WeCare Hospital";
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(useremail));
            message.setSubject("Regarding of your Change Shift Turn");
            message.setText(mess);

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });

            thread.start();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

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
            startActivity(new Intent(getApplicationContext(), Nursevieweditshift.class));
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
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

    private void showerror1(String title1, String message1, String action1) {
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
            startActivity(new Intent(getApplicationContext(), shiftdateshow.class));
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private boolean validate() {
        if (reason.getText().toString().trim().isEmpty()) {
            showerror("Empty field ", "Please enter the reason", "Ok");
            return false;
        } else if (nursespinner.getSelectedItem().toString().isEmpty()) {
            showerror("Empty field ", "Please select Id", "Ok");
            return false;
        } else {
            return true;
        }
    }

    private void check() {
        if (spinnerList.isEmpty()) {
            showerror1("Error", "No Nurse Found", "Ok");
        }
    }
}