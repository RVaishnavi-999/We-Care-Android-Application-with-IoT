package com.example.projectwecare;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class C_DrEditProfile extends AppCompatActivity {
    String encodedImage;
    private CircleImageView etimageProfile;
    DrawerLayout drawerLayout;
    private EditText etUpdateFullName, etUpdateSpeciality, etUpdateServiceYears, etUpdateDesig, etUpdateContactNumber,
            etUpdateUserID, etUpdatePWD;
    private TextView  tv_show_email;
    private  Button btnUpdateEmail, btnUpdatePwd;
    private String  textFullname,  textSpeciality,  textServiceyears,  textDesignation,
            textContactNumber,  textUserid, textEmail, textPwd;

    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    //r
    ImageView qrcode;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (C_DrEditProfile.this, C_DrPatientsList.class));
        finish ();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cdr_edit_profile);
        drawerLayout = findViewById (R.id.drawer_layout);
        etimageProfile = findViewById(R.id.profilepic2);
        etimageProfile.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickimage.launch(intent);
            }
        });

        progressBar = findViewById (R.id.progressbar);
        etUpdateFullName = findViewById (R.id.et_update_fullname);
        etUpdateSpeciality = findViewById (R.id.et_update_speciality);
        etUpdateServiceYears = findViewById (R.id.et_update_serviceyears);
        etUpdateDesig = findViewById (R.id.et_update_desig);
        etUpdateContactNumber = findViewById (R.id.et_update_contactnumber);
        etUpdateUserID = findViewById (R.id.et_update_uid);

        tv_show_email = findViewById (R.id.tv_show_email);

        etUpdatePWD = findViewById (R.id.et_update_pwd);

        //r
        qrcode = findViewById (R.id.imageBar);


        etUpdateSpeciality.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                alertDialog1();
            }
        });


        etUpdateDesig.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                alertDialog2 ();
            }
        });


        etUpdateUserID.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                alertDialog3 ();
            }
        });

        authProfile = FirebaseAuth.getInstance ();
        FirebaseUser firebaseUser = authProfile.getCurrentUser ();

        if (firebaseUser == null)
        {
            showerror ("Oops !", "Something went wrong! Doctor details are not available at the moment.", "OK");
        }
        else
        {
            showProfile(firebaseUser);
        }

        btnUpdateEmail = findViewById (R.id.update_email);
        btnUpdateEmail.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (C_DrEditProfile.this, UpdateEmailActivity.class);
                startActivity (i);
                finish();
            }
        });

        Button update_profile_btn = findViewById (R.id.updateprofile_btn);
        update_profile_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);    //method call
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {
        textFullname = etUpdateFullName.getText ().toString ();
        textSpeciality = etUpdateSpeciality.getText ().toString ();
        textServiceyears = etUpdateServiceYears.getText ().toString ();
        textDesignation = etUpdateDesig.getText ().toString ();
        textContactNumber = etUpdateContactNumber.getText ().toString ();
        textUserid = etUpdateUserID.getText ().toString ();
        textEmail = tv_show_email.getText ().toString ();
        textPwd =  etUpdatePWD.getText ().toString ();
        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile (mobileRegex);

        mobileMatcher = mobilePattern.matcher (textContactNumber);
        if (TextUtils.isEmpty (textFullname))
        {
            Toast.makeText (C_DrEditProfile.this, "Please enter full name", Toast.LENGTH_SHORT).show ();
            etUpdateFullName.setError ("Full Name is required");
            etUpdateFullName.requestFocus ();
        }
        else if(!textFullname.matches("[a-zA-Z ]+"))
        {
            etUpdateFullName.requestFocus();
            etUpdateFullName.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }
        else if (TextUtils.isEmpty (textServiceyears))
        {
            Toast.makeText (C_DrEditProfile.this, "Please enter Service Years", Toast.LENGTH_SHORT).show ();
            etUpdateServiceYears.setError ("Service Years  is required");
            etUpdateServiceYears.requestFocus ();
        }
        else if (TextUtils.isEmpty (textContactNumber))
        {
            Toast.makeText (C_DrEditProfile.this, "Please enter contact number", Toast.LENGTH_SHORT).show ();
            etUpdateContactNumber.setError ("Contact Number  is required");
            etUpdateContactNumber.requestFocus ();
        }

        else if (textContactNumber.length () != 10)
        {
            Toast.makeText (C_DrEditProfile.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
            etUpdateContactNumber.setError ("Contact Number  should be 10 digits");
            etUpdateContactNumber.requestFocus ();
        }
        else if (!mobileMatcher.find ()) {
            Toast.makeText (C_DrEditProfile.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
            etUpdateContactNumber.setError ("Contact Number is not valid");
            etUpdateContactNumber.requestFocus ();
        }
        else
        {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance ().getReference ("Registered Doctors");
            String userID = firebaseUser.getUid ();
            progressBar.setVisibility (View.VISIBLE);
            HashMap<String, Object> writedr = new HashMap<> ();
            writedr.put("fullname", textFullname);
            writedr.put("speciality", textSpeciality);
            writedr.put("service_yeras", textServiceyears);
            writedr.put("designation", textDesignation);
            writedr.put("contactnum", textContactNumber);
            writedr.put("userid", textUserid);
            writedr.put("email", textEmail);

            referenceProfile.child (userID).updateChildren (writedr).addOnCompleteListener (new OnCompleteListener<Void> () {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful ()) {
                        showsuccess ("Success", "Update Successful", "OK");
                    }
                    else
                    {
                        try
                        {
                            throw task.getException ();
                        }  catch (Exception e)
                        {
                            Toast.makeText (C_DrEditProfile.this, e.getMessage (), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility (View.GONE);
                }
            });
        }
    }
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid ();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance ().getReference ("Registered Doctors");
        progressBar.setVisibility (View.VISIBLE);
        referenceProfile.child (userIDofRegistered).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteDoctorDetails readDoctorDetails = snapshot.getValue (ReadWriteDoctorDetails.class);
                if (readDoctorDetails != null)  {

                    encodedImage = readDoctorDetails.encodedImage;
                    textFullname = readDoctorDetails.fullname;
                    textSpeciality = readDoctorDetails.speciality;
                    textServiceyears = readDoctorDetails.service_yeras;
                    textDesignation = readDoctorDetails.designation;
                    textContactNumber = readDoctorDetails.contactnum;
                    textUserid = readDoctorDetails.userid;
                    textEmail = firebaseUser.getEmail () ;
                    textPwd = readDoctorDetails.pwd;
                    byte[] bytes = Base64.decode(snapshot.child("encodedImage").getValue(String.class),Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                    etimageProfile.setImageBitmap(bitmap);
                    etUpdateFullName.setText (textFullname);
                    etUpdateSpeciality.setText (textSpeciality);
                    etUpdateServiceYears.setText (textServiceyears);
                    etUpdateDesig.setText (textDesignation);
                    etUpdateContactNumber.setText (textContactNumber);
                    etUpdateUserID.setText (textUserid);
                    tv_show_email.setText (textEmail);
                    etUpdatePWD.setText (textPwd);

                    String txt = snapshot.child("userid").getValue(String.class);
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writer.encode(txt, BarcodeFormat.QR_CODE, 150, 150);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        Bitmap bitmap1 = encoder.createBitmap(matrix);
                        qrcode.setImageBitmap(bitmap1);
                    } catch (WriterException e) {
                        e.printStackTrace ();
                    }
                }
                else
                {
                    showerror ("Oops !", "Something went wrong ! ", "OK");
                }
                progressBar.setVisibility (View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror ("Oops !", "Something went wrong ! ", "OK");
                progressBar.setVisibility (View.GONE);
            }
        });
    }


    public void ClickMenu(View view) {
        C_DrPatientsList.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        C_DrPatientsList.closeDrawer (drawerLayout);
    }

    public void ClickPatients(View view) {
        C_DrPatientsList.redirectActivity (this, C_DrPatientsList.class);
    }

    public void ClickChat(View view){
        //redirect activity to C_DrChat
        C_DrPatientsList.redirectActivity (this, chatDR.class);
    }

    public void ClickEditProfile(View view) {
        recreate ();
    }

    public void ClickForgotPassword(View view){
        C_DrPatientsList.redirectActivity (this, ForgotPWD.class);

    }

    public void ClickContactAdmin(View view){
        C_DrPatientsList.redirectActivity (this, C_DrContactAdmin.class);

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

                Toast.makeText (C_DrEditProfile.this, "Logged out", Toast.LENGTH_SHORT).show ();

                Intent i = new Intent (C_DrEditProfile.this, Home_Activity.class);
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

    private void alertDialog1() {
        AlertDialog.Builder dialog=new AlertDialog.Builder (this);
        dialog.setMessage("To change the Speciality field, Please contact admin");
        dialog.setTitle("Alert !");
        dialog.setPositiveButton ("OK", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss ();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void alertDialog2() {
        AlertDialog.Builder dialog=new AlertDialog.Builder (this);
        dialog.setMessage("To change the Designation field, Please contact admin");
        dialog.setTitle("Alert !");
        dialog.setPositiveButton ("OK", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss ();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void alertDialog3() {
        AlertDialog.Builder dialog=new AlertDialog.Builder (this);
        dialog.setMessage("UserID cannot be changed");
        dialog.setTitle("Alert !");
        dialog.setPositiveButton ("OK", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss ();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause ();
        C_DrPatientsList.closeDrawer (drawerLayout);
    }


    private String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap , previewWidth , previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickimage = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri uri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            etimageProfile.setImageBitmap(bitmap);
                            encodedImage = encodedImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void showerror(String title1, String message1, String action1) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
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

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable (0));
        }

        alertDialog.show();
    }

    private void showsuccess(String title1,String message1,String action1){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
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

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {

            Intent intent1= new Intent (C_DrEditProfile.this, C_DrEditProfile.class);
            intent1.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

}