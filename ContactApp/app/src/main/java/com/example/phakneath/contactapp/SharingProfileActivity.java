package com.example.phakneath.contactapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phakneath.contactapp.adapter.UserInformationAdapter;
import com.example.phakneath.contactapp.dialog.DetailInformationDialog;
import com.example.phakneath.contactapp.fragments.ContactFragment;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;

import de.hdodenhof.circleimageview.CircleImageView;

public class SharingProfileActivity extends AppCompatActivity implements View.OnClickListener, UserInformationAdapter.InformationDialog {

    private ImageView coverPhoto, backBtn, coverPhoto1;
    private CircleImageView profile;
    private TextView username;
    private RecyclerView myInfo;
    private User user;
    private TextView txt;
    private UserInformationAdapter userInformationAdapter;
    private ContactFragment contactFragment = new ContactFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sharing_profile);

        initView();
        user = getIntentUsers();
        backBtn.setOnClickListener(this::onClick);
        updateUI(user);
    }

    public void initView()
    {
        coverPhoto = findViewById(R.id.coverPhoto);
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        backBtn = findViewById(R.id.backBtn);
        coverPhoto1 = findViewById(R.id.coverPhoto1);
        myInfo = findViewById(R.id.infoContainer);
        txt = findViewById(R.id.txt);
    }

    public User getIntentUsers()
    {
        User user=new User();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("getUser");
        return user;
    }

    public void updateUI(User user)
    {
        username.setText(user.getUsername());
        txt.setText(user.getUsername() + "'s" + " Contacts");
        if(user.getContact() != null) contactFragment.getImage(user,profile,user.getImage() + "." + user.getExtension(),this);
        getUserInformation(user);
    }


    //get all user information such as facebook, telegram, skype
    public void getUserInformation(User user)
    {
        //setup Adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(SharingProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        myInfo.setLayoutManager(layoutManager);
        userInformationAdapter = new UserInformationAdapter(SharingProfileActivity.this, user.getContact(),2);
        myInfo.setAdapter(userInformationAdapter);
        userInformationAdapter.openInformationDialog = SharingProfileActivity.this::onClickContainer;
    }

    @Override
    public void onClick(View v) {
        if(v == backBtn)
        {
            finish();
        }
    }

    @Override
    public void onClickContainer(int position, UserInformation userInformation) {
        DetailInformationDialog c = new DetailInformationDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", userInformation);
        c.setArguments(bundle);
        c.show(getFragmentManager(),"Dialog");
    }

}
