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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class Myadapter_dr extends FirebaseRecyclerAdapter<Alldrview, Myadapter_dr.myViewHolder> {
    private FirebaseRecyclerOptions<Alldrview> options;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Myadapter_dr(@NonNull FirebaseRecyclerOptions<Alldrview> options) {
        super (options);
    }


    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView")final int position, @NonNull Alldrview alldrview) {
        byte[] bytes = Base64.decode(alldrview.getencodedImage (), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        holder.image.setImageBitmap(bitmap);
        holder.fullname.setText (alldrview.getFullname ());
        holder.userid.setText (alldrview.getUserid ());
        holder.speciality.setText (alldrview.getSpeciality ());
        holder.designation.setText (alldrview.getDesignation ());
        holder.service_yeras.setText (alldrview.getService_yeras ());
        holder.contactnum.setText (alldrview.getContactnum ());
        holder.email.setText (alldrview.getEmail ());


    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.alldrentry_recyclerview, parent, false);
        return new myViewHolder (v);
    }

    static  class myViewHolder extends RecyclerView.ViewHolder
    {
        private static  CircleImageView image;
        private static  TextView fullname, userid, speciality, designation, service_yeras, contactnum, email;

        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image = (CircleImageView) itemView.findViewById (R.id.set_imageUrl);
            fullname = (TextView) itemView.findViewById (R.id.set_name);
            userid = (TextView) itemView.findViewById (R.id.set_uid);
            speciality = (TextView) itemView.findViewById (R.id.set_spl);
            designation = (TextView) itemView.findViewById (R.id.set_desig);
            service_yeras = (TextView) itemView.findViewById (R.id.set_ser_years);
            contactnum = (TextView) itemView.findViewById (R.id.set_phone);
            email = (TextView) itemView.findViewById (R.id.set_mail);

        }

    }

}
