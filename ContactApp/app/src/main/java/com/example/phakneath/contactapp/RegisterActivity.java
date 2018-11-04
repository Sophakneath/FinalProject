package com.example.phakneath.contactapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.phakneath.contactapp.R.*;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView gotoLogin;
    private EditText username, password, email, phone;
    private String id = "";
    private Button male, female, register;
    private ImageView profile, camera;
    String receiveToken, receiveEmail, txtemail, txtpassword,txtusername, txtphone, extension;
    String gender = "Female";
    List<UserInformation> contact;
    private ProgressBar progressBar;
    public static final int OPEN_GALLERY = 1;
    private String pathImage, cover, cvExtension;
    private DatabaseReference mDatabase;
    int count;
    FirebaseAuth mAuth;
    Uri uri;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    StorageTask mUploadTask;

    private List<User> users = new ArrayList<>();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_register);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WriteIntoDatabase();
        initViews();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        gotoLogin.setOnClickListener(this);
        register.setOnClickListener(this::onClick);
        male.setOnClickListener(this::onClick);
        female.setOnClickListener(this::onClick);
        camera.setOnClickListener(this::onClick);
        profile.setOnClickListener(this::onClick);
    }

    public void initViews()
    {
        gotoLogin = findViewById(R.id.gotoLogin);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        email = findViewById(R.id.etEmail);
        phone = findViewById(R.id.etPhoneNum);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        register = findViewById(R.id.btnRegister);

        profile = findViewById(R.id.profile_image);
        progressBar = findViewById(R.id.progressbar);
        camera = findViewById(R.id.camera);
    }

    public void createNewUser(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    receiveToken = mAuth.getCurrentUser().getIdToken(true).toString();
                    receiveEmail = mAuth.getCurrentUser().getEmail().toString();
                    Toast.makeText(RegisterActivity.this,"Register successfull", Toast.LENGTH_SHORT).show();
                    Log.d("Register", mAuth.getCurrentUser().getIdToken(true).toString());
                    Log.d("Register", mAuth.getCurrentUser().getUid());

                    id =  mAuth.getCurrentUser().getUid();
                    writeUser(String.valueOf(id));
                    new FancyAlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Verification")
                            .setBackgroundColor(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                            .setMessage("Thank you for signing in, Please verify your email before login !")
                            .setPositiveBtnBackground(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                            .setPositiveBtnText("VERIFY")
                            .setNegativeBtnText("Cancel")
                            .setAnimation(Animation.SIDE)
                            .isCancellable(false)
                            .setIcon(R.drawable.infos, Icon.Visible)
                            .OnPositiveClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(RegisterActivity.this, "Please Check Your Email to Verify", Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                                }
                            })
                            .OnNegativeClicked(new FancyAlertDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            }).build();
                }else{
                    progressBar.setVisibility(View.GONE);
                    Log.d("error",task.getException().toString());
                    Toast.makeText(RegisterActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
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
                extension = getFileExtension(uri);
                camera.setVisibility(View.GONE);
                Picasso.with(this).load(uri).into(profile);
                String[] column = { MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, column, null, null, null);
                cursor.moveToFirst();
                //pathImage = cursor.getString(cursor.getColumnIndex(column[0]));
                pathImage = uri.getLastPathSegment() + System.currentTimeMillis();
                Log.d("Choose", "onActivityResult: " + pathImage);
                cursor.close();
            }
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void gallery()
    {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this,
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

    //write user in database firebase
    public void writeUser(String id)
    {
        if(mUploadTask == null || !mUploadTask.isInProgress())
        uploadImage(uri);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User(id,txtusername, txtphone,pathImage,gender,contact, cover, extension, cvExtension);
        mDatabase.child("user").child("id").child(id).setValue(user);
    }

    //read user
    public void WriteIntoDatabase()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").getDatabase().toString();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //users.clear();
                count=0;
                for (DataSnapshot postSnapshot: dataSnapshot.child("user").child("id").getChildren()) {
                    /*User user = postSnapshot.getValue(User.class);
                    users.add(user);
                    Log.d("ooooo", "onDataChange: " + postSnapshot.toString());
                    Counter(users.size());*/
                    count++;
                }
                //Log.d("ooooo", "onDataChange: " + users.size());*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //upload image to Firebase Storage
    public void uploadImage(Uri uri) {

        if(uri != null)
        {
            ref = storageReference.child("profile/").child( pathImage + "." + extension);
            mUploadTask = ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

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

    int Counter(int c)
    {
        return c;
    }

    @Override
    public void onClick(View v) {
        if(v == gotoLogin)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if(v == register)
        {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())) {
                Toast.makeText(this, "Please Enter Require Information above !", Toast.LENGTH_SHORT).show();
            } else {
                txtemail = email.getText().toString().trim();
                txtpassword = password.getText().toString().trim();
                txtusername = username.getText().toString().trim();
                txtphone = phone.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                //writeUser(String.valueOf(count + 1));
                createNewUser(txtemail, txtpassword);
                Log.d("ooooo", "onClick: " + id + receiveToken);

                //writeUser(id);


            }
        }
        else if(v == male)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            gender = "Male";
            male.setBackground(this.getResources().getDrawable(R.drawable.button_transparent1));
            female.setBackground(this.getResources().getDrawable(drawable.button_transparent));

        }
        else if(v == female)
        {
            gender = "Female";
            male.setBackground(this.getResources().getDrawable(R.drawable.button_transparent));
            female.setBackground(this.getResources().getDrawable(drawable.button_transparent1));
        }
        else if(v == camera)
        {
            gallery();
        }
        else if(v == profile)
        {
            gallery();
        }
    }
}
