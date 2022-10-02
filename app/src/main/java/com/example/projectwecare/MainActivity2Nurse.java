package com.example.projectwecare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity2Nurse extends AppCompatActivity {

    RecyclerView recyclerView;
    Myadapter_patient1 adapter;
    ArrayList<Allpatientview> list;
    private Myadapter_patient1.RecyclerViewClickListener listener;
    private PreferenceManage preferenceManage;
    DatabaseReference ref;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), MainActivity2Nurse.class));
        Toast.makeText (this, "Please Logout", Toast.LENGTH_SHORT).show ();
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2nurse);

        BottomNavigationView bnv = findViewById(R.id.btmnav);
        bnv.setSelectedItemId(R.id.home);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), MainActivityNurse.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), MainActivity3Nurse.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        preferenceManage = new PreferenceManage(getApplicationContext());
        recyclerView = findViewById (R.id.recyclerview_patients2);
        recyclerView.setLayoutManager (new LinearLayoutManager(this));
        ref = FirebaseDatabase.getInstance().getReference("Registered Patients");
        setOnClickListener();
        list =new ArrayList<>();
        adapter = new Myadapter_patient1 (this,list,listener);
        recyclerView.setAdapter(adapter);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    String status = datasnapshot.child("status").getValue(String.class);
                    if(status.equalsIgnoreCase("Admit")){
                        Allpatientview allpatientview = datasnapshot.getValue(Allpatientview.class);
                        list.add(allpatientview);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ");
            }
        });
    }

    private void setOnClickListener() {

        listener = (view, position) -> {
            preferenceManage.putString(Constants.KEY_PATIENTNAMEIOT,list.get (position).getFname());
            preferenceManage.putString(Constants.KEY_PATIENTIDIOT,list.get (position).getPatient_id ());
            preferenceManage.putString(Constants.KEY_PATIENTCAREUNITIOT,list.get (position).getCareunit());
            Intent i = new Intent (getApplicationContext (), PatientIotView.class);
            startActivity (i);
        };
    }

}