package com.example.projectwecare;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Myviewpadapter extends RecyclerView.Adapter<Myviewpadapter.myviewholder> {
    Context context;
    ArrayList<Allpatientview> list;

    private final RecyclerViewClickListener listener;

    public Myviewpadapter(Context context, ArrayList<Allpatientview> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fname, lname, patient_id, illness, careunit, ward, treating_dr_name, emergencyphone, contactnumbet;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            fname = (TextView) itemView.findViewById (R.id.set_fname);
            lname = (TextView) itemView.findViewById (R.id.set_lname);
            patient_id = (TextView) itemView.findViewById (R.id.set_patienid);
            illness = (TextView) itemView.findViewById (R.id.set_illness);
            careunit = (TextView) itemView.findViewById (R.id.set_careunit);
            ward = (TextView) itemView.findViewById (R.id.set_ward);
            treating_dr_name = (TextView) itemView.findViewById (R.id.set_dr_name);
            emergencyphone = (TextView) itemView.findViewById (R.id.set_eme_phone);
            contactnumbet = (TextView) itemView.findViewById (R.id.set_p_phone);

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
        View v = LayoutInflater.from (context).inflate(R.layout.allviewpatient_recyclerview, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Allpatientview allpatientview = list.get(position);
        holder.fname.setText (allpatientview.getFname ());
        holder.lname.setText (allpatientview.getLname ());
        holder.patient_id.setText (allpatientview.getPatient_id ());
        holder.illness.setText (allpatientview.getIllness ());
        holder.careunit.setText (allpatientview.getCareunit ());
        holder.ward.setText (allpatientview.getWard ());
        holder.treating_dr_name.setText (allpatientview.getTreating_dr_name ());
        holder.emergencyphone.setText (allpatientview.getEmergencyphone ());
        holder.contactnumbet.setText(allpatientview.getContactnumbet ());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}

