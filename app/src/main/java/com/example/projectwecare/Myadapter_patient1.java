package com.example.projectwecare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Myadapter_patient1 extends RecyclerView.Adapter<Myadapter_patient1.myviewholder> {
    Context context;
    ArrayList<Allpatientview> list;

    private final RecyclerViewClickListener listener;

    public Myadapter_patient1(Context context, ArrayList<Allpatientview> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fname, lname, patient_id, illness, careunit, ward;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            fname = (TextView) itemView.findViewById(R.id.set_fname1);
            lname = (TextView) itemView.findViewById(R.id.set_lname1);
            patient_id = (TextView) itemView.findViewById(R.id.set_patienid1);
            illness = (TextView) itemView.findViewById(R.id.set_illness1);
            careunit = (TextView) itemView.findViewById(R.id.set_careunit1);
            ward = (TextView) itemView.findViewById(R.id.set_ward1);
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
        View v = LayoutInflater.from(context).inflate(R.layout.allpatientlist_recyclerview1, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Allpatientview allpatientview = list.get(position);
        holder.fname.setText(allpatientview.getFname());
        holder.lname.setText(allpatientview.getLname());
        holder.patient_id.setText(allpatientview.getPatient_id());
        holder.illness.setText(allpatientview.getIllness());
        holder.careunit.setText(allpatientview.getCareunit());
        holder.ward.setText(allpatientview.getWard());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}

