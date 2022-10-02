package com.example.projectwecare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class shiftviewadapter extends RecyclerView.Adapter<shiftviewadapter.myviewholder> {

    Context context;
    ArrayList<ShiftScheduleModal> list;

    public shiftviewadapter(Context context, ArrayList<ShiftScheduleModal> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewshiftitem, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        ShiftScheduleModal s = list.get(position);
        holder.id.setText(s.getId());
        holder.name.setText(s.getName());
        holder.shift.setText(s.getShiftType());
        holder.holiday.setText(s.getHoliday());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder{

        TextView id,name,shift,holiday;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.nurse_id);
            name = itemView.findViewById(R.id.nurse_name);
            shift = itemView.findViewById(R.id.shift);
            holiday = itemView.findViewById(R.id.shiftholiday);
        }
    }
}
