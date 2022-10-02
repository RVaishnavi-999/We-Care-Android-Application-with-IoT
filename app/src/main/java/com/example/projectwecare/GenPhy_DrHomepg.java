package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class GenPhy_DrHomepg extends AppCompatActivity {
    DrawerLayout drawerLayout;
    public FirebaseAuth authProfile;
    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (GenPhy_DrHomepg.this, GenPhy_DrHomepg.class));
        Toast.makeText (this, "Please Logout", Toast.LENGTH_SHORT).show ();
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_gen_phy_dr_homepg);
        drawerLayout = findViewById (R.id.drawer_layout);
        authProfile = FirebaseAuth.getInstance ();
    }

    public void ClickMenu2(View view)
    {
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer (GravityCompat.START);
    }

    public void ClickLogo2(View view) {
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen (GravityCompat.START)){
            drawerLayout.closeDrawer (GravityCompat.START);
        }
    }

    public void ClickHome(View view){
        recreate();
    }

    public void ClickChat2(View view){
         redirectActivity(this, chat_jnr_dr.class);

    }

    public void ClickEditProfile2(View view){
        redirectActivity(this, EditProfile2.class);
    }

    public void ClickForgotPassword2(View view){
        redirectActivity(this, ForgotPWD_2.class);

    }

    public void ClickContactAdmin2(View view){
        redirectActivity(this, ContactAdmin2.class);
    }

    public void ClickLogout2(View view) {
        alertLogout();
    }

    private void alertLogout() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure want to logout ?");
        dialog.setTitle("Log out ?");
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                authProfile.signOut ();

                Toast.makeText (GenPhy_DrHomepg.this, "Logged out",  Toast.LENGTH_SHORT).show ();
                Intent i = new Intent (GenPhy_DrHomepg.this, Home_Activity.class);
                i.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (i);
                finish ();
            }
        });
        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss ();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }


    public static void redirectActivity(Activity activity, Class aClass)
    {
        Intent intent = new Intent (activity, aClass);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause ();
        closeDrawer (drawerLayout);
    }

}