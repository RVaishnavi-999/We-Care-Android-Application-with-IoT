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

import java.util.ArrayList;

public class ViewNurseaddShift extends AppCompatActivity {

    RecyclerView r;
    DatabaseReference ref;
    nurseviewadapter adapt;
    ArrayList<Allnurseview> list;
    private nurseviewadapter.RecyclerViewClickListener listener;
    ImageButton bk;
    ProgressBar progressBar;
    private PreferenceManage preferenceManage;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), viewShiftadd.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        progressBar = findViewById(R.id.progressBar1);

        preferenceManage = new PreferenceManage(getApplicationContext());

        r = findViewById(R.id.userList);
        ref = FirebaseDatabase.getInstance().getReference("Registered Nurses");
        r.setHasFixedSize(true);
        r.setLayoutManager(new LinearLayoutManager(this));

        setOnClickListener();
        list = new ArrayList<>();
        adapt = new nurseviewadapter(this, list, listener);
        r.setAdapter(adapt);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Allnurseview n = datasnapshot.getValue(Allnurseview.class);
                    list.add(n);
                }
                adapt.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ");
            }
        });
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            preferenceManage.putString(Constants.KEY_USERID, list.get(position).getNurseID());
            preferenceManage.putString(Constants.KEY_USERNAME, list.get(position).getFname());
            preferenceManage.putString(Constants.KEY_USEREMAIL, list.get(position).getEmail());
            Intent in = new Intent(getApplicationContext(), AddShift.class);
            startActivity(in);
        };
    }
}