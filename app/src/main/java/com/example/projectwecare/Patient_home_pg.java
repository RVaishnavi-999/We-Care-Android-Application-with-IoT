package com.example.projectwecare;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Patient_home_pg extends AppCompatActivity {
    DrawerLayout drawerLayout;
    DatabaseReference dref;
    TextInputEditText etphone;
    TextInputEditText  etfname, etlname, etconnum, etemail, etgender, etage, etaddress,
            eteme_name, eteme_phone, eteme_rel,
            etpid, etadmdate, etadmtime, etillness, etcareunit, etward, etdrname;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;

    PreferenceManage preferenceManage;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (Patient_home_pg.this, Home_Activity.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_patient_home_pg);
        drawerLayout = findViewById (R.id.drawer_layout);
        etphone = findViewById (R.id.phone);
        etfname = findViewById (R.id.tvpfname);
        etlname = findViewById (R.id.tvplname);
        etconnum = findViewById (R.id.tvpphone);
        etemail = findViewById (R.id.tvpemail);
        etgender = findViewById (R.id.tvpgender);
        etage = findViewById (R.id.tvpage);
        etaddress = findViewById (R.id.tvpaddress);

        eteme_name = findViewById (R.id.tv_pemename);
        eteme_phone = findViewById (R.id.tv_pemephone);
        eteme_rel = findViewById (R.id.tv_pemerelative);

        etpid = findViewById (R.id.tvpid);
        etadmdate = findViewById (R.id.tvpdate);
        etadmtime = findViewById (R.id.tvptime);
        etillness = findViewById (R.id.tvpillness);
        etcareunit = findViewById (R.id.tvcareunit);
        etward = findViewById (R.id.tvpward);
        etdrname = findViewById (R.id.tv_dr);

        preferenceManage = new PreferenceManage (getApplicationContext ());

        Button read_btn = findViewById (R.id.read_btn);
        read_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String pt_phone = etphone.getText ().toString ();
                if (!pt_phone.isEmpty()){
                    readData(pt_phone);
                }else{
                    Toast.makeText(Patient_home_pg.this,"Please Enter Registered Contact Number",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void readData(String pt_phone) {
        dref = FirebaseDatabase.getInstance().getReference("Registered Patients");
        dref.child (pt_phone).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserPatients p = snapshot.getValue (UserPatients.class);
                if (p != null)
                {
                    String fname = p.fname;
                    String lname = p.lname;
                    String con= p.contactnumbet;
                    String email = p.email;
                    String gender = p.gender;
                    String age = p.age;
                    String address = p.address;
                    String e_name = p.emergencyname;
                    String e_phone = p.emergencyphone;
                    String e_rel = p.relationship;
                    String pid = p.patient_id;
                    String date = p.adm_date;
                    String time = p.adm_time;
                    String ill = p.illness;
                    String cu = p.careunit;
                    String w = p.ward;
                    String dr = p.treating_dr_name;
                    String st = p.status;

                    preferenceManage.putString (Constants.KEY_COLLECTION_PID, pid);

                    etfname.setText (fname);
                    etlname.setText (lname);
                    etconnum.setText (con);
                    etemail.setText (email);
                    etgender.setText (gender);
                    etage.setText (age);
                    etaddress.setText (address);
                    eteme_name.setText (e_name);
                    eteme_phone.setText (e_phone);
                    eteme_rel.setText (e_rel);
                    etpid.setText (pid);
                    etadmdate.setText (date);
                    etadmtime.setText (time);
                    etillness.setText (ill);
                    etcareunit.setText (cu);
                    etward.setText (w);
                    etdrname.setText (dr);
                }
                else
                {
                    Toast.makeText (Patient_home_pg.this, "aaaaaaaaaaaaaaaaaaaa !", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText (Patient_home_pg.this, "Something went wrong !", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ClickMenuP(View view)
    {
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer (GravityCompat.START);
    }

    public void ClickLogoclose(View view) {
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen (GravityCompat.START)){
            drawerLayout.closeDrawer (GravityCompat.START);
        }
    }

    public void ClickSearch(View view){
        recreate();
    }

    public void ClickDownload(View view){
        String pid = preferenceManage.getString (Constants.KEY_COLLECTION_PID);
        storageReference = firebaseStorage.getInstance ().getReference ();
        ref = storageReference.child (pid + ".pdf");
        ref.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
            @Override
            public void onSuccess(Uri uri)
            {
                String url = uri.toString ();
                dlfiles(getApplicationContext (), pid , ".pdf", DIRECTORY_DOWNLOADS, url);
                showsuccess("Success", "Document saved successfully", "OK");
            }
        }).addOnFailureListener (new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
showerror ("Oops", "Failed to download the document", "OK");
            }
        });

    }

    private void dlfiles(Context context, String filename, String fileexe, String des_dir, String url) {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService (Context.DOWNLOAD_SERVICE);
        Uri uri=  Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request (uri);
        request.setNotificationVisibility (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir (context, des_dir, filename + fileexe);
        downloadManager.enqueue (request);
    }

    public void Clickhelpdesk(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData (Uri.parse("tel:9900814353"));
        startActivity(callIntent);
    }

    @Override
    protected void onPause() {
        super.onPause ();
        closeDrawer (drawerLayout);
    }

    private void showerror(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_error_dialog, (ConstraintLayout) findViewById(R.id.layoutdialogerrorContainer));
        builder.setView(view);

        TextView title, message;
        Button action;
        ImageView image;

        title = view.findViewById(R.id.textTitle2);
        message = view.findViewById(R.id.textMessage2);
        action = view.findViewById(R.id.buttonAction2);
        image = view.findViewById(R.id.imageIcon2);

        title.setText(title1);
        message.setText(message1);
        action.setText(action1);
        image.setImageResource(R.drawable.ic_error);

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable (0));
        }

        alertDialog.show();
    }

    private void showsuccess(String title1,String message1,String action1){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_success_dialog,(ConstraintLayout)findViewById(R.id.layoutdialogsuccessContainer));
        builder.setView(view);

        TextView title,message;
        Button action;
        ImageView image;

        title = view.findViewById(R.id.textTitle1);
        message = view.findViewById(R.id.textMessage1);
        action = view.findViewById(R.id.buttonAction1);
        image = view.findViewById(R.id.imageIcon1);

        title.setText(title1);
        message.setText(message1);
        action.setText(action1);
        image.setImageResource(R.drawable.ic_success);

        AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
}