package com.example.projectwecare;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Bitmap receiverProfileImage;
    private final List<ChatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage = bitmap;
    }

    public ChatAdapter(Bitmap receiverProfileImage, List<ChatMessage> chatMessages, String senderId) {
        this.receiverProfileImage = receiverProfileImage;
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_messages,parent,false)
            );
        }else {
            return new ReceivedMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_recieve_message,parent,false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            SentMessageViewHolder.textMessage.setText(chatMessage.message);
            SentMessageViewHolder.textDateTime.setText(chatMessage.dateTime);
        }else{
            ReceivedMessageViewHolder.textMessage1.setText(chatMessage.message);
            ReceivedMessageViewHolder.textDateTime1.setText(chatMessage.dateTime);
            if(receiverProfileImage != null){
                ReceivedMessageViewHolder.profileImage.setImageBitmap(receiverProfileImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private static TextView textMessage;
        private static TextView textDateTime;

        SentMessageViewHolder(View itemView){
            super(itemView);

            textMessage = itemView.findViewById(R.id.textMessage);
            textDateTime = itemView.findViewById(R.id.textDateTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        private static TextView textMessage1;
        private static TextView textDateTime1;
        private static RoundedImageView profileImage;

        ReceivedMessageViewHolder(View itemView){
            super(itemView);

            textMessage1 = itemView.findViewById(R.id.textMessage1);
            textDateTime1 = itemView.findViewById(R.id.textDateTime1);
            profileImage = itemView.findViewById(R.id.imageProfile1);
        }
    }
}

