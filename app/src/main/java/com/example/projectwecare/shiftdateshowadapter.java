package com.example.projectwecare;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class shiftdateshowadapter extends RecyclerView.Adapter<shiftdateshowadapter.myviewholder> {

    Context context;
    ArrayList<ShiftScheduleModal> list;
    private final RecyclerViewClickListener listener;

    public shiftdateshowadapter(Context context, ArrayList<ShiftScheduleModal> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView sd,ed,holi;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            sd = itemView.findViewById(R.id.startshift);
            ed = itemView.findViewById(R.id.endshift);
            holi = itemView.findViewById(R.id.holiday1);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            listener.onClick(itemView, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public shiftdateshowadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.shiftitem,parent,false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull shiftdateshowadapter.myviewholder holder, int position) {
        ShiftScheduleModal s = list.get(position);
        holder.sd.setText(s.getStartShiftDate());
        holder.ed.setText(s.getEndShiftDate());
        holder.holi.setText(s.getHoliday());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
