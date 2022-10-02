package com.example.projectwecare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversationViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;

    public RecentConversationAdapter(List<ChatMessage> chatMessages,ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_recent_conversation,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {

        ChatMessage lists = chatMessages.get(position);
        holder.imageProfile1.setImageBitmap(getConversationImage(lists.conservationImage));
        holder.textName1.setText(lists.conversationName);
        holder.textRecentMessage1.setText(lists.message);
        holder.layoutRecent.setOnClickListener(v -> {
            Users users = new Users();
            users.id = lists.conversationId;
            users.name = lists.conversationName;
            users.image = lists.conservationImage;
            conversationListener.OnConversationListener(users);
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageProfile1;
        private TextView textName1,textRecentMessage1;
        private ConstraintLayout layoutRecent;

        ConversationViewHolder(View itemView){
            super(itemView);

            imageProfile1 = itemView.findViewById(R.id.imageProfile1);
            textName1 = itemView.findViewById(R.id.textName1);
            textRecentMessage1 = itemView.findViewById(R.id.textRecentMessage1);
            layoutRecent = itemView.findViewById(R.id.layoutRecent);
        }

    }

    private Bitmap getConversationImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
