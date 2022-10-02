package com.example.projectwecare;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class view_nurse extends AppCompatActivity {
    RecyclerView recyclerView;
    Myadapter_nurse adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (view_nurse.this, popup_window_nurse.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_view_nurse);

        recyclerView = findViewById (R.id.recyclerview);

        recyclerView = findViewById (R.id.recyclerview);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));

        FirebaseRecyclerOptions<Allnurseview> options =
                new FirebaseRecyclerOptions.Builder<Allnurseview>()
                        .setQuery(FirebaseDatabase.getInstance ().getReference ("Registered Nurses"), Allnurseview.class)
                        .build();

        adapter = new Myadapter_nurse (options);
        recyclerView.setAdapter (adapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}