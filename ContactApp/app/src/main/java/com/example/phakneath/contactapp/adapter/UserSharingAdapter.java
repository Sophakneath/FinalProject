package com.example.phakneath.contactapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.phakneath.contactapp.R;
import com.example.phakneath.contactapp.fragments.ContactFragment;
import com.example.phakneath.contactapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSharingAdapter extends RecyclerView.Adapter<UserSharingAdapter.viewHolder> {

    private List<User> users;
    private Context context;
    public DetailSharing detailSharing;
    public DeleteSharing deleteSharing;
    private ContactFragment contactFragment = new ContactFragment();

    public UserSharingAdapter(Context context, List<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sharing_layout, parent, false);
        return new UserSharingAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        holder.phoneNum.setText(user.getPhone());

        contactFragment.getImage(user, holder.logos, user.getImage() + "." + user.getExtension(), context);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailSharing.onOpenDetail(users.get(position));
            }
        });

        holder.menus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.inflate(R.menu.delete_menus);
                menu.setGravity(Gravity.RIGHT);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                deleteSharing.onDelete(users.get(position));
                                return true;
                            default:
                                return false;
                        }

                    }
                });

                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder
    {
        CardView container;
        CircleImageView logos;
        TextView username, phoneNum;
        ImageView menus;

        public viewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            logos = itemView.findViewById(R.id.logos);
            username = itemView.findViewById(R.id.username);
            phoneNum = itemView.findViewById(R.id.phoneNum);
            menus = itemView.findViewById(R.id.menus);
        }
    }

    public interface DetailSharing
    {
        public void onOpenDetail(User user);
    }

    public interface DeleteSharing
    {
        public void onDelete(User user);
    }
}
