package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class AddShift extends AppCompatActivity {

    SwitchCompat mySwitch = null;
    AppCompatButton mon, tue, wed, thu, fri, sat, sun, holiday;
    String chk_color = "blue";
    int i = 0;
    String def = "SUN";
    Button save;
    PreferenceManage preferenceManage;
    TextView showdate1, showdate2, showto, name, viewoldshift;
    Button opencal;
    DatePickerDialog datePickerDialog;

    //add data value
    final String[] enddate = {" "};
    final String[] startdate = {" "};
    String shift = "Day";

    String useremail;

    String endDate;

    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), ViewNurseaddShift.class));
        finish ();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shift);

        progressBar = findViewById(R.id.progressBar4);
        mon = findViewById(R.id.monday);
        tue = findViewById(R.id.tueday);
        wed = findViewById(R.id.wednesday);
        thu = findViewById(R.id.thursday);
        fri = findViewById(R.id.friday);
        sat = findViewById(R.id.saturday);
        sun = findViewById(R.id.sunday);
        holiday = findViewById(R.id.holiday);

        save = findViewById(R.id.savechanges);

        ImageButton viewshift = findViewById(R.id.viewshifts);

        showdate1 = findViewById(R.id.dateSelected1);
        showdate2 = findViewById(R.id.dateSelected2);
        showto = findViewById(R.id.showto);
        opencal = findViewById(R.id.openCalendar);

        viewoldshift = findViewById(R.id.viewoldshift);

        name = findViewById(R.id.shiftname);

        preferenceManage = new PreferenceManage(getApplicationContext());
        name.setText(preferenceManage.getString(Constants.KEY_USERNAME));

        useremail = preferenceManage.getString(Constants.KEY_USEREMAIL);

        opencal.setOnClickListener(v -> datevalidate());

        viewshift.setOnClickListener(v -> showdialog());

        mySwitch = findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chk_color = "darkblue";
                shift = "Night";
                setcolor(def);
            } else {
                chk_color = "blue";
                shift = "Day";
                setcolor(def);
            }
        });

        holiday.setOnClickListener(v -> {
            String[] days = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            def = days[i];
            holiday.setText(days[i]);
            setcolor(days[i]);
            if (i >= 6) {
                i = 0;
            } else {
                i++;
            }
        });

        save.setOnClickListener(v -> {
            if (IsValidate()) {
                progressBar.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                saveData();
            }
        });

        setViewoldshift();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void showdialog() {

        final int[] day = {0};
        final int[] night = { 0 };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_viewshift
                , findViewById(R.id.layoutviewshift));
        builder.setView(view);

        TextView title, message1, message2, message3;
        Button action;

        title = view.findViewById(R.id.textTitle3);
        message1 = view.findViewById(R.id.textviewday);
        message2 = view.findViewById(R.id.textviewnight);
        message3 = view.findViewById(R.id.textviewholiday);
        action = view.findViewById(R.id.buttonAction3);

        ArrayList<String> holi = new ArrayList<>();

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        String currentdate = format.format(date);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShiftScheduling").child(currentdate);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String shiftType = dataSnapshot.child("ShiftType").getValue(String.class);
                    holi.add(dataSnapshot.child("Holiday").getValue(String.class));

                    if (shiftType.equalsIgnoreCase("Night")) {
                        night[0] +=1;
                    } else {
                        day[0] +=1;
                    }
                }

                message1.setText("Number of day shift : " + day[0]);
                message2.setText("Number of night shift : " + night[0]);
                message3.setText("Holiday assigned : " + Arrays.toString(holi.toArray()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        title.setText("View all shifts");
        action.setText("Done");

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void setViewoldshift() {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
        String str = formatter.format(date);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShiftScheduling").child(str)
                .child(preferenceManage.getString(Constants.KEY_USERID));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewoldshift.setText(String.valueOf(snapshot.child("ShiftType").getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error", "Previous Shift not found", "Ok");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveData() {

        DatabaseReference reference;

        String currentDate = null;

        try {
            Date out = new SimpleDateFormat("dd-MM-yyyy").parse(showdate1.getText().toString());
            currentDate = new SimpleDateFormat("MM-yyyy").format(out);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reference = FirebaseDatabase.getInstance().getReference("ShiftScheduling")
                .child(currentDate).child(preferenceManage.getString(Constants.KEY_USERID));

        HashMap<String, Object> user = new HashMap<>();
        user.put("name", preferenceManage.getString(Constants.KEY_USERNAME));
        user.put("id",preferenceManage.getString(Constants.KEY_USERID));
        user.put("StartShiftDate", showdate1.getText().toString());
        user.put("EndShiftDate", showdate2.getText().toString());
        user.put("ShiftType", shift);
        user.put("Holiday", def);

        reference.setValue(user)
                .addOnCompleteListener(task -> originalSaveData())
                .addOnFailureListener(e -> showerror("Error", e.getMessage(), "Ok"));

    }

    private void sendemail() {
        try {

            final String email = "rachana110298@gmail.com";
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
                            return new PasswordAuthentication(email, pass);
                        }
                    });

            MimeMessage message = new MimeMessage(session);

            String mess =
                    "Shift type :" + shift +
                            "\n Starting Date :" + showdate1.getText().toString() +
                            "\n Ending Date :" + showdate2.getText().toString() +
                            "\n\nHoliday is on " + def + " in between your shift dates \n\nThank you, \n WeCare Hospital";
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(useremail));
            message.setSubject("Regarding of your Shift Turn");
            message.setText(mess);

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
            showsuccess("Shift Scheduled", "Shift is marked successfully and mail has been sent.", "Ok");
            progressBar.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private void showDatePickerRange(int mYear, int mMonth, int mDay) {

        final Calendar c = Calendar.getInstance();

        c.set(mYear, mMonth, mDay);

        // date picker dialog
        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String startDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        try {
                            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
                            c.setTime(date1);
                            c.add(Calendar.DATE, 6);
                            Date newdate = c.getTime();
                            enddate[0] = new SimpleDateFormat("dd-MM-yyyy").format(newdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        startdate[0] = startDate;
                        showdate1.setText(startDate);
                        showto.setVisibility(View.VISIBLE);
                        showdate2.setText(enddate[0]);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        c.add(Calendar.DATE, 28);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        datePickerDialog.show();
    }

    private void datevalidate() {
        final Calendar c = Calendar.getInstance();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
        String str = formatter.format(date);

        final int[] mYear = new int[1];
        final int[] mMonth = new int[1];
        final int[] mDay = new int[1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ShiftScheduling")
                .child(str).child(preferenceManage.getString(Constants.KEY_USERID));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                endDate = snapshot.child("EndShiftDate").getValue(String.class);
                if (endDate != null) {
                    String dateParts[] = endDate.split("-");

                    String day = dateParts[0];
                    String month = dateParts[1];
                    String year = dateParts[2];

                    mYear[0] = Integer.parseInt(year);
                    mMonth[0] = Integer.parseInt(month);
                    mDay[0] = Integer.parseInt(day);

                    showDatePickerRange(mYear[0], mMonth[0] - 1, mDay[0] + 1);

                } else {

                    mYear[0] = c.get(Calendar.YEAR);
                    mMonth[0] = c.get(Calendar.MONTH);
                    mDay[0] = c.get(Calendar.DAY_OF_MONTH);

                    showDatePickerRange(mYear[0], mMonth[0], mDay[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error", error.getMessage().toString(), "Ok");
            }
        });
    }

    private void setcolor(String day) {
        if (day.equalsIgnoreCase("MON")) {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkred));
                tue.setTextColor(getResources().getColor(R.color.darkblue));
                wed.setTextColor(getResources().getColor(R.color.darkblue));
                thu.setTextColor(getResources().getColor(R.color.darkblue));
                fri.setTextColor(getResources().getColor(R.color.darkblue));
                sat.setTextColor(getResources().getColor(R.color.darkblue));
                sun.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                mon.setTextColor(getResources().getColor(R.color.darkred));
                tue.setTextColor(getResources().getColor(R.color.blue));
                wed.setTextColor(getResources().getColor(R.color.blue));
                thu.setTextColor(getResources().getColor(R.color.blue));
                fri.setTextColor(getResources().getColor(R.color.blue));
                sat.setTextColor(getResources().getColor(R.color.blue));
                sun.setTextColor(getResources().getColor(R.color.blue));
            }
        } else if (day.equalsIgnoreCase("TUE")) {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkblue));
                tue.setTextColor(getResources().getColor(R.color.darkred));
                wed.setTextColor(getResources().getColor(R.color.darkblue));
                thu.setTextColor(getResources().getColor(R.color.darkblue));
                fri.setTextColor(getResources().getColor(R.color.darkblue));
                sat.setTextColor(getResources().getColor(R.color.darkblue));
                sun.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                mon.setTextColor(getResources().getColor(R.color.blue));
                tue.setTextColor(getResources().getColor(R.color.darkred));
                wed.setTextColor(getResources().getColor(R.color.blue));
                thu.setTextColor(getResources().getColor(R.color.blue));
                fri.setTextColor(getResources().getColor(R.color.blue));
                sat.setTextColor(getResources().getColor(R.color.blue));
                sun.setTextColor(getResources().getColor(R.color.blue));
            }
        } else if (day.equalsIgnoreCase("WED")) {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkblue));
                tue.setTextColor(getResources().getColor(R.color.darkblue));
                wed.setTextColor(getResources().getColor(R.color.darkred));
                thu.setTextColor(getResources().getColor(R.color.darkblue));
                fri.setTextColor(getResources().getColor(R.color.darkblue));
                sat.setTextColor(getResources().getColor(R.color.darkblue));
                sun.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                mon.setTextColor(getResources().getColor(R.color.blue));
                tue.setTextColor(getResources().getColor(R.color.blue));
                wed.setTextColor(getResources().getColor(R.color.darkred));
                thu.setTextColor(getResources().getColor(R.color.blue));
                fri.setTextColor(getResources().getColor(R.color.blue));
                sat.setTextColor(getResources().getColor(R.color.blue));
                sun.setTextColor(getResources().getColor(R.color.blue));
            }
        } else if (day.equalsIgnoreCase("THU")) {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkblue));
                tue.setTextColor(getResources().getColor(R.color.darkblue));
                wed.setTextColor(getResources().getColor(R.color.darkblue));
                thu.setTextColor(getResources().getColor(R.color.darkred));
                fri.setTextColor(getResources().getColor(R.color.darkblue));
                sat.setTextColor(getResources().getColor(R.color.darkblue));
                sun.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                mon.setTextColor(getResources().getColor(R.color.blue));
                tue.setTextColor(getResources().getColor(R.color.blue));
                wed.setTextColor(getResources().getColor(R.color.blue));
                thu.setTextColor(getResources().getColor(R.color.darkred));
                fri.setTextColor(getResources().getColor(R.color.blue));
                sat.setTextColor(getResources().getColor(R.color.blue));
                sun.setTextColor(getResources().getColor(R.color.blue));
            }
        } else if (day.equalsIgnoreCase("FRI")) {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkblue));
                tue.setTextColor(getResources().getColor(R.color.darkblue));
                wed.setTextColor(getResources().getColor(R.color.darkblue));
                thu.setTextColor(getResources().getColor(R.color.darkblue));
                fri.setTextColor(getResources().getColor(R.color.darkred));
                sat.setTextColor(getResources().getColor(R.color.darkblue));
                sun.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                mon.setTextColor(getResources().getColor(R.color.blue));
                tue.setTextColor(getResources().getColor(R.color.blue));
                wed.setTextColor(getResources().getColor(R.color.blue));
                thu.setTextColor(getResources().getColor(R.color.blue));
                fri.setTextColor(getResources().getColor(R.color.darkred));
                sat.setTextColor(getResources().getColor(R.color.blue));
                sun.setTextColor(getResources().getColor(R.color.blue));
            }
        } else if (day.equalsIgnoreCase("SAT")) {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkblue));
                tue.setTextColor(getResources().getColor(R.color.darkblue));
                wed.setTextColor(getResources().getColor(R.color.darkblue));
                thu.setTextColor(getResources().getColor(R.color.darkblue));
                fri.setTextColor(getResources().getColor(R.color.darkblue));
                sat.setTextColor(getResources().getColor(R.color.darkred));
                sun.setTextColor(getResources().getColor(R.color.darkblue));
            } else {
                mon.setTextColor(getResources().getColor(R.color.blue));
                tue.setTextColor(getResources().getColor(R.color.blue));
                wed.setTextColor(getResources().getColor(R.color.blue));
                thu.setTextColor(getResources().getColor(R.color.blue));
                fri.setTextColor(getResources().getColor(R.color.blue));
                sat.setTextColor(getResources().getColor(R.color.darkred));
                sun.setTextColor(getResources().getColor(R.color.blue));
            }
        } else {
            if (chk_color.equalsIgnoreCase("darkblue")) {
                mon.setTextColor(getResources().getColor(R.color.darkblue));
                tue.setTextColor(getResources().getColor(R.color.darkblue));
                wed.setTextColor(getResources().getColor(R.color.darkblue));
                thu.setTextColor(getResources().getColor(R.color.darkblue));
                fri.setTextColor(getResources().getColor(R.color.darkblue));
                sat.setTextColor(getResources().getColor(R.color.darkblue));
                sun.setTextColor(getResources().getColor(R.color.darkred));
            } else {
                mon.setTextColor(getResources().getColor(R.color.blue));
                tue.setTextColor(getResources().getColor(R.color.blue));
                wed.setTextColor(getResources().getColor(R.color.blue));
                thu.setTextColor(getResources().getColor(R.color.blue));
                fri.setTextColor(getResources().getColor(R.color.blue));
                sat.setTextColor(getResources().getColor(R.color.blue));
                sun.setTextColor(getResources().getColor(R.color.darkred));
            }
        }
    }

    private Boolean IsValidate() {
        if (showdate1.getText().toString().isEmpty()) {
            showerror("Error", "Pick a date", "Ok");
            return false;
        } else {
            return true;
        }
    }

    private void originalSaveData() {
        DatabaseReference reference;

        String StartShiftDate = showdate1.getText().toString();
        String EndShiftDate = showdate2.getText().toString();
        String id = preferenceManage.getString(Constants.KEY_USERID);
        String name = preferenceManage.getString(Constants.KEY_USERNAME);

        String currentDate = null,currentDay = null;
        try {
            Date out = new SimpleDateFormat("dd-MM-yyyy").parse(EndShiftDate);
            currentDate = new SimpleDateFormat("MMyyyy").format(out);
            currentDay = new SimpleDateFormat("dd").format(out);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String newid = currentDay+""+id;

        reference = FirebaseDatabase.getInstance().getReference("NewShiftScheduling").child(currentDate).child(newid);

        ShiftScheduleModal shiftSchedule = new ShiftScheduleModal(StartShiftDate, EndShiftDate, shift, def, id, name);

        reference.setValue(shiftSchedule);

        sendemail();
        clearall();
    }

    private void clearall() {
        showdate1.setText("");
        showdate2.setText("");
        showto.setVisibility(View.INVISIBLE);
        def = "SUN";
        chk_color = "blue";
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

        action.setOnClickListener(v -> alertDialog.dismiss());

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

        action.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
}