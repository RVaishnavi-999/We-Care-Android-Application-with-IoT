package com.example.projectwecare;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class view_dr extends AppCompatActivity {
    RecyclerView recyclerView;
    Myadapter_dr adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (view_dr.this, popup_window_dr.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_view_dr);

        recyclerView = findViewById (R.id.recyclerview);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));

        FirebaseRecyclerOptions<Alldrview> options =
                new FirebaseRecyclerOptions.Builder<Alldrview>()
                        .setQuery(FirebaseDatabase.getInstance ().getReference ("Registered Doctors"), Alldrview.class)
                        .build();

        adapter = new Myadapter_dr (options);
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