package com.example.projectwecare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class nurseviewadapter extends RecyclerView.Adapter<nurseviewadapter.myviewholder> {

    Context context;
    ArrayList<Allnurseview> list;

    private final RecyclerViewClickListener listener;

    public nurseviewadapter(Context context, ArrayList<Allnurseview> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, id, shift;
        RoundedImageView propic;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
            shift = itemView.findViewById(R.id.shift);
            propic = itemView.findViewById(R.id.imageProfilenurse);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(itemView, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Allnurseview n = list.get(position);
        holder.id.setText(n.getNurseID());
        holder.name.setText(n.getFname());
        holder.shift.setText(n.getEmail());

        byte[] bytes = Base64.decode(n.getencodedImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.propic.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
