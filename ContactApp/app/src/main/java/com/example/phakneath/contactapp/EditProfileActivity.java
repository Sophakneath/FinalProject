package com.example.phakneath.contactapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.contactapp.fragments.ContactFragment;
import com.example.phakneath.contactapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView backBtn;
    private Button female, male;
    private FirebaseAuth mAuth;
    private EditText username, phoneNum;
    private RelativeLayout email;
    private CircleImageView cover;
    private TextView saveChange;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String uID, gender, extension;
    private String pathImage, profileImage, downloadImage;
    public static final int OPEN_GALLERY = 1;
    private ProgressBar progressBar;
    static StorageTask mUploadTask;
    private StorageReference ref;
    private ContactFragment contactFragment = new ContactFragment();
    Uri uri;
    User user = new User();
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uID = mAuth.getCurrentUser().getUid();
        backBtn.setOnClickListener(this::onClick);
        female.setOnClickListener(this::onClick);
        male.setOnClickListener(this::onClick);
        email.setOnClickListener(this::onClick);
        saveChange.setOnClickListener(this::onClick);
        cover.setOnClickListener(this::onClick);
        getUserIntent();
        activity = this;


    }

    private void initView() {
        backBtn = findViewById(R.id.backBtn);
        saveChange = findViewById(R.id.saveChange);
        female = findViewById(R.id.female);
        male = findViewById(R.id.male);
        username = findViewById(R.id.editUsername);
        phoneNum = findViewById(R.id.editPhoneNum);
        email = findViewById(R.id.editEmail);
        cover = findViewById(R.id.profile);
        progressBar = findViewById(R.id.progressbar);
    }

    private void getUserIntent()
    {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        updateUI(user);
    }

    private void updateUI(User user)
    {
        username.setText(user.getUsername());
        phoneNum.setText(user.getPhone());
        gender = user.getGender();
        profileImage = user.getImage();
        if (user.getGender().equals("Female"))
        {
            setFemale();
        }
        else
        {
            setMale();
        }


        if(user.getImage() != null)
        {
            contactFragment.getImage(user,cover,user.getImage()+"."+user.getExtension(),this);
            //Bitmap bitmap = BitmapFactory.decodeFile(user.getImage());
            //cover.setImageBitmap(bitmap);
        }

        username.setSelection(username.getText().length());
        phoneNum.setSelection(phoneNum.getText().length());
    }

    public void setFemale()
    {
        female.setBackground(this.getResources().getDrawable(R.color.white));
        male.setBackground(this.getResources().getDrawable(R.color.grey));
    }

    public void setMale()
    {
        female.setBackground(this.getResources().getDrawable(R.color.grey));
        male.setBackground(this.getResources().getDrawable(R.color.white));
    }

    public void editUsers(String uID)
    {
        //Toast.makeText(this, user.getUsername() + user.getGender() + user.getPhone() , Toast.LENGTH_SHORT).show();
        if(!username.getText().toString().equals(user.getUsername()) || !phoneNum.getText().toString().equals(user.getPhone()) || !gender.equals(user.getGender()))
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("user").child("id").child(uID).child("username").setValue(username.getText().toString());
            mDatabase.child("user").child("id").child(uID).child("phone").setValue(phoneNum.getText().toString());
            mDatabase.child("user").child("id").child(uID).child("gender").setValue(gender);
            //startActivity(new Intent(this, ProfileActivity.class));
        }
        final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    saveChange.setVisibility(View.VISIBLE);
                    if(uri != null || username.getText().toString().equals(user.getUsername()) || phoneNum.getText().toString().equals(user.getPhone()) || gender.equals(user.getGender())) Toast.makeText(EditProfileActivity.this, " Changed Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, 2000);
    }

    //upload image to Firebase Storage
    public void uploadImage(Uri uri) {

        String p = user.getImage() + "." + user.getExtension();
        if(uri != null)
        {
            ref = storageReference.child("profile/").child( pathImage + "." + extension);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref = storageReference.child("profile/").child( p);
                            ref.delete();
                            Log.d("uplaod", "onSuccess: " + taskSnapshot.toString()); }
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

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void editProfile(String uID, Uri uri, String pathImage)
    {
        if(mUploadTask == null || !mUploadTask.isInProgress())
            uploadImage(uri);
        if(uri != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("user").child("id").child(uID).child("image").setValue(pathImage);
            mDatabase.child("user").child("id").child(uID).child("extension").setValue(extension);}
        mUploadTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                editUsers(uID);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_GALLERY)
        {
            if (resultCode == RESULT_OK)
            {
                uri = data.getData();

                //only get path in gallery
                Log.e("ooooo", "HI : " +  uri.toString());

                //how to get direct path for image
                String[] column = { MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, column, null, null, null);
                cursor.moveToFirst();

                pathImage = cursor.getString(cursor.getColumnIndex(column[0]));
                //Bitmap bitmap = BitmapFactory.decodeFile(pathImage);

                //Picasso.with(this).load(pathImage).into(cover);
                //cover.setImageBitmap(bitmap);
                Glide.with(this).load(uri).into(cover);
                pathImage = uri.getLastPathSegment() + System.currentTimeMillis();
                extension = getFileExtension(uri);
                //editProfile(uID,uri,pathImage);
                Log.e("ooooo", "Path Image : " + pathImage);
                cursor.close();

            }
        }
    }

    public void gallery()
    {
        if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
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

    @Override
    public void onClick(View v) {
        if(v == backBtn)
        {
            finish();
        }
        else if(v == saveChange)
        {
            saveChange.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            editProfile(uID,uri,pathImage);
            //editUsers(uID);
        }
        else if(v == email)
        {
            startActivity(new Intent(this, EditEmailActivity.class));
        }
        else if(v == female)
        {
            gender = "Female";
            setFemale();
        }
        else if(v == male)
        {
            gender = "Male";
            setMale();
        }
        else if(v == cover)
        {
            gallery();
        }
    }
}
