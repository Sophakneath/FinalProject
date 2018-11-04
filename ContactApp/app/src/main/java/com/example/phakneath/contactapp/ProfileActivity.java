package com.example.phakneath.contactapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.phakneath.contactapp.adapter.UserInformationAdapter;
import com.example.phakneath.contactapp.dialog.DetailInformationDialog;
import com.example.phakneath.contactapp.dialog.LoadingDialog;
import com.example.phakneath.contactapp.fragments.ContactFragment;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;
import com.example.phakneath.contactapp.sharePreferences.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Picasso;
import com.taishi.flipprogressdialog.FlipProgressDialog;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class  ProfileActivity extends AppCompatActivity implements View.OnClickListener, UserInformationAdapter.InformationDialog , UserInformationAdapter.deleteDialog{

    private ImageView coverPhoto, settingBtn, backBtn, coverPhoto1, addInfo;
    private CircleImageView profile;
    private TextView username, phoneNum;
    private RecyclerView myInfo;
    private DatabaseReference mDatabase;
    private DatabaseReference mChild;
    private ProgressBar progressBar, progressBarCover;
    private String uID;
    public static final int OPEN_GALLERY = 1;
    private String pathImage, extension;
    private FirebaseAuth mAuth;
    Uri uri;
    private FirebaseStorage storage;
    private StorageReference ref, sto;
    StorageTask mUploadTask;
    User user;
    UserInformationAdapter userInformationAdapter;
    private ContactFragment contactFragment = new ContactFragment();
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        initView();
        mAuth = FirebaseAuth.getInstance();
        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        ref = storage.getReference();
        userInformationAdapter = new UserInformationAdapter();
        settingBtn.setOnClickListener(this::onClick);
        backBtn.setOnClickListener(this::onClick);
        addInfo.setOnClickListener(this::onClick);
        //getInformation();
        activity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInformation();
        getUserInformation();
    }

    public void initView()
    {
        coverPhoto = findViewById(R.id.coverPhoto);
        settingBtn = findViewById(R.id.SettingBtn);
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        phoneNum = findViewById(R.id.userPhoneNum);
        backBtn = findViewById(R.id.backBtn);
        progressBar = findViewById(R.id.progressbar);
        coverPhoto1 = findViewById(R.id.coverPhoto1);
        progressBarCover = findViewById(R.id.progressbarCover);
        addInfo = findViewById(R.id.addInfo);
        myInfo = findViewById(R.id.infoContainer);
    }

    @Override
    public void onClick(View v) {
        if(v == settingBtn)
        {
            openDialogCover();
        }
        else if(v == backBtn)
        {
            finish();
        }
        else if(v== addInfo)
        {
            startActivity(new Intent(this, AddInfoActivity.class));
        }
    }

    private void UpdateUI(User user)
    {
        username.setText(user.getUsername());
        Log.d("ooooo", "UpdateUI: " + user.getPhone());
        //phoneNum.setText(user.getPhone());
        phoneNum.setVisibility(View.GONE);
        String getImage = user.getImage() + "." + user.getExtension();
        String getCover = user.getCover() + "." + user.getCvExtension();

        if(user.getImage() != null)
        {
            //bitmap = BitmapFactory.decodeFile(user.getImage());
            contactFragment.getImage(user,profile,getImage,this);
            //profile.setImageBitmap(bitmap);
        }

        if(user.getCover() != null)
        {
            //bitmap = BitmapFactory.decodeFile(user.getCover());
            //coverPhoto.setImageBitmap(bitmap);
            contactFragment.getImage(user,coverPhoto, getCover,this);
        }
    }

    private void getInformation()
    {
        progressBar.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChild = mDatabase.child("user").child("id").child(uID);
        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("ooooo", "onDataChange: " + dataSnapshot.child("email").getValue());
                String username = dataSnapshot.child("username").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                String id = dataSnapshot.child("id").getValue(String.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                String cover = dataSnapshot.child("cover").getValue(String.class);
                String extension = dataSnapshot.child("extension").getValue(String.class);
                String cvExtension = dataSnapshot.child("cvExtension").getValue(String.class);

                user = new User(id,username,phone,image,gender,cover,extension,cvExtension);
                progressBar.setVisibility(View.GONE);
                setInfoPhone(user);
                UpdateUI(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //get all user information such as facebook, telegram, skype
    public void getUserInformation()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChild = mDatabase.child("user").child("id").child(uID).child("contact");
        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserInformation> userInformations = new ArrayList<>();
                UserInformation userInformation =new UserInformation();
                for (DataSnapshot d: dataSnapshot.getChildren())
                {
                    userInformation = d.getValue(UserInformation.class);
                    Log.d("userDatas", "onDataChange: " + userInformation.getUsername());
                    userInformations.add(userInformation);
                }

                //setup Adapter
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false);
                    myInfo.setLayoutManager(layoutManager);
                    userInformationAdapter = new UserInformationAdapter(ProfileActivity.this, userInformations,1);
                    myInfo.setAdapter(userInformationAdapter);
                    userInformationAdapter.openInformationDialog = ProfileActivity.this::onClickContainer;
                    userInformationAdapter.deleteDialog = ProfileActivity.this::onDelete;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //permission for open gallery
    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, Uri.parse(MediaStore.Images.Media.DATA));
        i.setType("image/*");
        startActivityForResult(i,OPEN_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == OPEN_GALLERY)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                openGallery();
            }
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_GALLERY)
        {
            if (resultCode == RESULT_OK)
            {
                uri = data.getData();
                extension = getFileExtension(uri);

                String[] column = { MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, column, null, null, null);
                cursor.moveToFirst();
                //pathImage = cursor.getString(cursor.getColumnIndex(column[0]));
                pathImage = uri.getLastPathSegment() + System.currentTimeMillis();
                Log.d("Choose", "onActivityResult: " + pathImage);
                cursor.close();

                progressBarCover.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBarCover.setVisibility(View.GONE);
                    }
                }, 1000);
                Picasso.with(this).load(uri).into(coverPhoto);
                writeCoverPhoto(uID, uri,pathImage, extension);

                Log.e("ooooo", "Path Image : " + pathImage);

            }
        }
    }

    public void gallery()
    {
        if(ContextCompat.checkSelfPermission(ProfileActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != getPackageManager().PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},OPEN_GALLERY);
        }
        else
        {
            openGallery();
        }
    }

    //upload image to Firebase Storage
    public void uploadImage(Uri uri) {

        String p = user.getCover() + "." + user.getCvExtension();
        if(uri != null)
        {
            ref = storage.getReference().child("profile/").child( pathImage + "." + extension);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.d("uplaod", "onSuccess: " + taskSnapshot.toString());
                            ref = storage.getReference().child("profile/").child( p);
                            ref.delete(); }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
    }

    public void writeCoverPhoto(String uID, Uri uri, String pathImage, String cvExtension)
    {
        if(mUploadTask == null || !mUploadTask.isInProgress())
            uploadImage(uri);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uID).child("cover").setValue(pathImage);
        mDatabase.child("user").child("id").child(uID).child("cvExtension").setValue(extension);
    }

    public void signout()
    {
        mAuth.signOut();
        UserPreferences.remove(this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ContactActivity.activity.finish();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }

    public void logoutDialog()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingDialog dialog = new LoadingDialog();
                dialog.show(getFragmentManager(), "dialogLoading");
            }
        }, 1000);

    }

    public void openDialogCover()
    {
        String[] items = {"Choose a Cover Photo", "Edit Profile", "Logout"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0: gallery(); break;
                    case 1: Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        intent.putExtras(bundle);
                        startActivity(intent); break;
                    case 2: logoutDialog(); signout(); break;
                }
            }
        });

        builder.setCancelable(true);
        builder.create().show();
    }

    public void removePhoto(UserInformation userInformation)
    {
        String qrCode = userInformation.getQrCode() + "." + userInformation.getExtensionCode();
        ref = FirebaseStorage.getInstance().getReference();
        sto = ref.child("qrCode/").child(qrCode);
        sto.delete();
    }

    public void remove(String title)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uID).child("contact").child(title).removeValue();
    }

    public void delete(UserInformation userInformation)
    {
        remove(userInformation.getId());
        if(userInformation.getQrCode() != null)removePhoto(userInformation);
    }

    @Override
    public void onClickContainer(int position, UserInformation userInformation) {
        DetailInformationDialog c = new DetailInformationDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", userInformation);
        c.setArguments(bundle);
        c.show(getFragmentManager(),"Dialog");
    }

    @Override
    public void onDelete(UserInformation userInformation) {

        new FancyAlertDialog.Builder(this)
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
                        delete(userInformation);
                        if(!userInformation.getTitle().equals("Contact Number")) Toast.makeText(ProfileActivity.this, "Delete Successfull", Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();
    }

    public void setInfoPhone(User user)
    {
        UserInformation userInformation = new UserInformation();
        userInformation.setTitle("Contact Number");
        userInformation.setPhoneNum(user.getPhone());
        userInformation.setUsername(user.getUsername());
        userInformation.setId("contactnumber");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uID).child("contact").child("contactnumber").setValue(userInformation);
    }

}
