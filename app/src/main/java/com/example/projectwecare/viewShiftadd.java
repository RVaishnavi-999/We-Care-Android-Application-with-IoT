package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class viewShiftadd extends AppCompatActivity {

    RecyclerView recyclerView;
    shiftviewadapter adapter;
    ArrayList<ShiftScheduleModal> list;

    ProgressBar progressBar;
    FloatingActionButton newshift;
    TextView current;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), adminMain.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shift);

        progressBar = findViewById(R.id.progressBar7);
        newshift = findViewById(R.id.newshift);
        current = findViewById(R.id.current);

        recyclerView = findViewById(R.id.viewshiftRecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new shiftviewadapter(this, list);
        recyclerView.setAdapter(adapter);

        long millis = System.currentTimeMillis();
        java.util.Date date = new java.util.Date(millis);
        current.setText(String.valueOf(date));

        newshift.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ViewNurseaddShift.class));
        });

        currentdate();
    }

    ;

    private void currentdate() {

        String currentMonth = new SimpleDateFormat("MMyyyy", Locale.getDefault()).format(new Date());
        String cdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NewShiftScheduling");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                        String sdate = dataSnapshot.child("startShiftDate").getValue(String.class);
                        String edate = dataSnapshot.child("endShiftDate").getValue(String.class);

                        Date currentdate = null;
                        try {
                            currentdate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(cdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date startdate = null;
                        try {
                            startdate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(sdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date enddate = null;
                        try {
                            enddate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(edate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (currentdate.after(startdate) && currentdate.before(enddate) || currentdate.equals(startdate) || currentdate.equals(enddate)) {
                            ShiftScheduleModal s = dataSnapshot.getValue(ShiftScheduleModal.class);
                            list.add(s);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}
}