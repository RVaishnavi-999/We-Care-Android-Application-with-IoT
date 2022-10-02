package com.example.projectwecare;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Discharge_Summary extends AppCompatActivity {
    TextInputEditText et1con_dia, et2pre_exis, et3trea_give, et4con_dis, et5pres, et6follow_up;
    String encodedImage1, encodedImage2;
    ImageView repo1, repo2;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (Discharge_Summary.this, C_DrPatientsList.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_discharge_summary);

        PreferenceManage pref = new PreferenceManage (getApplicationContext ());

        TextView tvpid = findViewById (R.id.tvpid);
        TextView tvpfname = findViewById (R.id.tvpfname);
        TextView tvplname = findViewById (R.id.tvplname);
        tvpid.setText (pref.getString (Constants.KEY_USERID1));
        tvpfname.setText (pref.getString (Constants.KEY_USERNAME1));
        tvplname.setText (pref.getString (Constants.KEY_USERLLASTNAMEP));

        et1con_dia = findViewById (R.id.et1);
        et2pre_exis = findViewById (R.id.et2);
        et3trea_give = findViewById (R.id.et3);
        et4con_dis = findViewById (R.id.et4);
        et5pres = findViewById (R.id.et5);
        et6follow_up = findViewById (R.id.et6);

        repo1 = findViewById (R.id.repo1);
        repo2 = findViewById (R.id.repo2);
        repo1.setOnClickListener (view -> {
            Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickimage1.launch(intent);
        });
        repo2.setOnClickListener (view -> {
            Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickimage2.launch(intent);
        });

        Button btnsave = findViewById (R.id.btnsave);
        btnsave.setOnClickListener (view -> {
            String str_pid = tvpid.getText ().toString ();
            String str_pfname = tvpfname.getText ().toString ();
            String str_plname = tvplname.getText ().toString ();
            String str1 = et1con_dia.getText ().toString ();
            String str2 = et2pre_exis.getText ().toString ();
            String str3 = et3trea_give.getText ().toString ();
            String str4 = et4con_dis.getText ().toString ();
            String str5 = et5pres.getText ().toString ();
            String str6 = et6follow_up.getText ().toString ();


            if (TextUtils.isEmpty (str1))
            {
                Toast.makeText (Discharge_Summary.this, "Please write about Confirmed Diagnosis ", Toast.LENGTH_SHORT).show ();
                et1con_dia.setError ("This cannot be empty");
                et1con_dia.requestFocus ();
            }
             else if (TextUtils.isEmpty (str2))
             {
                 Toast.makeText (Discharge_Summary.this, "Please write about Pre-existing medical conditions and Medications on presentation ", Toast.LENGTH_SHORT).show ();
                 et2pre_exis.setError ("This cannot be empty");
                 et2pre_exis.requestFocus ();
             }
             else if (TextUtils.isEmpty (str3))
             {
                 Toast.makeText (Discharge_Summary.this, "Please write about Treatment given / Procedure done ", Toast.LENGTH_SHORT).show ();
                 et3trea_give.setError ("This cannot be empty");
                 et3trea_give.requestFocus ();
             }
             else if (TextUtils.isEmpty (str4))
             {
                 Toast.makeText (Discharge_Summary.this, "Please write about Condition on Discharge ", Toast.LENGTH_SHORT).show ();
                 et4con_dis.setError ("This cannot be empty");
                 et4con_dis.requestFocus ();
             }
             else if (TextUtils.isEmpty (str5))
             {
                 Toast.makeText (Discharge_Summary.this, "Please write about Advice on Discharge / Prescription", Toast.LENGTH_SHORT).show ();
                 et5pres.setError ("This cannot be empty");
                 et5pres.requestFocus ();
             }
             else if (TextUtils.isEmpty (str6))
             {
                 Toast.makeText (Discharge_Summary.this, "Please write about Follow up schedule", Toast.LENGTH_SHORT).show ();
                 et6follow_up.setError ("This cannot be empty");
                 et6follow_up.requestFocus ();
             }
            else if(encodedImage1 == null)
            {
                Toast.makeText (Discharge_Summary.this, "Please select a report from your gallery", Toast.LENGTH_SHORT).show ();
                repo1.requestFocus ();
            }
            else if(encodedImage2 == null)
            {
                Toast.makeText (Discharge_Summary.this, "Please select a report from your gallery", Toast.LENGTH_SHORT).show ();
                repo2.requestFocus ();
            }
             else
             {
                 Toast.makeText (Discharge_Summary.this, "Saving", Toast.LENGTH_SHORT).show ();
                 saveData(str_pid, str_pfname, str_plname, str1, str2, str3, str4, str5, str6, encodedImage1, encodedImage2);
             }
        });
    }

    private String encodedImage1(Bitmap bitmap){
        int previewWidth = 1200;
        int previewHeight = 2000;
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap , previewWidth , previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickimage1 = registerForActivityResult( new
            ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                Uri uri = result.getData().getData();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    repo1.setImageBitmap(bitmap);
                    encodedImage1 = encodedImage1(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });

    private String encodedImage2(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap , previewWidth , previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickimage2 = registerForActivityResult( new
            ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                Uri uri = result.getData().getData();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    repo2.setImageBitmap(bitmap);
                    encodedImage2 = encodedImage2(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });

    private void saveData(String str_pid, String str_pfname, String str_plname,
                          String str1, String str2, String str3, String str4, String str5, String str6, String encodedImage1, String encodedImage2) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Discharge_Summary").child(str_pid);
        HashMap<String, String> dis_sum = new HashMap<>();
        dis_sum.put("Pid", str_pid);
        dis_sum.put("Pfname", str_pfname);
        dis_sum.put("Plname", str_plname);
        dis_sum.put("confirmed_diagnosis", str1);
        dis_sum.put("preexisting_condition", str2);
        dis_sum.put("treatment_given", str3);
        dis_sum.put("condition_discharge", str4);
        dis_sum.put("advice_discharge", str5);
        dis_sum.put("followup", str6);
        dis_sum.put("Report1", encodedImage1);
        dis_sum.put("Report2", encodedImage2);
        dis_sum.put ("status", "ready");
        databaseReference.setValue(dis_sum)
                .addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText (Discharge_Summary.this, "Success data saved", Toast.LENGTH_SHORT).show ();
            }
        })
                .addOnFailureListener (new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText (Discharge_Summary.this, "Failed", Toast.LENGTH_SHORT).show ();
                    }
                });
        }


}

//pop up dialog saying "data added successfully"
    /*
    code do
     */