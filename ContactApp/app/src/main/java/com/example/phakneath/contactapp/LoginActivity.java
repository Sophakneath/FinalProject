package com.example.phakneath.contactapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.model.ContactInPhone;
import com.example.phakneath.contactapp.sharePreferences.UserPreferences;
import com.example.phakneath.contactapp.singleton.ContactInMemory;
import com.example.phakneath.contactapp.singleton.ContactInPhoneObj;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.forusers.heinsinputdialogs.HeinsInputDialog;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputDoubleListener;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputStringListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView gotoRegister, forgotPassword, termAndCondition;
    private Button login, fbLogin;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private CallbackManager mCallbackManager;

    private String txtemail, txtpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        mAuth = FirebaseAuth.getInstance();
        gotoRegister.setOnClickListener(this::onClick);
        login.setOnClickListener(this::onClick);
        forgotPassword.setOnClickListener(this::onClick);
        fbLogin.setOnClickListener(this::onClick);
        termAndCondition.setOnClickListener(this::onClick);
    }

    void initViews()
    {
        gotoRegister = findViewById(R.id.gotoRegister);
        login = findViewById(R.id.btnLogin);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        forgotPassword= findViewById(R.id.txtforgotPassword);
        progressBar = findViewById(R.id.progressbar);
        fbLogin = findViewById(R.id.fbLogin);
        termAndCondition = findViewById(R.id.termAndCondition);

    }

    public void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    if(mAuth.getCurrentUser().isEmailVerified()) {
                        UserPreferences.save(LoginActivity.this, password);
                        Intent intent = new Intent(LoginActivity.this, ContactActivity.class);
                        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        new FancyAlertDialog.Builder(LoginActivity.this)
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
                                                    Toast.makeText(LoginActivity.this, "Please Check Your Email to Verify", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
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
                    }
                    Log.d("ooooo", "onComplete: " + mAuth.getCurrentUser().getIdToken(true));
                    Log.d("ooooo", "onComplete: " + mAuth.getCurrentUser().getUid());
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    String message = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void openForgetPasswordDialog()
    {
        HeinsInputDialog dialog = new HeinsInputDialog(this);
        dialog.setPositiveButton("Submit", new OnInputStringListener()
        {
            @Override
            public boolean onInputString(AlertDialog alertDialog, String s) {
                if(TextUtils.isEmpty(s))
                {
                    Toast.makeText(LoginActivity.this, "Please write your valid email address before you submit.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                {
                    mAuth.sendPasswordResetEmail(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Please Check Your Email Account.", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error Occure : " + message + " Please try again.", Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    });

                }
                return false;
            }
        });
        dialog.setTitle("Reset Password");
        dialog.setHint("Email");
        dialog.setMessage("We will send you the link to reset your password after you submit.");
        dialog.setIcon(R.drawable.common_google_signin_btn_icon_dark);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onClick(View v) {
        if(v == gotoRegister)
        {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
        else if(v == login)
        {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
                Toast.makeText(this, "Please Enter Require Information above !", Toast.LENGTH_SHORT).show();
            } else {
                txtemail = email.getText().toString().trim();
                txtpassword = password.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                loginUser(txtemail, txtpassword);
            }
        }
        else if(v == forgotPassword)
        {
            openForgetPasswordDialog();
        }
        else if(v == termAndCondition)
        {
            startActivity(new Intent(this, TermConditionActivity.class));
        }
    }
}
