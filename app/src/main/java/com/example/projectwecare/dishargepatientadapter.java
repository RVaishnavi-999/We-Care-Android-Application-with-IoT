package com.example.projectwecare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class dishargepatientadapter extends RecyclerView.Adapter<dishargepatientadapter.myviewholder>{

    Context context;
    ArrayList<discharge_patientsModal> list;

    private final RecyclerViewClickListener listener;

    public dishargepatientadapter(Context context, ArrayList<discharge_patientsModal> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, id, op;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.patientBillname);
            id = itemView.findViewById(R.id.patientBillid);
            op = itemView.findViewById(R.id.patientBilloperation);
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
        View v = LayoutInflater.from(context).inflate(R.layout.patientbillview, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        discharge_patientsModal p = list.get(position);
        holder.id.setText(p.getPid());
        holder.name.setText(p.getPfname());
        holder.op.setText(p.getTreatment_given());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
