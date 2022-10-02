package com.example.projectwecare;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class add_dr extends AppCompatActivity {
    String encodedImage;
    ImageView profilepic;
    TextInputEditText fullname_et,  seryears_et, contactnum_et, userid_et, email_et, password_et;
    Button idGenerateBtn;
    ProgressBar progressbar;
    String TAG = "add_dr";
    Spinner designation, speciality;
    String[] design_name = {"Senior Doctor", "Junior Doctor"};
    String[] speciality_name = {"Cardiologist", "General physician"};

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (add_dr.this, popup_window_dr.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_dr);

        profilepic = findViewById (R.id.profilepic);
        profilepic.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickimage.launch(intent);
            }
        });

        designation = findViewById (R.id.designation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, design_name);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        designation.setAdapter (adapter);
        designation.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                //Toast
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        speciality = findViewById (R.id.spl);
        ArrayAdapter<String> adapter_spl = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, speciality_name);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        speciality.setAdapter (adapter_spl);
        speciality.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                //Toast
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Toast.makeText (add_dr.this, "You can add doctor now", Toast.LENGTH_LONG).show ();

        fullname_et = findViewById (R.id.fullname);
        seryears_et = findViewById (R.id.service);
        contactnum_et = findViewById (R.id.phone);
        userid_et = findViewById (R.id.userid);
        email_et = findViewById (R.id.email);
        password_et = findViewById (R.id.password);
        progressbar = findViewById (R.id.progressbar);

        idGenerateBtn = findViewById (R.id.generate_id);
        idGenerateBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                GENERATE_ID();
            }
        });

        Button save_btn = findViewById (R.id.save_btn);
        save_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String textFullname = fullname_et.getText ().toString ();
                String textSpeciality = speciality.getSelectedItem().toString();
                String textServiceyears = seryears_et.getText ().toString ();
                String textDesignation = designation.getSelectedItem().toString();
                String textContactNumber = contactnum_et.getText ().toString ();
                String textUserid = userid_et.getText ().toString ();
                String textEmail = email_et.getText ().toString ();
                String textPwd = password_et.getText ().toString ();

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile (mobileRegex);
                mobileMatcher = mobilePattern.matcher (textContactNumber);
                if(encodedImage == null)
                {
                    Toast.makeText (add_dr.this, "Please select image", Toast.LENGTH_SHORT).show ();
                }
                else if (TextUtils.isEmpty (textFullname))
                {
                    Toast.makeText (add_dr.this, "Please enter full name", Toast.LENGTH_SHORT).show ();
                    fullname_et.setError ("Full Name is required");
                    fullname_et.requestFocus ();
                }
                else if(!textFullname.matches("[a-zA-Z ]+"))
                {
                    fullname_et.requestFocus();
                    fullname_et.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else if (TextUtils.isEmpty (textServiceyears))
                {
                    Toast.makeText (add_dr.this, "Please enter Service Years", Toast.LENGTH_SHORT).show ();
                    seryears_et.setError ("Service Years  is required");
                    seryears_et.requestFocus ();
                }
                else if (TextUtils.isEmpty (textContactNumber))
                {
                    Toast.makeText (add_dr.this, "Please enter contact number", Toast.LENGTH_SHORT).show ();
                    contactnum_et.setError ("Contact Number  is required");
                    contactnum_et.requestFocus ();
                }

                else if (textContactNumber.length () != 10)
                {
                    Toast.makeText (add_dr.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    contactnum_et.setError ("Contact Number  should be 10 digits");
                    contactnum_et.requestFocus ();

                }
                else if (!mobileMatcher.find ())
                {
                    Toast.makeText (add_dr.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    contactnum_et.setError ("Contact Number  is not valid");
                    contactnum_et.requestFocus ();
                }
                else if (TextUtils.isEmpty (textUserid))
                {
                    Toast.makeText (add_dr.this, "Click to generate User ID", Toast.LENGTH_SHORT).show ();
                    userid_et.setError ("User ID  is required");
                    userid_et.requestFocus ();
                }
                else if (TextUtils.isEmpty (textEmail))
                {
                    Toast.makeText (add_dr.this, "Please enter Email Address", Toast.LENGTH_SHORT).show ();
                    email_et.setError ("Email ID  is required");
                    email_et.requestFocus ();
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher (textEmail).matches ())
                {
                    Toast.makeText (add_dr.this, "Please re-enter Email Address", Toast.LENGTH_SHORT).show ();
                    email_et.setError ("Valid Email ID  is required");
                    email_et.requestFocus ();
                }
                else if (TextUtils.isEmpty (textPwd))
                {
                    Toast.makeText (add_dr.this, "Please enter password", Toast.LENGTH_SHORT).show ();
                    password_et.setError ("Password is required");
                    password_et.requestFocus ();
                }

                else if (textPwd.length () < 5)
                {
                    Toast.makeText (add_dr.this, "Password should be at least 5 digits", Toast.LENGTH_SHORT).show ();
                    password_et.setError ("Password too weak");
                    password_et.requestFocus ();
                }

                else
                {

                    Toast.makeText (add_dr.this, "Registering", Toast.LENGTH_SHORT).show ();
                    progressbar.setVisibility (View.VISIBLE);

                    saveDoctor(encodedImage, textFullname, textSpeciality, textServiceyears, textDesignation, textContactNumber, textUserid, textEmail, textPwd);
                }


            }
        });

    }

    private String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap , previewWidth , previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private final ActivityResultLauncher<Intent> pickimage = registerForActivityResult( new
            ActivityResultContracts.StartActivityForResult(),  result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                Uri uri = result.getData().getData();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    profilepic.setImageBitmap(bitmap);
                    encodedImage = encodedImage(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });




    private void GENERATE_ID() {
        RandomString randomString = new RandomString ();
        String result = randomString.generateAlphaNumeric ( 4);
        userid_et.setText (new StringBuilder ().append ("WCDoc").append (result).toString ());
    }


    private void saveDoctor(String encodedImage, String textFullname, String textSpeciality, String textServiceyears, String textDesignation,
                            String textContactNumber, String textUserid, String textEmail, String textPwd)
    {

        FirebaseAuth auth = FirebaseAuth.getInstance ();
        auth.createUserWithEmailAndPassword (textEmail, textPwd).addOnCompleteListener (this,
                new OnCompleteListener<AuthResult> () {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser ();
                            ReadWriteDoctorDetails writeDoctorDetails = new ReadWriteDoctorDetails(encodedImage , textFullname, textSpeciality, textServiceyears, textDesignation,
                                    textContactNumber, textUserid, textEmail, textPwd);
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance ().getReference ("Registered Doctors");
                            referenceProfile.child (firebaseUser.getUid ()).setValue (writeDoctorDetails).addOnCompleteListener (new OnCompleteListener<Void> () {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful ())
                                    {
                                        firebaseUser.sendEmailVerification ();
                                        showsuccess ("Success", "Doctor details are saved successfully. Please " +
                                                "verify Email to login !", "OK");

                                    }
                                    else
                                    {
                                        showerror ("Failed","Failed to add Doctor, Please try again", "OK");
                                    }
                                    progressbar.setVisibility (View.GONE);
                                }
                            });
                        }
                        else
                        {
                            try
                            {
                                throw task.getException ();
                            } catch (FirebaseAuthInvalidCredentialsException e)
                            {
                                password_et.setError ("Email is invalid or Already in use. Kindly re-enter");
                                password_et.requestFocus ();
                            } catch (FirebaseAuthUserCollisionException e)
                            {
                                password_et.setError ("One Doctor is already registered with this email. Use another email");
                                password_et.requestFocus ();
                            } catch (Exception e)
                            {
                                Log.e(TAG, e.getMessage ());
                                showerror ("Failed",e.getMessage (), "OK" );
                                Toast.makeText (add_dr.this, e.getMessage (), Toast.LENGTH_SHORT).show ();
                            }
                            progressbar.setVisibility (View.GONE);
                        }
                    }
                });


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> doctor = new HashMap<>();
        doctor.put(Constants.KEY_NAME,fullname_et.getText().toString());
        doctor.put(Constants.KEY_EMAIL,email_et.getText().toString());
        doctor.put(Constants.KEY_IMAGE,encodedImage);
        doctor.put(Constants.KEY_DESIGNATION,designation.getSelectedItem ().toString());
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(doctor)
                .addOnSuccessListener(documentReference -> {});
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
            profilepic.setImageResource(R.drawable.best_dr);
            fullname_et.setText ("");
            seryears_et.setText ("");
            contactnum_et.setText ("");
            userid_et.setText ("");
            email_et.setText ("");
            password_et.setText ("");
            Toast.makeText (add_dr.this, "closed", Toast.LENGTH_SHORT).show ();

            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


}