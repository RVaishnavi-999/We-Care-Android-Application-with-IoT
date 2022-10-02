package com.example.projectwecare;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class view_patient extends AppCompatActivity {
    RecyclerView recyclerView;
    Myviewpadapter adapter;
    ArrayList<Allpatientview> list;
    private Myviewpadapter.RecyclerViewClickListener listener;
    private PreferenceManage preferenceManage;
    DatabaseReference ref;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (view_patient.this, popup_window_patient.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_view_patient);

        preferenceManage = new PreferenceManage(getApplicationContext());
        recyclerView = findViewById (R.id.recyclerview_patients);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        ref = FirebaseDatabase.getInstance().getReference("Registered Patients");
        setOnClickListener();
        list =new ArrayList<>();
        adapter = new Myviewpadapter (this,list,listener);
        recyclerView.setAdapter(adapter);

        ref.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Allpatientview allpatientview = datasnapshot.getValue(Allpatientview.class);
                    list.add(allpatientview);
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
            preferenceManage.putString (Constants.KEY_USERID1, list.get (position).getPatient_id ());
            preferenceManage.putString (Constants.KEY_USERNAME1, list.get (position).getFname ());
            preferenceManage.putString (Constants.KEY_USERLLASTNAMEP, list.get(position).getLname ());
            preferenceManage.putString (Constants.KEY_PPHONE, list.get(position).getContactnumbet ());
            Intent i = new Intent (getApplicationContext (), edit_patient.class);
            startActivity (i);
        };
    }


}