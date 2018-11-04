package com.example.phakneath.contactapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phakneath.contactapp.R;
import com.example.phakneath.contactapp.model.ContactInPhone;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class contactInPhoneAdapter extends RecyclerView.Adapter<contactInPhoneAdapter.viewHolder>{

    private Context context;
    private List<ContactInPhone> contactInPhones;
    public OpenDialogDetail openDialogDetail;

    public contactInPhoneAdapter(Context context, List<ContactInPhone> contactInPhones)
    {
        this.context = context;
        this.contactInPhones = contactInPhones;
    }

    public void setUsers(List<ContactInPhone> contactInPhones)
    {
        this.contactInPhones.addAll(contactInPhones);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contactlists_layout, parent, false);
        return new contactInPhoneAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ContactInPhone user = contactInPhones.get(position);
        holder.img.setImageBitmap(user.getImg());
        holder.name.setText(user.getName());;

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogDetail.onClick(position, contactInPhones.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactInPhones.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView name;

        public viewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }
    }

    public interface OpenDialogDetail
    {
        public void onClick(int position, ContactInPhone contactInPhone);
    }


}
