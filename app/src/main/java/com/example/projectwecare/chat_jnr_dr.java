package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class chat_jnr_dr extends BaseActivity implements ConversationListener{

    private PreferenceManage preferenceManage;
    private List<ChatMessage> conversation;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    private RoundedImageView imageProfile;
    private TextView textName;
    private RecyclerView conversationRecyclerview;
    private ProgressBar progressBar;
    private FloatingActionButton fabnewchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_chat_jnr_dr);

        preferenceManage = new PreferenceManage(getApplicationContext());

        imageProfile = findViewById(R.id.imageProfile1);
        textName = findViewById(R.id.textName1);
        conversationRecyclerview = findViewById(R.id.conversationRecyclerview1);
        progressBar = findViewById(R.id.progressBar12);
        fabnewchat = findViewById(R.id.fabnewchat1);

        init();
        loaduserdetails();
        getToken();
        setListeners();
        listenMessage();
    }

    private void init(){
        conversation = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversation,this);
        conversationRecyclerview.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        fabnewchat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),UserActivity1.class)));
    }

    private void loaduserdetails() {
        textName.setText(preferenceManage.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManage.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void  listenMessage(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManage.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.KEY_RECEIVED_ID,preferenceManage.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if(error != null){
            return ;
        }
        if(value != null){
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderTd = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderTd;
                    chatMessage.receiverId = receiverId;

                    if(preferenceManage.getString(Constants.KEY_USER_ID).equals(senderTd)){
                        chatMessage.conservationImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversationId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                    }else{
                        chatMessage.conservationImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversationId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }

                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateobject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversation.add(chatMessage);
                }else  if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i < conversation.size() ; i++){
                        String senderTd = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                        if (conversation.get(i).senderId.equals(senderTd) && conversation.get(i).receiverId.equals(receiverId)){
                            conversation.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversation.get(i).dateobject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversation,(obj1, obj2) -> obj2.dateobject.compareTo(obj1.dateobject));
            conversationAdapter.notifyDataSetChanged();
            conversationRecyclerview.smoothScrollToPosition(0);
            conversationRecyclerview.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManage.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManage.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e -> showToast("Unable to Update Token"));
    }

    @Override
    public void OnConversationListener(Users users) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,users);
        startActivity(intent);
    }
}