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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class attendpatientview extends AppCompatActivity {

    RecyclerView recyclerView;
    Myviewpadapter1 adapter;
    ArrayList<Allpatientview> list;
    private PreferenceManage preferenceManage;
    DatabaseReference ref;
    TextView txt;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent(getApplicationContext(), adminMain.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendpatientview);

        preferenceManage = new PreferenceManage(getApplicationContext());
        recyclerView = findViewById (R.id.recyclerview_patients1);
        progressBar = findViewById(R.id.progressBar21);
        txt = findViewById(R.id.viewText4);

        recyclerView.setLayoutManager (new LinearLayoutManager(this));
        ref = FirebaseDatabase.getInstance().getReference("Registered Patients");
        list =new ArrayList<>();
        adapter = new Myviewpadapter1 (this,list);
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

        check();
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
}