package com.example.projectwecare;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private Users receiverrUser;
    List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManage preferenceManage;
    private FirebaseFirestore database;
    private String conversationId = null;
    private Boolean IsReceiverAvailable = false;

    private AppCompatImageView imageback,imageinfo;
    private TextView textName,textavailability;
    private RecyclerView chatRecyclerview;
    private ProgressBar progressBar;
    private FrameLayout layoutSend;
    private EditText inputMessage;

    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageback = findViewById(R.id.imageback);
        imageinfo = findViewById(R.id.imageinfo);
        chatRecyclerview = findViewById(R.id.chatRecyclerview);
        textName = findViewById(R.id.textName);
        textavailability = findViewById(R.id.textavailability);
        progressBar = findViewById(R.id.progressBar1);
        layoutSend = findViewById(R.id.layoutSend);
        inputMessage = findViewById(R.id.inputMessage);

        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        inputMessage.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0,0,0,"Copy");
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        ClipData clipData = clipboardManager.getPrimaryClip();
                        ClipData.Item item1 = clipData.getItemAt(0);
                        inputMessage.setText(item1.getText().toString());
                        Toast.makeText(getApplicationContext(),"Pasted",Toast.LENGTH_LONG).show();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        setListeners();
        loadReceiverDetails();
        init();
        listenMessage();
    }

    private final EventListener<QuerySnapshot> eventListener= (value, error)->{
        if(error != null){
            return ;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage= new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateobject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1 , obj2) -> obj1.dateobject.compareTo(obj2.dateobject));
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                chatRecyclerview.smoothScrollToPosition(chatMessages.size() - 1);
            }
            chatRecyclerview.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);

        if(conversationId == null){
            checkForConversation();
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if(encodedImage != null){
            byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else {
            return null;
        }
    }

    private void init(){
        preferenceManage = new PreferenceManage(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getBitmapFromEncodedString(receiverrUser.image),chatMessages,preferenceManage.getString(Constants.KEY_USER_ID));
        chatRecyclerview.setAdapter(chatAdapter);
        database= FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManage.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVED_ID,receiverrUser.id);
        message.put(Constants.KEY_MESSAGE,inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        if(conversationId != null){
            updateConversation(inputMessage.getText().toString());
        }else{
            HashMap<String, Object> conversations = new HashMap<>();
            conversations.put(Constants.KEY_SENDER_ID,preferenceManage.getString(Constants.KEY_USER_ID));
            conversations.put(Constants.KEY_SENDER_NAME,preferenceManage.getString(Constants.KEY_NAME));
            conversations.put(Constants.KEY_SENDER_IMAGE,preferenceManage.getString(Constants.KEY_IMAGE));
            conversations.put(Constants.KEY_RECEIVED_ID,receiverrUser.id);
            conversations.put(Constants.KEY_RECEIVER_NAME,receiverrUser.name);
            conversations.put(Constants.KEY_RECEIVER_IMAGE,receiverrUser.image);
            conversations.put(Constants.KEY_LAST_MESSAGE,inputMessage.getText().toString());
            conversations.put(Constants.KEY_TIMESTAMP,new Date());
            addConversation(conversations);
        }
        if(!IsReceiverAvailable){
            try{
                JSONArray tokens = new JSONArray();
                tokens.put(receiverrUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManage.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME,preferenceManage.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN,preferenceManage.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE,inputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.KEY_MSG_DATA,data);
                body.put(Constants.KEY_MSG_REGISTRATION_IDS,tokens);

                sendNotification(body.toString());
            }catch (Exception e){
                showToast(e.getMessage());
            }
        }
        inputMessage.setText(null);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApService.class).sendMessage(
                Constants.getRemoteMsgHeader(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    try {
                        if(response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if(responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    showToast("Notification sent successfully");
                }else{
                    showToast("Error: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverrUser.id)
                .addSnapshotListener(ChatActivity.this , (value, error) -> {
                    if(error != null){
                        return;
                    }
                    if(value != null){
                        if(value.getLong(Constants.KEY_AVAILABILITY) != null){
                            int availability = Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)).intValue();
                            IsReceiverAvailable = availability == 1;
                        }
                        receiverrUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                        if(receiverrUser.image == null){
                            receiverrUser.image = value.getString(Constants.KEY_IMAGE);
                            chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverrUser.image));
                            chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
                        }
                    }
                    if(IsReceiverAvailable){
                        textavailability.setVisibility(View.VISIBLE);
                    }else{
                        textavailability.setVisibility(View.GONE);
                    }
                });
    }

    private void  listenMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManage.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVED_ID,receiverrUser.id)
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverrUser.id)
                .whereEqualTo(Constants.KEY_RECEIVED_ID,preferenceManage.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private void loadReceiverDetails() {
        receiverrUser = (Users) getIntent().getSerializableExtra(Constants.KEY_USER);
        textName.setText(receiverrUser.name);
    }

    private void setListeners() {
        imageback.setOnClickListener(v -> onBackPressed());
        layoutSend.setOnClickListener(v -> Isvalidate());
    }

    private void Isvalidate(){
        if(inputMessage.getText().toString().isEmpty()){
            showerror("Empty Text","Enter the message","Ok");
        }else{
            sendMessage();
        }
    }
    private void showerror(String title1,String message1,String action1){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_error_dialog,(ConstraintLayout)findViewById(R.id.layoutdialogerrorContainer));
        builder.setView(view);

        TextView title,message;
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

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void  addConversation(HashMap<String ,Object> conversation){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private void updateConversation(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATION).document(conversationId);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP , new Date());
    }

    private void checkForConversation(){
        if(chatMessages.size() != 0){
            checkForConversationRemotely(preferenceManage.getString(Constants.KEY_USER_ID), receiverrUser.id);
            checkForConversationRemotely(receiverrUser.id,preferenceManage.getString(Constants.KEY_USER_ID));
        }
    }

    private void checkForConversationRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVED_ID,receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListeners);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListeners = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}