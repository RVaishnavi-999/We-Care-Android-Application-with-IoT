package com.example.projectwecare;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class add_nurse extends AppCompatActivity {

    String encodedImage;
    ImageView profilepicnurse;

    FirebaseStorage storage;
    StorageReference storageReference;

    TextInputEditText etfname, etlname, etDate, etcon_num, etaddress, etnurseid, etemail, etpwd;
     Button idGenerateBtn;
     ProgressBar progressBar;
     RadioGroup radioGroupRegisterGender;
     RadioButton radioButtonRegisterGenderSelected;
     String TAG = "add_nurse";

    private Spinner spinner;
    String[] name = {"General Duty", "Emergency Care"};
    private DatePickerDialog picker;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (add_nurse.this, popup_window_nurse.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_nurse);

        storage = FirebaseStorage.getInstance ();
        storageReference = storage.getReference ();

        Toast.makeText (this, "You can add nurse details now", Toast.LENGTH_SHORT).show ();

        profilepicnurse = findViewById (R.id.profilepic);
        profilepicnurse.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickimage.launch(intent);
            }
        });

        etfname = findViewById (R.id.fname);
        etlname = findViewById (R.id.lname);
        etcon_num = findViewById (R.id.phone);
        etaddress = findViewById (R.id.address);
        etnurseid = findViewById (R.id.userid);
        etemail = findViewById (R.id.email_add);
        etpwd = findViewById (R.id.password_add);

        radioGroupRegisterGender = findViewById (R.id.radio_gp);
        radioGroupRegisterGender.clearCheck ();
        progressBar = findViewById (R.id.progressbar);


        etDate = findViewById (R.id.etDate);

        etDate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance ();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);


                picker = new DatePickerDialog (add_nurse.this, new DatePickerDialog.OnDateSetListener () {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDate.setText (dayOfMonth + "/" +(month+1)+ "/" + year);
                    }
                },  year, month, day);
                picker.show();
            }
        });


        spinner = findViewById (R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, name);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter (adapter);

        spinner.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        idGenerateBtn = findViewById (R.id.generate_id);
        idGenerateBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                GENERATE_ID();
            }
        });

        Button register_btn = (Button) findViewById (R.id.save_btn);
        register_btn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                int selectedGenderID = radioGroupRegisterGender.getCheckedRadioButtonId ();
                radioButtonRegisterGenderSelected = findViewById (selectedGenderID);

                String textFname = etfname.getText ().toString ();
                String textLname = etlname.getText ().toString ();
                String textDob = etDate.getText ().toString ();
                String textCon_num = etcon_num.getText ().toString ();
                String textAddress = etaddress.getText ().toString ();
                String textDutyshift = spinner.getSelectedItem().toString();
                String textNurseid = etnurseid  .getText ().toString ();
                String textEmail = etemail.getText ().toString ();
                String textPwd = etpwd.getText ().toString ();
                String textGender;

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile (mobileRegex);

                mobileMatcher = mobilePattern.matcher (textCon_num);



                if(encodedImage == null)
                {
                    Toast.makeText (add_nurse.this, "Please select image", Toast.LENGTH_SHORT).show ();
                }
                else  if (TextUtils.isEmpty (textFname))
                {
                    Toast.makeText (add_nurse.this, "Please enter first name", Toast.LENGTH_SHORT).show ();
                    etfname.setError ("Full Name is required");
                    etfname.requestFocus ();
                }
                else if(!textFname.matches("[a-zA-Z ]+"))
                {
                    etfname.requestFocus();
                    etfname.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else if (TextUtils.isEmpty (textLname))
                {
                    Toast.makeText (add_nurse.this, "Please enter last name", Toast.LENGTH_SHORT).show ();
                    etlname.setError ("Last Name  is required");
                    etlname.requestFocus ();
                }
                else if(!textLname.matches("[a-zA-Z ]+"))
                {
                    etlname.requestFocus();
                    etlname.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                }
                else if (TextUtils.isEmpty (textDob))
                {
                    Toast.makeText (add_nurse.this, "Please enter Date of Birth", Toast.LENGTH_SHORT).show ();
                    etDate.setError ("Date of Birth is required");
                    etDate.requestFocus ();
                }
                else if (radioGroupRegisterGender.getCheckedRadioButtonId () == -1)
                {
                    Toast.makeText (add_nurse.this, "Please select Gender", Toast.LENGTH_SHORT).show ();

                }
                else if (TextUtils.isEmpty (textCon_num))
                {
                    Toast.makeText (add_nurse.this, "Please enter contact number", Toast.LENGTH_SHORT).show ();
                    etcon_num.setError ("Contact Number  is required");
                    etcon_num.requestFocus ();
                }

                else if (textCon_num.length () != 10)
                {
                    Toast.makeText (add_nurse.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    etcon_num.setError ("Contact Number  should be 10 digits");
                    etcon_num.requestFocus ();

                }
                else if (!mobileMatcher.find ())  {
                    Toast.makeText (add_nurse.this, "Please re-enter contact number", Toast.LENGTH_SHORT).show ();
                    etcon_num.setError ("Contact Number  is not valid");
                    etcon_num.requestFocus ();
                }
                else if (TextUtils.isEmpty (textAddress))
                {
                    Toast.makeText (add_nurse.this, "Please enter address", Toast.LENGTH_SHORT).show ();
                    etaddress.setError ("Address is required");
                    etaddress.requestFocus ();
                }
                else if (TextUtils.isEmpty (textNurseid))
                {
                    Toast.makeText (add_nurse.this, "Click to generate User ID", Toast.LENGTH_SHORT).show ();
                    etnurseid.setError ("Nurse: User ID  is required");
                    etnurseid.requestFocus ();
                }
                else if (TextUtils.isEmpty (textEmail))
                {
                    Toast.makeText (add_nurse.this, "Please enter Email Address", Toast.LENGTH_SHORT).show ();
                    etemail.setError ("Email ID  is required");
                    etemail.requestFocus ();
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher (textEmail).matches ())
                {
                    Toast.makeText (add_nurse.this, "Please re-enter Email Address", Toast.LENGTH_SHORT).show ();
                    etemail.setError ("Valid Email ID  is required");
                    etemail.requestFocus ();
                }
                else if (TextUtils.isEmpty (textPwd))
                {
                    Toast.makeText (add_nurse.this, "Please enter password", Toast.LENGTH_SHORT).show ();
                    etpwd.setError ("Password is required");
                    etpwd.requestFocus ();
                }

                else if (textPwd.length () < 5)
                {
                    Toast.makeText (add_nurse.this, "Password should be at least 5 digits", Toast.LENGTH_SHORT).show ();
                    etpwd.setError ("Password too weak");
                    etpwd.requestFocus ();
                }

                else
                {
                    textGender = radioButtonRegisterGenderSelected.getText ().toString ();

                    Toast.makeText (add_nurse.this, "Registering", Toast.LENGTH_SHORT).show ();
                    progressBar.setVisibility (View.VISIBLE);
                    saveNurse(encodedImage, textFname, textLname, textDob, textGender, textCon_num, textAddress, textDutyshift, textNurseid, textEmail, textPwd);
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
            ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                Uri uri = result.getData().getData();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    profilepicnurse.setImageBitmap(bitmap);
                    encodedImage = encodedImage(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });


    private void saveNurse(String encodedImage, String textFname, String textLname, String textDob, String textGender, String textCon_num,
                           String textAddress, String textDutyshift, String textNurseid, String textEmail, String textPwd)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance ();
        auth.createUserWithEmailAndPassword (textEmail, textPwd).addOnCompleteListener (this,
                new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser ();

                            ReadWriteNurseDetails writeNurseDetails = new ReadWriteNurseDetails(encodedImage, textFname, textLname, textDob, textGender,
                                    textCon_num, textAddress, textDutyshift, textNurseid, textEmail, textPwd);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance ().getReference ("Registered Nurses");

                            referenceProfile.child (firebaseUser.getUid ()).setValue (writeNurseDetails).addOnCompleteListener (new OnCompleteListener<Void> () {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful ()) {

                                        firebaseUser.sendEmailVerification ();
                                        showsuccess ("Success", "Nurse details are saved successfully. Please verify" +
                                                "email to to login !", "OK");
                                    }
                                    else
                                    {
                                        Toast.makeText (add_nurse.this, "Failed to add nurse, Please try again", Toast.LENGTH_LONG).show();
                                    }

                                    progressBar.setVisibility (View.GONE);
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
                                etemail.setError ("Email is invalid or Already in use. Kindly re-enter");
                                etemail.requestFocus ();
                            } catch (FirebaseAuthUserCollisionException e)
                            {
                                etemail.setError ("One Nurse  is already registered with this email. Use another email");
                                etemail.requestFocus ();
                            } catch (Exception e)
                            {

                                Log.e(TAG, e.getMessage ());
                                Toast.makeText (add_nurse.this, e.getMessage (), Toast.LENGTH_SHORT).show ();
                            }

                            progressBar.setVisibility (View.GONE);
                        }
                    }
                });

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> doctor = new HashMap<>();
        doctor.put(Constants.KEY_NAME,etfname.getText().toString());
        doctor.put(Constants.KEY_EMAIL,etemail.getText().toString());
        doctor.put(Constants.KEY_IMAGE,encodedImage);
        doctor.put(Constants.KEY_DESIGNATION, "Nurse");
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(doctor)
                .addOnSuccessListener(documentReference -> {});
    }

    private void GENERATE_ID() {
        RandomString randomString = new RandomString ();
        String result = randomString.generateAlphaNumeric ( 4);
        etnurseid.setText (new StringBuilder ().append ("WCN").append (result).toString ());
    }
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
            profilepicnurse.setImageResource(R.drawable.nurseprofile);
            etfname.setText ("");
            etlname.setText ("");
            etcon_num.setText ("");
            etaddress.setText ("");
            etnurseid.setText ("");
            etemail.setText ("");
            etpwd.setText ("");

            Toast.makeText (add_nurse.this, "closed", Toast.LENGTH_SHORT).show ();
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

}

