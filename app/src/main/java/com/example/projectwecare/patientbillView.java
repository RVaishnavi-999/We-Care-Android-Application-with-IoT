package com.example.projectwecare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class patientbillView extends AppCompatActivity {

    RecyclerView recyclerView;
    dishargepatientadapter adapt;
    ArrayList<discharge_patientsModal> list;
    private dishargepatientadapter.RecyclerViewClickListener listener;
    ImageButton bk;
    ProgressBar progressBar;
    TextView txt;
    private PreferenceManage preferenceManage;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discharge_bill);

        txt = findViewById(R.id.viewText1);

        progressBar = findViewById(R.id.progressBar3);

        preferenceManage = new PreferenceManage(getApplicationContext());

        recyclerView = findViewById(R.id.RecyclerpatientView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setOnClickListener();
        list = new ArrayList<>();
        adapt = new dishargepatientadapter(this, list, listener);
        recyclerView.setAdapter(adapt);

        load();
    }

    private void load() {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Discharge_Summary");
        reference.orderByChild("status").equalTo("ready").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    discharge_patientsModal patient = snapshot1.getValue(discharge_patientsModal.class);
                    list.add(patient);
                }
                adapt.notifyDataSetChanged();
                check();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ");
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
            preferenceManage.putString(Constants.KEY_PATIENTID, list.get(position).getPid());
            preferenceManage.putString(Constants.KEY_PATIENTNAME, list.get(position).getPfname());
            preferenceManage.putString(Constants.KEY_PATIENTADVICE, list.get(position).getAdvice_discharge());
            preferenceManage.putString(Constants.KEY_PATIENTCONDITION, list.get(position).getCondition_discharge());
            preferenceManage.putString(Constants.KEY_PATIENTDIAGNOSIS, list.get(position).getConfirmed_diagnosis());
            preferenceManage.putString(Constants.KEY_PATIENTFOLLOWP, list.get(position).getFollowup());
            preferenceManage.putString(Constants.KEY_PATIENTPREEXIST, list.get(position).getPreexisting_condition());
            preferenceManage.putString(Constants.KEY_PATIENTTREATMENT, list.get(position).getTreatment_given());
            preferenceManage.putString(Constants.KEY_PATIENTTEST1, list.get(position).getReport1());
            preferenceManage.putString(Constants.KEY_PATIENTTEST2, list.get(position).getReport2());
            Intent in = new Intent(getApplicationContext(), bill_generate.class);
            startActivity(in);
        };
    }
}