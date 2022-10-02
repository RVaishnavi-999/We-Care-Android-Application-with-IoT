package com.example.projectwecare;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {

    private TextView back;

    private PreferenceManage preferenceManage;
    private RoundedImageView imageProfile;
    private String encodedImage;
    private FrameLayout layoutImage;
    private TextView TextaddImage;
    private EditText inputName, inputEmail, inputphone, inputaddress;
    private Button savechanages;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    String Useremail, name, address, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        preferenceManage = new PreferenceManage(getApplicationContext());

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        Useremail = firebaseUser.getEmail();

        layoutImage = findViewById(R.id.layoutImage1);
        imageProfile = findViewById(R.id.imageProfile);
        TextaddImage = findViewById(R.id.TextaddImage1);
        inputName = findViewById(R.id.input_name1);
        inputphone = findViewById(R.id.input_phone1);
        inputaddress = findViewById(R.id.input_address1);
        savechanages = findViewById(R.id.save1);
        inputEmail = findViewById(R.id.input_email1);

        viewprofile();

        layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickimage.launch(intent);
        });

        savechanages.setOnClickListener(v -> {
            if(isValidDetails()){
                check();
            }
        });

    }

    private void FirestoreUpdate() {
        name = inputName.getText().toString();
        phone = inputphone.getText().toString();
        address = inputaddress.getText().toString();

        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("name", name);
        userDetail.put("email", inputEmail.getText().toString());
        userDetail.put("image", encodedImage);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo("email", Useremail)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(documentID)
                                    .update(userDetail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            realtimeupdate();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showToast(e.getMessage());
                                        }
                                    });
                        } else {
                            showToast("Failed to update");
                        }
                    }
                });
    }

    private void realtimeupdate() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Nurses");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String key = dataSnapshot.getKey();
                    if (email.equalsIgnoreCase(Useremail)) {
                        HashMap<String, Object> user = new HashMap<>();
                        user.put("fname", inputName.getText().toString());
                        user.put("email", inputEmail.getText().toString());
                        user.put("encodedImage", encodedImage);
                        user.put("address", inputaddress.getText().toString());

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Registered Nurses").child(key);
                        reference1.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                preferenceManage.putString(Constants.KEY_EMAIL,inputEmail.getText().toString());
                                showsuccess ("Success", "Email has been updated. Please  verify your new email !", "OK");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void authupdate(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(inputEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.sendEmailVerification();
                    FirestoreUpdate();
                }
            }
        });
    }

    private void check() {
        if (firebaseUser.equals(" ")) {
            showerror("Error", "Something went wrong! Details not available.", "OK");
        } else {
            authupdate(firebaseUser);
        }
    }

    private void viewprofile() {
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference reference1 = database1.getReference("Registered Nurses");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String email = dataSnapshot.child("email").getValue(String.class);

                    if (email.equals(Useremail)) {
                        inputEmail.setText(Useremail);
                        inputName.setText(dataSnapshot.child("fname").getValue(String.class));
                        inputphone.setText(dataSnapshot.child("contactnum").getValue(String.class));
                        inputaddress.setText(dataSnapshot.child("address").getValue(String.class));
                        byte[] bytes = Base64.decode(dataSnapshot.child("encodedImage").getValue(String.class), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        encodedImage = encodedImage(bitmap);
                        imageProfile.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(error.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickimage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageProfile.setImageBitmap(bitmap);
                            TextaddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);
                        } catch (FileNotFoundException e) {
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
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void showsuccess(String title1, String message1, String action1) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
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

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        action.setOnClickListener(v -> {

            Intent i = new Intent(getApplicationContext(), MainActivity3Nurse.class);
            startActivity(i);
            finish();
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private Boolean isValidDetails() {
        String stringname = "[a-z][A-Z]";
        if (inputEmail.getText().toString().trim().isEmpty()) {
            inputEmail.setError("Email is empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
            inputEmail.setError("Enter valid email");
            return false;
        } else if (inputaddress.getText().toString().trim().isEmpty()) {
            inputaddress.setError("Enter Address");
            return false;
        }else if (inputphone.getText().toString().trim().isEmpty()) {
            inputphone.setError("Enter phone");
            return false;
        }else if (!Patterns.PHONE.matcher(inputphone.getText().toString()).matches()) {
            inputphone.setError("Enter valid phone number");
            return false;
        }else if (inputName.getText().toString().trim().isEmpty()) {
            inputName.setError("Enter name");
            return false;
//        } else if (!Pattern.matches(stringname,inputName.getText().toString())) {
//            inputName.setError("Enter valid name");
//            return false;
        }else if (encodedImage == null) {
            Toast.makeText(getApplicationContext(),"Add Image",Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }
    }
}