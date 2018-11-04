package com.example.phakneath.contactapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.AddInfoActivity;
import com.example.phakneath.contactapp.R;
import com.example.phakneath.contactapp.model.ContactInPhone;
import com.example.phakneath.contactapp.model.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationAdapter extends RecyclerView.Adapter<UserInformationAdapter.viewHolder> {

    private Context context;
    private List<UserInformation> userInformations;
    public InformationDialog openInformationDialog;
    public deleteDialog deleteDialog;
    private int count;

    public UserInformationAdapter(){}

    public UserInformationAdapter(Context context, List<UserInformation> userInformations, int count)
    {
        this.context = context;
        this.userInformations = userInformations;
        this.count = count;
    }

    public void setUserInformations(List<UserInformation> userInformations)
    {
        this.userInformations.addAll(userInformations);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_information_layout, parent, false);
        return new UserInformationAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserInformation userInformation = userInformations.get(position);
        holder.title.setText(userInformation.getTitle());
        if(userInformation.getTitle().equals("Contact Number") || userInformation.getTitle().equals("Contact Number "))
            holder.username.setText(userInformation.getPhoneNum());
        else
            holder.username.setText(userInformation.getUsername());
        if(userInformation.getTitle() != null)
        {
            switch(userInformation.getTitle())
            {

                case "Email": Picasso.with(context).load(R.drawable.emailogo).into(holder.logos); break;
                case "Facebook": Picasso.with(context).load(R.drawable.facebooklogo).into(holder.logos); break;
                case "Instagram": Picasso.with(context).load(R.drawable.instagram).into(holder.logos); break;
                case "Line": Picasso.with(context).load(R.drawable.linelogo).into(holder.logos); break;
                case "Linkedin": Picasso.with(context).load(R.drawable.linkedin).into(holder.logos); break;
                case "Skype": Picasso.with(context).load(R.drawable.skype).into(holder.logos); break;
                case "Telegram": Picasso.with(context).load(R.drawable.telegram).into(holder.logos); break;
                case "Twitter": Picasso.with(context).load(R.drawable.twitter).into(holder.logos); break;
                case "Viber": Picasso.with(context).load(R.drawable.viber).into(holder.logos); break;
                case "Wechat": Picasso.with(context).load(R.drawable.wechat).into(holder.logos);break;
                case "Whatsapp": Picasso.with(context).load(R.drawable.whatsapp).into(holder.logos); break;
                case "Contact Number": Picasso.with(context).load(R.drawable.phonecalllogo).into(holder.logos); break;
                case "Contact Number ": Picasso.with(context).load(R.drawable.phonecalllogo).into(holder.logos); break;
            }
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInformationDialog.onClickContainer(position, userInformations.get(position));
            }
        });

        if(count != 1) holder.menus.setVisibility(View.GONE);

        holder.menus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 1) {
                    PopupMenu menu = new PopupMenu(context, v);
                    menu.inflate(R.menu.edit_menus);
                    menu.setGravity(Gravity.RIGHT);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    //Toast.makeText(context, "Eidt", Toast.LENGTH_SHORT).show();
                                    edit(userInformations.get(position));
                                    return true;
                                case R.id.delete:
                                    deleteDialog.onDelete(userInformations.get(position));
                                    return true;
                                default:
                                    return false;
                            }

                        }
                    });

                    menu.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.userInformations.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder
    {
        CardView container;
        CircleImageView logos;
        TextView title, username;
        ImageView menus;

        public viewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            logos = itemView.findViewById(R.id.logos);
            title = itemView.findViewById(R.id.title);
            username = itemView.findViewById(R.id.username);
            menus = itemView.findViewById(R.id.menus);
        }
    }

    public interface InformationDialog
    {
        public void onClickContainer(int position, UserInformation userInformation);
    }

    public void edit(UserInformation userInformation)
    {
        Intent intent = new Intent(context, AddInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("information", userInformation);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public interface deleteDialog
    {
        public void onDelete(UserInformation userInformation);
    }


}
