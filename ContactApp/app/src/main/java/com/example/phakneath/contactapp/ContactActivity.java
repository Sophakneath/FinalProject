package com.example.phakneath.contactapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.contactapp.adapter.contactInPhoneAdapter;
import com.example.phakneath.contactapp.dialog.ContactInPhoneDialog;
import com.example.phakneath.contactapp.fragments.ContactFragment;
import com.example.phakneath.contactapp.fragments.MoreFragment;
import com.example.phakneath.contactapp.model.ContactInPhone;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.singleton.ContactInMemory;
import com.example.phakneath.contactapp.singleton.ContactInPhoneObj;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, contactInPhoneAdapter.OpenDialogDetail{

    private Button btnlogout;
    FirebaseAuth mAuth;
    String email;
    private TextView t;
    private ImageView contact, connect, profile, logoContact, searchView;
    private TextView tContact, tConnect, tProfile;
    private ContactFragment contactFragment = new ContactFragment();
    private MoreFragment moreFragment = new MoreFragment();
    private TextView title, cancel;
    private android.support.v7.widget.SearchView search;
    private LinearLayout linearLayout, containerHorizontal, mainContainer, containerVertical, bottom;
    private RecyclerView searchHorizontal, searchVertical;
    private contactInPhoneAdapter contactInPhoneAdapter;
    private DatabaseReference mDatabase;
    int b=1;
    List<ContactInPhone> contacts;
    String txt, uID;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        initViews();
        contact.setOnClickListener(this::onClick);
        connect.setOnClickListener(this::onClick);
        profile.setOnClickListener(this::onClick);

        tContact.setOnClickListener(this::onClick);
        tConnect.setOnClickListener(this::onClick);
        tProfile.setOnClickListener(this::onClick);

        searchView.setOnClickListener(this::onClick);
        cancel.setOnClickListener(this::onClick);

        openContactFragment();
        //new RunBackGround().execute();
        activity = this;

    }

    private void initViews() {
       contact = findViewById(R.id.contactIcon);
       connect = findViewById(R.id.connectIcon);
       profile = findViewById(R.id.profileIcon);
       tContact = findViewById(R.id.textConIcon);
       tConnect = findViewById(R.id.textConnectIcon);
       tProfile = findViewById(R.id.textProfileIcon);
       title = findViewById(R.id.title);
       logoContact = findViewById(R.id.logoContact);
       searchView = findViewById(R.id.searchView);
       search = findViewById(R.id.search);
       cancel = findViewById(R.id.cancel);
       linearLayout = findViewById(R.id.linear);
       containerHorizontal = findViewById(R.id.action_ContainerSearch);
       mainContainer = findViewById(R.id.action_container);
       containerVertical = findViewById(R.id.action_ContainerSearchVertical);
       searchHorizontal = findViewById(R.id.listSearchHorizontal);
       searchVertical = findViewById(R.id.listSearchVertical);
       bottom = findViewById(R.id.container);
    }

    @Override
    public void onClick(View v) {
       if(v == contact || v == tContact)
       {
           setIconContactColor(R.color.colorTitle, R.drawable.contact_profile);
           setIconConnectColor(R.color.iconWhite, R.drawable.plus);
           setIconProfileColor(R.color.iconWhite, R.drawable.more1);

           openContactFragment();
       }
       else if(v == connect || v == tConnect)
       {
           //setIconContactColor(R.color.iconWhite, R.drawable.contact_profile1);
           //setIconConnectColor(R.color.iconBlack, R.drawable.plus1);
           //setIconProfileColor(R.color.iconWhite, R.drawable.more2);

           startActivity(new Intent(this, QRCodeActivity.class));
       }
       else if(v == profile || v == tProfile)
       {
           setIconContactColor(R.color.iconWhite, R.drawable.contact_profile1);
           setIconConnectColor(R.color.iconWhite, R.drawable.plus);
           setIconProfileColor(R.color.colorTitle, R.drawable.more2);

           openMoreFragment();
       }
       else if(v == searchView)
       {
           contacts = ContactInPhoneObj.getRepository().getUsers();
           bottom.setVisibility(View.GONE);
           logoContact.setVisibility(View.GONE);
           title.setVisibility(View.GONE);
           mainContainer.setVisibility(View.GONE);
           searchView.setVisibility(View.GONE);
           search.setVisibility(View.VISIBLE);
           cancel.setVisibility(View.VISIBLE);
           linearLayout.setVisibility(View.VISIBLE);
           containerHorizontal.setVisibility(View.VISIBLE);
           containerVertical.setVisibility(View.VISIBLE);
           search.setQueryHint("Search ...");
           search.setIconifiedByDefault(false);
           search.requestFocus();
           searchHorizontal.clearDisappearingChildren();

           search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(String query) {
                   return false;
               }

               @Override
               public boolean onQueryTextChange(String newText) {
                   txt = newText;
                   new Run().execute();
                   LinearLayoutManager layoutManager = new LinearLayoutManager(ContactActivity.this, LinearLayoutManager.VERTICAL, false);
                   searchVertical.setLayoutManager(layoutManager);
                   firebaseSearchContact(newText);
                   return true;
               }
           });

       }
       else if(v == cancel)
       {
           bottom.setVisibility(View.VISIBLE);
           logoContact.setVisibility(View.VISIBLE);
           title.setVisibility(View.VISIBLE);
           mainContainer.setVisibility(View.VISIBLE);
           searchView.setVisibility(View.VISIBLE);
           search.setVisibility(View.GONE);
           cancel.setVisibility(View.GONE);
           linearLayout.setVisibility(View.GONE);
           containerHorizontal.setVisibility(View.GONE);
           containerVertical.setVisibility(View.GONE);
       }
    }

    public void setIconContactColor(int color, int image)
    {
        Glide.with(this).load(getResources().getDrawable(image)).into(contact);
        //contact.setBackground(this.getResources().getDrawable(image));
        tContact.setTextColor(this.getResources().getColor(color));
    }

    public void setIconConnectColor(int color, int image)
    {
        //connect.setBackground(this.getResources().getDrawable(image));
        Glide.with(this).load(getResources().getDrawable(image)).into(connect);
        tConnect.setTextColor(this.getResources().getColor(color));
    }

    public void setIconProfileColor(int color, int image)
    {
        Glide.with(this).load(getResources().getDrawable(image)).into(profile);
        //profile.setBackground(this.getResources().getDrawable(image));
        tProfile.setTextColor(this.getResources().getColor(color));
    }

    public void openContactFragment()
    {
        searchView.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().remove(moreFragment);
        title.setText("MY CONTACT");
        getSupportFragmentManager().beginTransaction().replace(R.id.action_container, contactFragment).detach(contactFragment).attach(contactFragment).commit();

    }

    public void openMoreFragment()
    {
        searchView.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().remove(contactFragment);
        title.setText("MORE      ");
        getSupportFragmentManager().beginTransaction().replace(R.id.action_container, moreFragment).commit();
    }

    @Override
    public void onClick(int position, ContactInPhone contactInPhone) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("contact", contactInPhone);
        ContactInPhoneDialog c = new ContactInPhoneDialog();
        c.setArguments(bundle);
        c.show(this.getFragmentManager(),"Dialog");
    }

    public class Run extends AsyncTask<String, Integer, List<ContactInPhone>>
    {

        @Override
        protected List<ContactInPhone> doInBackground(String... strings) {
            List<ContactInPhone> items = new ArrayList<>();
            items.clear();
            if(!txt.isEmpty() && contacts.size() != 0){
                txt = txt.toLowerCase();
                for(ContactInPhone item: contacts){
                    try {

                            if(item.getName() != null && item.getName().toLowerCase().contains(txt) ){
                                items.add(item);
                            }

                    }catch (NullPointerException e){

                    }
                }
            }
            else if(txt.isEmpty())
            {
                items.addAll(items);
            }
            return items;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<ContactInPhone> contactInPhones) {
            super.onPostExecute(contactInPhones);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ContactActivity.this, LinearLayoutManager.HORIZONTAL, false);
            searchHorizontal.setLayoutManager(layoutManager);
            contactInPhoneAdapter = new contactInPhoneAdapter(ContactActivity.this, contactInPhones);
            searchHorizontal.setAdapter(contactInPhoneAdapter);
            contactInPhoneAdapter.openDialogDetail = ContactActivity.this::onClick;
        }
    }

    //search from firebase
    private void firebaseSearchContact(String newText)
    {
        if(!newText.isEmpty()) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child("id").child(uID).child("sharing");
            Query query = mDatabase.orderByChild("username").startAt(newText).endAt(newText + "\uf8ff");
            FirebaseRecyclerOptions<User> options =
                    new FirebaseRecyclerOptions.Builder<User>()
                            .setQuery(query, User.class)
                            .setLifecycleOwner(this)
                            .build();

            FirebaseRecyclerAdapter<User, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, ViewHolder>(options) {

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_sharing_layout, parent, false);
                    return new ViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
                    holder.username.setText(model.getUsername());
                    holder.phoneNum.setText(model.getPhone());
                    contactFragment.getImage(model,holder.logos,model.getImage()+"."+model.getExtension(), ContactActivity.this);

                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ContactActivity.this, SharingProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("getUser", model);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    holder.menus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu menu = new PopupMenu(ContactActivity.this, v);
                            menu.inflate(R.menu.delete_menus);
                            menu.setGravity(Gravity.RIGHT);
                            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.delete:
                                            delete(model);
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
            };
            searchVertical.setAdapter(firebaseRecyclerAdapter);
        }
        else
        {
            searchVertical.setAdapter(null);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView container;
        CircleImageView logos;
        TextView username, phoneNum;
        ImageView menus;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            logos = itemView.findViewById(R.id.logos);
            username = itemView.findViewById(R.id.username);
            phoneNum = itemView.findViewById(R.id.phoneNum);
            menus = itemView.findViewById(R.id.menus);
        }
    }

    public void delete(User user)
    {
        new FancyAlertDialog.Builder(ContactActivity.this)
                .setTitle("Confirmation")
                .setBackgroundColor(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                .setMessage("Are you sure you want to delete this information?")
                .setNegativeBtnText("No")
                .setPositiveBtnBackground(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Yes")
                .setNegativeBtnBackground(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SIDE)
                .isCancellable(true)
                .setIcon(R.drawable.infos, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("user").child("id").child(uID).child("sharing").child(user.getId()).removeValue();
                        Toast.makeText(ContactActivity.this, "Delete Successfull", Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();
    }

}
