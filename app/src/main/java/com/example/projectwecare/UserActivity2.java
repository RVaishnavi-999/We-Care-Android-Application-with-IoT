package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity2 extends BaseActivity implements UserListener {

    private PreferenceManage preferenceManage;
    private AppCompatImageView imageBack;
    private RecyclerView userRecyclerView;
    private ProgressBar progressBar;
    private TextView textErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        imageBack = findViewById(R.id.imageBack2);
        userRecyclerView = findViewById(R.id.userRecyclerView2);
        progressBar = findViewById(R.id.progressBar15);
        textErrorMessage = findViewById(R.id.textErrorMessage2);

        preferenceManage = new PreferenceManage(getApplicationContext());
        setListeners();
        getUsers();

    }

    private void setListeners(){
        imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManage.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Users> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            String design = queryDocumentSnapshot.getString(Constants.KEY_DESIGNATION);
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }else if(design != null && design.equalsIgnoreCase("Junior Doctor")){
                                Users users1 = new Users();
                                users1.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                users1.type = queryDocumentSnapshot.getString(Constants.KEY_DESIGNATION);
                                users1.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                users1.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                users1.id = queryDocumentSnapshot.getId();
                                users.add(users1);
                            }else {
                                continue;
                            }
                        }
                        if(users.size() > 0){
                            UserAdapter userAdapter = new UserAdapter(users,this);
                            userRecyclerView.setAdapter(userAdapter);
                            userRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        textErrorMessage.setText(String.format("%s", "No User Available"));
        textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isloading){
        if(isloading){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(Users users) {
        Intent intent =  new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,users);
        startActivity(intent);
        finish();
    }
}