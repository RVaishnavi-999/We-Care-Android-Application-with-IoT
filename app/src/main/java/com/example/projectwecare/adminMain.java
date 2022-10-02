package com.example.projectwecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class adminMain extends AppCompatActivity {

    BottomNavigationView btmnav;
    CardView patient, doctor, nurse, attend, shift, invoice;
    private PreferenceManage preferenceManage;
    AppCompatImageButton editshift, manualattend,pat,nv,dv;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity(new Intent(getApplicationContext(),adminMain.class));
        Toast.makeText(getApplicationContext(),"Logout First",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        preferenceManage = new PreferenceManage(getApplicationContext());

        patient = findViewById(R.id.Patient);
        doctor = findViewById(R.id.Doctor);
        nurse = findViewById(R.id.Nurse);
        attend = findViewById(R.id.Attendance);
        shift = findViewById(R.id.ShiftSchedular);
        invoice = findViewById(R.id.Invoice);
        viewPager2 = findViewById(R.id.viewpager2);

        manualattend = findViewById(R.id.manualattendance);
        editshift = findViewById(R.id.editshift);
        pat = findViewById(R.id.patientview);
        nv = findViewById(R.id.nurseview);
        dv = findViewById(R.id.doctorview);


        List<SliderItem> sliderItemList = new ArrayList<>();
        sliderItemList.add(new SliderItem(R.drawable.four));
        sliderItemList.add(new SliderItem(R.drawable.two));
        sliderItemList.add(new SliderItem(R.drawable.three));
        sliderItemList.add(new SliderItem(R.drawable.one));
        sliderItemList.add(new SliderItem(R.drawable.six));

        viewPager2.setAdapter(new SliderAdapter(sliderItemList, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(slideRunnable);
                sliderHandler.postDelayed(slideRunnable, 2000);
            }
        });

        pat.setOnClickListener(v ->{
            startActivity(new Intent(getApplicationContext(), attendpatientview.class));
        });

        nv.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), attendnurse.class));
        });

        dv.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), attenddoctor.class));
        });

        manualattend.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ManualAttendance.class));
        });

        editshift.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Nursevieweditshift.class));
        });

        doctor.setOnClickListener(v ->{
            startActivity(new Intent(getApplicationContext(),popup_window_dr.class));
        });

        nurse.setOnClickListener(v ->{
            startActivity(new Intent(getApplicationContext(),popup_window_nurse.class));
        });

        patient.setOnClickListener(v ->{
            startActivity(new Intent(getApplicationContext(),popup_window_patient.class));
        });

        attend.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Barattendance.class));
        });

        shift.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), viewShiftadd.class));
        });

        invoice.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), patientbillView.class));
        });

        btmnav = findViewById(R.id.nav_view);
        btmnav.setSelectedItemId(R.id.home1);

        btmnav.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.user1:
                    startActivity(new Intent(getApplicationContext(), AdminEditProfile.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.home1:
                    return true;

            }
            return false;
        });

    }

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(slideRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(slideRunnable, 2000);
    }

}