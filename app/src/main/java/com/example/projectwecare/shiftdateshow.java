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

public class shiftdateshow extends AppCompatActivity {

    TextView txt;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    shiftdateshowadapter adapter;
    ArrayList<ShiftScheduleModal> list;
    PreferenceManage preferenceManage;
    private shiftdateshowadapter.RecyclerViewClickListener listener;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), Nursevieweditshift.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftdateshow);

        preferenceManage = new PreferenceManage(getApplicationContext());
        recyclerView = findViewById(R.id.nurseshiftList);
        progressBar = findViewById(R.id.progressBar11);
        txt = findViewById(R.id.viewText5);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setOnClickListener();

        list = new ArrayList<>();
        adapter = new shiftdateshowadapter(this,list,listener);
        recyclerView.setAdapter(adapter);

        current();
    }

    private void current() {
        progressBar.setVisibility(View.VISIBLE);

        String cdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Date currentdate = null;
        try {
            currentdate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(cdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NewShiftScheduling");
        Date finalCurrentdate = currentdate;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot2:snapshot.getChildren()){
                    for(DataSnapshot snapshot1: snapshot2.getChildren()){
                        String startshift = snapshot1.child("startShiftDate").getValue(String.class);


                        Date sdate = null;
                        try {
                            sdate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(startshift);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String id = snapshot1.child("id").getValue(String.class);

                        if(id.equalsIgnoreCase(preferenceManage.getString(Constants.KEY_USERID)) && (finalCurrentdate.equals(sdate) ||  finalCurrentdate.before(sdate)) ){
                            ShiftScheduleModal s = snapshot1.getValue(ShiftScheduleModal.class);
                            list.add(s);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                check();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void check(){
        if(list.size() > 0){
            progressBar.setVisibility(View.INVISIBLE);
            txt.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            preferenceManage.putString(Constants.KEY_SHIFTCURRENT, list.get(position).getStartShiftDate());
            preferenceManage.putString(Constants.KEY_SHIFTEND, list.get(position).getEndShiftDate());
            preferenceManage.putString(Constants.KEY_SHIFTID, list.get(position).getId());
            preferenceManage.putString(Constants.KEY_SHIFTHOLIDAY, list.get(position).getHoliday());
            preferenceManage.putString(Constants.KEY_SHIFTYPE, list.get(position).getShiftType());
            Intent in = new Intent(getApplicationContext(), editnurseshift.class);
            startActivity(in);
        };
    }
}