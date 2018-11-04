package com.example.phakneath.contactapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.model.ContactInPhone;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener, ResultHandler {

    private ZXingScannerView scanner;
    private TextView backBtn;
    private String resultCode;
    private DatabaseReference ref, getRef;
    private User user;
    private UserInformation userInformation;
    String profile, profileExtension, cover, coverExtension;
    private List<UserInformation> userInformations;
    private FirebaseAuth mAuth;
    String uID;
    public static final int PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        initView();
        //scanner.setAspectTolerance(0.5f);
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        backBtn.setOnClickListener(this::onClick);
        //getUser("FbQo3cEWOQQuSa7O3sPdoCH9bI02");
    }

    public void initView()
    {
        scanner = findViewById(R.id.scanner);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    public void onClick(View v) {
        if(v == backBtn)
        {
            finish();
        }
    }

    @Override
    public void handleResult(Result result) {
        resultCode = result.getText();
        //Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        if( resultCode != null || resultCode != "") {

            scanner.stopCamera();
            getRef = FirebaseDatabase.getInstance().getReference();
            ref = getRef.child("user").child("id").child(resultCode).child("username");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    new FancyGifDialog.Builder(ScannerActivity.this)
                            .setTitle(dataSnapshot.getValue(String.class))
                            .setMessage("Do you want to save the information you received?")
                            .setNegativeBtnText("Cancel")
                            .setPositiveBtnBackground("#56c0fb")
                            .setPositiveBtnText("Save")
                            .setNegativeBtnBackground("#FFA9A7A8")
                            .setGifResource(R.drawable.gif)   //Pass your Gif here
                            .isCancellable(false)
                            .OnPositiveClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {
                                    Toast.makeText(ScannerActivity.this,"Save Successfull",Toast.LENGTH_SHORT).show();
                                    getUser(resultCode);
                                    onResume();
                                }
                            })
                            .OnNegativeClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {
                                    Toast.makeText(ScannerActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                                    onResume();
                                }
                            })
                            .build();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        scanner.resumeCameraPreview(this);
    }

    public void getUser(String resultCode)
    {
        getRef = FirebaseDatabase.getInstance().getReference();
        ref = getRef.child("user").child("id").child(resultCode);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = new User();
                userInformation = new UserInformation();
                userInformations = new ArrayList<>();
                String name = dataSnapshot.child("username").getValue(String.class);
                String phoneNum = dataSnapshot.child("phone").getValue(String.class);
                profile = dataSnapshot.child("image").getValue(String.class);
                profileExtension = dataSnapshot.child("extension").getValue(String.class);
                cover = dataSnapshot.child("cover").getValue(String.class);
                coverExtension = dataSnapshot.child("cvExtension").getValue(String.class);
                String id = dataSnapshot.child("id").getValue(String.class);

                user.setUsername(name);
                user.setPhone(phoneNum);
                user.setImage(profile);
                user.setExtension(profileExtension);
                user.setCover(cover);
                user.setCvExtension(coverExtension);
                user.setId(id);

                for (DataSnapshot d: dataSnapshot.child("contact").getChildren()) {
                    userInformation = d.getValue(UserInformation.class);
                    Log.d("userDatas", "onDataChange: " + userInformation.getUsername());
                    //setInformationSharing(userInformation);
                    userInformations.add(userInformation);
                }

                user.setContact(userInformations);
                setInformationUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setInformationUser(User user)
    {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("user").child("id").child(uID).child("sharing").child(resultCode).setValue(user);
    }

    public void setInformationSharing(UserInformation userInformation)
    {
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("user").child("id").child(uID).child("sharing").child(resultCode).child("sharingContact").child(userInformation.getTitle()).setValue(userInformation);
    }

    //ask camera permission
    public void camera() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            scanner.setResultHandler(ScannerActivity.this);
            scanner.startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                scanner.setResultHandler(ScannerActivity.this);
                scanner.startCamera();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot scan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
