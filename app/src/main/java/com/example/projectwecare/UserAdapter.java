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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private final List<Users> users;
    private final UserListener userListener;

    public UserAdapter(List<Users> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users lists = users.get(position);
        UserViewHolder.textName.setText(lists.name);
        UserViewHolder.textType.setText(lists.type);
        UserViewHolder.imageProfile.setImageBitmap(getUserImg(lists.image));
        UserViewHolder.itemUser.setOnClickListener(v -> userListener.onUserClicked(lists));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{

        private static RoundedImageView imageProfile;
        private static TextView textName,textType;
        private static ConstraintLayout itemUser;

        UserViewHolder(View itemView){
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile3);
            textName = itemView.findViewById(R.id.textName);
            textType = itemView.findViewById(R.id.textType);
            itemUser = itemView.findViewById(R.id.itemUser);
        }
    }

    private Bitmap getUserImg(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
