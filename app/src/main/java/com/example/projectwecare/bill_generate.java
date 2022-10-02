package com.example.projectwecare;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class bill_generate extends AppCompatActivity {

    AppCompatButton in1, in2, de1, de2;
    TextView value1, value2;

    TextInputEditText icu, ward, consult, nursefee, icutypes, wardtypes, pid, pname, opcost, Dfee, equip,opertaionon;
    AppCompatButton discharge;
    int count1 = 1;
    int count2 = 1;

    int icucost;

    int wardcost;
    int wardnursecost;

    int consultcost = 1500;

    String id;

    Bitmap bmp, bmp1, scaledbmp, scaledbmp1;

    int Total = 0;

    Random random = new Random();
    int receipt = random.nextInt(1000000);

    private PreferenceManage preferenceManage;

    ProgressBar progressBar,progressBar1;

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        startActivity (new Intent (getApplicationContext(), patientbillView.class));
        finish ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_generate);

        preferenceManage = new PreferenceManage(getApplicationContext());

        id = preferenceManage.getString(Constants.KEY_PATIENTID);

        progressBar = findViewById(R.id.progressBar5);
        progressBar1 = findViewById(R.id.progressBar6);

        value1 = findViewById(R.id.textshow1);
        in1 = findViewById(R.id.increment1);
        de1 = findViewById(R.id.decrement1);

        value2 = findViewById(R.id.textshow2);
        in2 = findViewById(R.id.increment2);
        de2 = findViewById(R.id.decrement2);

        icu = findViewById(R.id.icuCost);
        ward = findViewById(R.id.wardCost);
        consult = findViewById(R.id.ConsultFee);
        nursefee = findViewById(R.id.NursingFee);
        icutypes = findViewById(R.id.icutype);
        wardtypes = findViewById(R.id.wardtype);
        pid = findViewById(R.id.edittext1);
        pname = findViewById(R.id.edittext2);
        opcost = findViewById(R.id.OperationCost);
        Dfee = findViewById(R.id.DoctorFee);
        equip = findViewById(R.id.EquipmentCost);
        opertaionon = findViewById(R.id.Operationon);

        discharge = findViewById(R.id.dischargebtn);

        in1.setOnClickListener(v -> {
            count1++;

            value1.setText(Integer.toString(count1));
            icu.setText(Integer.toString((int) (count1 * icucost)));
        });

        de1.setOnClickListener(v -> {
            if (count1 <= 1) count1 = 1;
            else count1--;

            value1.setText(Integer.toString(count1));
            icu.setText(Integer.toString((int) (count1 * icucost)));
        });

        in2.setOnClickListener(v -> {
            count2++;

            value2.setText(Integer.toString(count2));
            ward.setText(Integer.toString((int) (count2 * wardcost)));
            consult.setText(Integer.toString((int) (count2 * consultcost)));
            nursefee.setText(Integer.toString((int) (count2 * wardnursecost)));
        });

        de2.setOnClickListener(v -> {
            if (count2 <= 1) count2 = 1;
            else count2--;

            value2.setText(Integer.toString(count2));
            ward.setText(Integer.toString((int) (count2 * wardcost)));
            consult.setText(Integer.toString((int) (count2 * consultcost)));
            nursefee.setText(Integer.toString((int) (count2 * wardnursecost)));
        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        viewprofile();
        createInvoice();

    }

    private void getdatas() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Patients");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    String newid = dataSnapshot1.child("patient_id").getValue(String.class);
                    if (id.equals(newid)) {

                        String it = dataSnapshot1.child("careunit").getValue(String.class);
                        icutypes.setText(it);

                        String wt = dataSnapshot1.child("ward").getValue(String.class);
                        wardtypes.setText(wt);

                        String operation = dataSnapshot1.child("illness").getValue(String.class);
                        opertaionon.setText(operation);

                        String ad = dataSnapshot1.child("adm_date").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTDOA,ad);

                        String at = dataSnapshot1.child("adm_time").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTDOT,at);

                        String age = dataSnapshot1.child("age").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTAGE,age);

                        String cn = dataSnapshot1.child("contactnumbet").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTNUM,cn);

                        String em = dataSnapshot1.child("email").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTEMAIL,em);

                        String gen = dataSnapshot1.child("gender").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTSEX,gen);

                        String doc = dataSnapshot1.child("treating_dr_name").getValue(String.class);
                        preferenceManage.putString(Constants.KEY_PATIENTDOC,doc);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error", "Id not found", "Ok");
            }
        });
    }

    private void viewprofile() {

        progressBar1.setVisibility(View.VISIBLE);
        getdatas();
        consult.setText(Integer.toString((int) consultcost));
        pid.setText(id);
        pname.setText(preferenceManage.getString(Constants.KEY_PATIENTNAME));
        geticu();
        getward();
        getoperation();
        Toast.makeText(getApplicationContext(),""+preferenceManage.getString(Constants.KEY_PATIENTNUM),Toast.LENGTH_LONG).show();
    }

    private void geticu() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Icuroom").child(String.valueOf(icutypes.getText()));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String Cost = snapshot1.child("Cost").getValue(String.class);
                    icu.setText(Cost);
                    icucost = Integer.parseInt(Cost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error", "Error while finding icu info", "Ok");
            }
        });
    }

    private void getward() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Wardroom").child(String.valueOf(wardtypes.getText()));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String Cost = snapshot1.child("Cost").getValue(String.class);
                    String NurseFee = snapshot1.child("NurseFee").getValue(String.class);

                    ward.setText(Cost);
                    nursefee.setText(NurseFee);

                    wardnursecost = Integer.parseInt(NurseFee);
                    wardcost = Integer.parseInt(Cost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error", "Error while finding ward info", "Ok");
            }
        });
    }

    private void getoperation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Operation").child(String.valueOf(opertaionon.getText()));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    String Cost = snapshot1.child("Cost").getValue(String.class);
                    String DoctorFee = snapshot1.child("DoctorFee").getValue(String.class);
                    String Equipment = snapshot1.child("Equipment").getValue(String.class);

                    opcost.setText(Cost);
                    Dfee.setText(DoctorFee);
                    equip.setText(Equipment);

                    progressBar1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showerror("Error","Error while finding operation info", "Ok");
            }
        });
    }

    private void createInvoice() {

        discharge.setOnClickListener(v -> {

            progressBar.setVisibility(View.VISIBLE);
            discharge.setVisibility(View.INVISIBLE);

            int pageWidth = 1200;
            int pageheight = 2010;

            PdfDocument pdfDocument = new PdfDocument();

            bill(pageWidth,pageheight,pdfDocument,1);
            dischargesummary(pageWidth,pageheight,pdfDocument,2);
            testreport1(pageWidth,pageheight,pdfDocument,3);
            testreport2(pageWidth,pageheight,pdfDocument,4);

            File file = new File(Environment.getExternalStorageDirectory(), "/" + id + ".pdf");

            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();
            if(isvalidate()){
                upload(Uri.fromFile(file));
            }else{
                showerror("Error","Fields are empty","Ok");
            }

        });
    }

    private void testreport2(int pageWidth, int pageheight, PdfDocument pdfDocument, int i) {
        byte[] bytes = Base64.decode(preferenceManage.getString(Constants.KEY_PATIENTTEST2),Base64.DEFAULT);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        scaledbmp = Bitmap.createScaledBitmap(bitmap1, 1200, 2000, false);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, i).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(scaledbmp, 0, 0, paint);

        pdfDocument.finishPage(page);
    }

    private void testreport1(int pageWidth, int pageheight, PdfDocument pdfDocument, int i) {

        byte[] bytes = Base64.decode(preferenceManage.getString(Constants.KEY_PATIENTTEST1),Base64.DEFAULT);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
//        String encodedImage;
//        encodedImage = encodedImage(bitmap1);
//        bmp = BitmapFactory.decodeResource(getResources(), Integer.parseInt(encodedImage));
        scaledbmp = Bitmap.createScaledBitmap(bitmap1, 1200, 2000, false);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, i).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(scaledbmp, 0, 0, paint);

        pdfDocument.finishPage(page);
    }

    private void bill(int pageWidth,int pageheight,PdfDocument pdfDocument,int pageno){
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.invoice);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 1200, 300, false);

        bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.symbol);
        scaledbmp1 = Bitmap.createScaledBitmap(bmp1, 120, 120, false);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, pageno).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        Paint painttitle = new Paint();
        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(scaledbmp, 0, 0, paint);
        canvas.drawBitmap(scaledbmp1, 40, 80, paint);

        painttitle.setTextAlign(Paint.Align.CENTER);
        painttitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        painttitle.setColor(Color.rgb(19, 147, 197));
        painttitle.setTextSize(50);
        canvas.drawText("WeCare Hosiptal", 500, 190, painttitle);

        paint.setTextSize(30f);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Ph no: 080-42999999", 800, 150, paint);
        canvas.drawText("Email: wecare@gmailcom", 800, 200, paint);

        painttitle.setTextAlign(Paint.Align.CENTER);
        painttitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        painttitle.setTextSize(50);
        canvas.drawText("Invoice", pageWidth / 2, 320, painttitle);

        paint.setTextSize(35f);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        canvas.drawText("Receipt No: " + receipt, 20, 390, paint);
        canvas.drawText("Patient Id: " + pname.getText(), 20, 440, paint);
        canvas.drawText("Patient Name: " + pid.getText(), 20, 490, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Phone No: " + preferenceManage.getString(Constants.KEY_PATIENTNUM), 400, 390, paint);
        canvas.drawText("Gender: " + preferenceManage.getString(Constants.KEY_PATIENTSEX), 400, 440, paint);
        canvas.drawText("Age: " + preferenceManage.getString(Constants.KEY_PATIENTAGE), 400, 490, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("DOA: " + preferenceManage.getString(Constants.KEY_PATIENTDOA), 800, 390, paint);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        canvas.drawText("DOD: "+ formatter.format(date), 800, 440, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(20, 520, pageWidth - 20, 570, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Description", 40, 560, paint);
        canvas.drawText("Amount (Rs)", 900, 560, paint);
        canvas.drawLine(880, 520, 880, 570, paint);

        canvas.drawText("Operation Cost", 80, 640, paint);
        canvas.drawText(opcost.getText().toString(), 940, 640, paint);

        canvas.drawText("Doctor Fee", 80, 700, paint);
        canvas.drawText(Dfee.getText().toString(), 940, 700, paint);

        canvas.drawText("Equipment Cost", 80, 760, paint);
        canvas.drawText(equip.getText().toString(), 940, 760, paint);

        canvas.drawText("ICU Room Charges ("+icutypes.getText()+") * " +value1.getText(), 80, 820, paint);
        canvas.drawText(icu.getText().toString(), 940, 820, paint);

        canvas.drawText("Ward Room Charges ("+wardtypes.getText()+") * " +value2.getText(), 80, 880, paint);
        canvas.drawText(ward.getText().toString(), 940, 880, paint);

        canvas.drawText("Consultance Charges", 80, 940, paint);
        canvas.drawText(consult.getText().toString(), 940, 940, paint);

        canvas.drawText("Nursing Fee", 80, 1000, paint);
        canvas.drawText(nursefee.getText().toString(), 940, 1000, paint);

        canvas.drawLine(40, 1200, pageWidth - 40, 1200, paint);

        canvas.drawText("Total Amount to be Paid:", 400, 1300, paint);
        setTotal();
        canvas.drawText(String.valueOf(Total)+" /-", 940, 1300, paint);

        pdfDocument.finishPage(page);
    }

    private void dischargesummary(int pageWidth,int pageheight,PdfDocument pdfDocument,int pageno){
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.invoice);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 1200, 300, false);

        bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.symbol);
        scaledbmp1 = Bitmap.createScaledBitmap(bmp1, 120, 120, false);

        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, pageno).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo1);

        Paint paint1 = new Paint();
        Paint painttitle1 = new Paint();
        Canvas canvas1 = page1.getCanvas();

        canvas1.drawBitmap(scaledbmp, 0, 0, paint1);
        canvas1.drawBitmap(scaledbmp1, 40, 80, paint1);

        painttitle1.setTextAlign(Paint.Align.CENTER);
        painttitle1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        painttitle1.setColor(Color.rgb(19, 147, 197));
        painttitle1.setTextSize(50);
        canvas1.drawText("WeCare Hosiptal", 500, 190, painttitle1);

        paint1.setTextSize(30f);
        paint1.setColor(Color.BLACK);
        paint1.setTextAlign(Paint.Align.LEFT);
        canvas1.drawText("Ph no: 080-42999999", 800, 150, paint1);
        canvas1.drawText("Email: wecare@gmailcom", 800, 200, paint1);

        painttitle1.setTextAlign(Paint.Align.CENTER);
        painttitle1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        painttitle1.setTextSize(50);
        canvas1.drawText("Discharge Summary", pageWidth / 2, 320, painttitle1);

        paint1.setTextSize(35f);
        paint1.setTextAlign(Paint.Align.LEFT);
        paint1.setColor(Color.BLACK);
        canvas1.drawText("Name: " + pname.getText(), 40, 390, paint1);

        canvas1.drawText("Age: " + preferenceManage.getString(Constants.KEY_PATIENTAGE), 40, 450, paint1);
        canvas1.drawText("Sex: " + preferenceManage.getString(Constants.KEY_PATIENTSEX), pageWidth/2, 450, paint1);

        canvas1.drawText("Treating Doctor: " + preferenceManage.getString(Constants.KEY_PATIENTDOC), 40, 510, paint1);

        canvas1.drawText("Date of Admission: " + preferenceManage.getString(Constants.KEY_PATIENTDOA), 40, 570, paint1);
        canvas1.drawText("Time of Admission: " + preferenceManage.getString(Constants.KEY_PATIENTDOT), pageWidth/2, 570, paint1);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        canvas1.drawText("Date of Discharge: " + formatter.format(date), 40, 630, paint1);
        canvas1.drawText("Time of Discharge: " + formatter1.format(date), pageWidth/2, 630, paint1);

        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(2);
        canvas1.drawRect(20, 700, pageWidth - 20, 850, paint1);
        canvas1.drawRect(20, 870, pageWidth - 20, 1020, paint1);
        canvas1.drawRect(20, 1040, pageWidth - 20, 1190, paint1);
        canvas1.drawRect(20, 1210, pageWidth - 20, 1360, paint1);
        canvas1.drawRect(20, 1380, pageWidth - 20, 1630, paint1);
        canvas1.drawRect(20, 1650, pageWidth - 20, 1900, paint1);

        paint1.setTextAlign(Paint.Align.LEFT);
        paint1.setStyle(Paint.Style.FILL);

        canvas1.drawText("Confirmed Diagnosis:", 40, 740, paint1);
        canvas1.drawText(preferenceManage.getString(Constants.KEY_PATIENTDIAGNOSIS), 60, 780, paint1);

        canvas1.drawText("Pre-Existing Medical Condition:", 40, 910, paint1);
        canvas1.drawText(preferenceManage.getString(Constants.KEY_PATIENTPREEXIST), 60, 950, paint1);

        canvas1.drawText("Treatment Given Procedure:", 40, 1080, paint1);
        canvas1.drawText(preferenceManage.getString(Constants.KEY_PATIENTTREATMENT), 60, 1120, paint1);

        canvas1.drawText("Condition-On Discharge:", 40, 1250, paint1);
        canvas1.drawText(preferenceManage.getString(Constants.KEY_PATIENTCONDITION), 60, 1290, paint1);

        canvas1.drawText("Advice on Discharge:", 40, 1420, paint1);
        canvas1.drawText(preferenceManage.getString(Constants.KEY_PATIENTADVICE), 60, 1460, paint1);

        canvas1.drawText("Follow Up Schedule:", 40, 1690, paint1);
        canvas1.drawText(preferenceManage.getString(Constants.KEY_PATIENTFOLLOWP), 60, 1730, paint1);

        pdfDocument.finishPage(page1);
    }

    private void upload(Uri uri) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child(pid.getText().toString()+".pdf").putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bills").child(String.valueOf(receipt));

                        HashMap<String, Object> user = new HashMap<>();
                        user.put("Patient Id", pid.getText().toString());
                        user.put("url", url);

                        databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Discharge_Summary").child(pid.getText().toString());
                                        HashMap<String, Object> user1 = new HashMap<>();
                                        user1.put("status","Discharge");
                                        reference.updateChildren(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                updatepatient();
                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showerror("Error",e.getMessage().toString(),"Try Again");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showerror("Error", e.getMessage().toString(), "Ok");
                    }
                });

    }

    private void updatepatient() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Patients").child(preferenceManage.getString(Constants.KEY_PATIENTNUM));
        HashMap<String, Object> user1 = new HashMap<>();
        user1.put("status","Discharge");
        reference.updateChildren(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updatecareunit();
            }
        });
    }

    private void updatecareunit() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CareUnit").child(icutypes.getText().toString());
        HashMap<String ,Object> user = new HashMap<>(0);
        user.put("status","available");
        reference.updateChildren(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.INVISIBLE);
                discharge.setVisibility(View.VISIBLE);
                showsuccess("Success","Successfully Discharged","Done");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showerror("Error","Error while handling Try Again!" ,"Ok");
                    }
                });
    }

    private void setTotal() {
        int op = parseInt(opcost.getText().toString());
        int df = parseInt(Dfee.getText().toString());
        int eq = parseInt(equip.getText().toString());
        int iroom = parseInt(icu.getText().toString());
        int wroom = parseInt(ward.getText().toString());
        int con = parseInt(consult.getText().toString());
        int nf = parseInt(nursefee.getText().toString());

        Total = op + df + eq + iroom + wroom + con + nf;
    }

    private void showsuccess(String title1, String message1, String action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_success_dialog, (ConstraintLayout) findViewById(R.id.layoutdialogsuccessContainer));
        builder.setView(view);

        TextView title, message;
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
            startActivity(new Intent(getApplicationContext(), patientbillView.class));
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
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
                    startActivity(new Intent(getApplicationContext(), patientbillView.class));
                    alertDialog.dismiss();
                });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private boolean isvalidate(){
        if(opertaionon.getText().toString().isEmpty()){
           return false;
        }else if(opcost.getText().toString().isEmpty()){
            return false;
        }else if(Dfee.getText().toString().isEmpty()){
            return false;
        }else if(equip.getText().toString().isEmpty()){
            return false;
        }else if(icutypes.getText().toString().isEmpty()){
            return false;
        }else if(icu.getText().toString().isEmpty()){
            return false;
        }else if(wardtypes.getText().toString().isEmpty()){
            return false;
        }else if(ward.getText().toString().isEmpty()){
            return false;
        }else if(nursefee.getText().toString().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}