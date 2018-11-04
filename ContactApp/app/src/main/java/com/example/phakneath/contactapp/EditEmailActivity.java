package com.example.phakneath.contactapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.sharePreferences.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

public class EditEmailActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView saveChange;
    private ProgressBar progressBar;
    private EditText email;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        initView();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        email.setText(user.getEmail().toString());
        email.setSelection(email.getText().length());
        saveChange.setOnClickListener(this::onClick);
        backBtn.setOnClickListener(this::onClick);
    }

    private void initView() {
        saveChange = findViewById(R.id.saveChange);
        progressBar = findViewById(R.id.progressbar);
        email = findViewById(R.id.email);
        backBtn = findViewById(R.id.backBtn);
    }

    public void changeEmail()
    {
        String password = UserPreferences.getUser(this);
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail().toString(), password); // Current Login Credentials \\

        progressBar.setVisibility(View.VISIBLE);
        saveChange.setVisibility(View.GONE);
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.d("ooooo", "User re-authenticated.");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(email.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("ooooo", "User email address updated.");
                                            progressBar.setVisibility(View.GONE);
                                            saveChange.setVisibility(View.VISIBLE);
                                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(EditEmailActivity.this, "Please Check Your Email to Verify", Toast.LENGTH_LONG).show();
                                                        mAuth.signOut();
                                                        ContactActivity.activity.finish();
                                                        ProfileActivity.activity.finish();
                                                        EditProfileActivity.activity.finish();
                                                        Intent intent = new Intent(EditEmailActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditEmailActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        saveChange.setVisibility(View.VISIBLE);
                                    }
                                });
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEmailActivity.this, e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        saveChange.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == saveChange)
        {
            new FancyAlertDialog.Builder(EditEmailActivity.this)
                    .setTitle("Verification")
                    .setBackgroundColor(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                    .setMessage("By changing your email, you have to verify your new email and log in again. Are you sure you want to change your email?")
                    .setPositiveBtnBackground(Color.parseColor("#56c0fb"))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText("Yes")
                    .setNegativeBtnText("No")
                    .setAnimation(Animation.SIDE)
                    .isCancellable(false)
                    .setIcon(R.drawable.infos, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            changeEmail();
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }).build();
        }
        else if(v == backBtn)
        {
            finish();
        }
    }
}
