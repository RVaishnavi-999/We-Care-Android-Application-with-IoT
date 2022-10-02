package com.example.projectwecare;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class Myadapter_nurse extends FirebaseRecyclerAdapter<Allnurseview, Myadapter_nurse.myViewHolder> {
    private FirebaseRecyclerOptions<Allnurseview> options;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Myadapter_nurse(@NonNull FirebaseRecyclerOptions<Allnurseview> options) {
        super (options);
    }


    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView")final int position, @NonNull Allnurseview allnurseview) {
        byte[] bytes = Base64.decode(allnurseview.getencodedImage (), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);

        holder.image.setImageBitmap(bitmap);


        holder.fname.setText (allnurseview.getFname ());
        holder.lname.setText (allnurseview.getLname ());
        holder.nurseID.setText (allnurseview.getNurseID ());
        holder.dutyshift.setText (allnurseview.getDutyshift ());
        holder.contactnum.setText (allnurseview.getContactnum ());
        holder.email.setText (allnurseview.getEmail ());

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.allnurseentry_recyclerview, parent, false);
        return new myViewHolder (v);
    }
    static  class myViewHolder extends RecyclerView.ViewHolder
    {
        private static  CircleImageView image;
        private static  TextView fname, lname, nurseID, dutyshift, contactnum, email;

        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image = (CircleImageView) itemView.findViewById (R.id.set_image);
            fname = (TextView) itemView.findViewById (R.id.set_fname);
            lname = (TextView) itemView.findViewById (R.id.set_lname);
            nurseID = (TextView) itemView.findViewById (R.id.set_nurseuserid);
            dutyshift = (TextView) itemView.findViewById (R.id.set_dutyshift);
            contactnum = (TextView) itemView.findViewById (R.id.set_contactnum);
            email = (TextView) itemView.findViewById (R.id.set_email);
        }

    }

}
