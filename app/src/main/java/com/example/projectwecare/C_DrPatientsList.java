package com.example.projectwecare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class C_DrPatientsList extends AppCompatActivity {
    DrawerLayout drawerLayout;
    public FirebaseAuth authProfile;
    RecyclerView recyclerView;
    Myadapter_patient adapter;
    ArrayList<Allpatientview> list;
    private Myadapter_patient.RecyclerViewClickListener listener;
    private PreferenceManage preferenceManage;
    DatabaseReference ref;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (C_DrPatientsList.this, C_DrPatientsList.class));
        Toast.makeText (this, "Please Logout", Toast.LENGTH_SHORT).show ();
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cdr_patients_list);
        drawerLayout = findViewById (R.id.drawer_layout);
        authProfile = FirebaseAuth.getInstance ();
        preferenceManage = new PreferenceManage(getApplicationContext());
        recyclerView = findViewById (R.id.recyclerviewpatient);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        ref = FirebaseDatabase.getInstance().getReference("Registered Patients");
        setOnClickListener();
        list =new ArrayList<>();
        adapter = new Myadapter_patient (this,list,listener);
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
    public void ClickMenu(View view)
    {
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer (GravityCompat.START);
    }

    public void ClickLogo(View view) {
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen (GravityCompat.START)){
            drawerLayout.closeDrawer (GravityCompat.START);
        }
    }

    public void ClickPatients(View view){
        recreate();
    }

    public void ClickChat(View view){
        redirectActivity(this, chatDR.class);

    }

    public void ClickEditProfile(View view){
        redirectActivity(this, C_DrEditProfile.class);
    }

    public void ClickForgotPassword(View view){
        redirectActivity(this, ForgotPWD.class);

    }

    public void ClickContactAdmin(View view){
        redirectActivity(this, C_DrContactAdmin.class);
    }

    public void ClickLogout(View view) {
        alertLogout();
    }


    private void alertLogout() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure want to logout ?");
        dialog.setTitle("Log out ?");
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                authProfile.signOut ();
                Toast.makeText (C_DrPatientsList.this, "Logged out", Toast.LENGTH_SHORT).show ();
                Intent i = new Intent (C_DrPatientsList.this, Home_Activity.class);

                i.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (i);
                finish ();
            }
        });
        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss ();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }


    public static void redirectActivity(Activity activity, Class aClass){
        Intent intent = new Intent (activity, aClass);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause ();
        closeDrawer (drawerLayout);
    }

    private void setOnClickListener() {
        listener = (view, position) -> {
            preferenceManage.putString (Constants.KEY_USERID1, list.get (position).getPatient_id ());
            preferenceManage.putString (Constants.KEY_USERNAME1, list.get (position).getFname ());
            preferenceManage.putString (Constants.KEY_USERLLASTNAMEP, list.get(position).getLname ());
            Intent i = new Intent (getApplicationContext (), Discharge_Summary.class);
            startActivity (i);
        };
    }

}