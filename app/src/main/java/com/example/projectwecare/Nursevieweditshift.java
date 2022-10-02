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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Nursevieweditshift extends AppCompatActivity {

    DatabaseReference reference1;
    RecyclerView recyclerView;
    nurseviewadapter adapter;
    ArrayList<Allnurseview> list;
    private nurseviewadapter.RecyclerViewClickListener listener;
    ImageButton back;
    ProgressBar progressBar;
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
        setContentView(R.layout.activity_swapshift);

        progressBar = findViewById(R.id.progressBar9);

        progressBar.setVisibility(View.VISIBLE);

        preferenceManage = new PreferenceManage(getApplicationContext());

        recyclerView = findViewById(R.id.nurseList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setOnClickListener();

        list = new ArrayList<>();
        adapter = new nurseviewadapter(this, list, listener);
        recyclerView.setAdapter(adapter);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
        String str = formatter.format(date);

        reference1 = FirebaseDatabase.getInstance().getReference("ShiftScheduling").child(str);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    String id = snapshot1.child("id").getValue(String.class);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Nurses");

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String newid = dataSnapshot1.child("nurseID").getValue(String.class);
                                if(newid.equalsIgnoreCase(id)){
                                    Allnurseview n = dataSnapshot1.getValue(Allnurseview.class);
                                    list.add(n);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: ");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            preferenceManage.putString(Constants.KEY_USERID, list.get(position).getNurseID());
            preferenceManage.putString(Constants.KEY_USERNAME, list.get(position).getFname());
            preferenceManage.putString(Constants.KEY_USEREMAIL, list.get(position).getEmail());
            Intent in = new Intent(getApplicationContext(), shiftdateshow.class);
            startActivity(in);
        };
    }
}