package com.example.phakneath.contactapp;

import android.Manifest;
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
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.dialog.DetailInformationDialog;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinner;
    private ImageView backBtn, imageCode;
    private TextView save, logo, qrCode;
    private CircleImageView img;
    private EditText name, additonal, email;
    private CardView container;
    private RelativeLayout code;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String uID;
    String num  = "", pathImage, extension;
    private Uri uri;
    private ProgressBar progressBar;
    private Intent intent;
    UserInformation userInformation;
    private TextView titleLogos;
    StorageReference ref, storage;
    String p = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        initView();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        backBtn.setOnClickListener(this::onClick);
        save.setOnClickListener(this::onClick);
        container.setVisibility(View.GONE);
        imageCode.setOnClickListener(this::onClick);
        intent = getIntent();
        userInformation = (UserInformation) intent.getSerializableExtra("information");
        if(userInformation != null) { chooseEdit(); spinner.setVisibility(View.GONE); titleLogos.setVisibility(View.VISIBLE);}
        else { spinner.setVisibility(View.VISIBLE); setupSpinner(); titleLogos.setVisibility(View.GONE);}
    }

    private void initView() {
        spinner = findViewById(R.id.spinner);
        backBtn = findViewById(R.id.backBtn);
        save= findViewById(R.id.saveChange);
        logo = findViewById(R.id.logo);
        img = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        additonal = findViewById(R.id.additional);
        container = findViewById(R.id.containerInformation);
        email = findViewById(R.id.email);
        code = findViewById(R.id.qrCodeContainer);
        imageCode = findViewById(R.id.imageCode);
        qrCode = findViewById(R.id.qrCode);
        progressBar = findViewById(R.id.progressbar);
        titleLogos = findViewById(R.id.titleApp);
    }

    public void setupSpinner()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.infoList, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0)
                {
                    container.setVisibility(View.VISIBLE);
                    ClearEditText();
                    qrCode.setVisibility(View.VISIBLE);

                }
                switch(position)
                {
                    case 0: container.setVisibility(View.GONE);
                            logo.setVisibility(View.VISIBLE);
                            img.setImageDrawable(getDrawable(R.color.white));
                            break;
                    case 1: email();
                            break;
                    case 2: facebook(); break;
                    case 3: instagram(); break;
                    case 4: line(); break;
                    case 5: linkedin(); break;
                    case 6: skype(); break;
                    case 7: telegram(); break;
                    case 8: twitter(); break;
                    case 9: viber(); break;
                    case 10: wechat(); break;
                    case 11: whatsapp(); break;
                    case 12: contact(); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void chooseEdit()
    {
        container.setVisibility(View.VISIBLE);
        ClearEditText();
        qrCode.setVisibility(View.VISIBLE);

        titleLogos.setText(userInformation.getTitle());
        name.setText(userInformation.getUsername());
        if(userInformation.getQrCode()!=null) DetailInformationDialog.getImage(AddInfoActivity.this, userInformation,imageCode,userInformation.getQrCode() +"." + userInformation.getExtensionCode());
        qrCode.setVisibility(View.GONE);
        switch(userInformation.getTitle())
        {
            case "Email": email();
                        name.setTag("EditUsername");
                        email.setText(userInformation.getUserEmail());
                        break;
            case "Facebook": facebook();
                        name.setTag("EditFacebookUsername");
                        additonal.setText(userInformation.getUserID());
                        break;
            case "Instagram": instagram();
                        name.setTag("EditInstagramUsername"); break;
            case "Line": line();
                        name.setTag("EditLineUsername");
                        additonal.setText(userInformation.getUserID()); break;
            case "Linkedin":
                        linkedin();
                        name.setTag("EditLinkedinUsername");
                        break;
            case "Skype": skype();
                        name.setTag("EditSkypeUsername");
                        additonal.setText(userInformation.getPhoneNum());
                        email.setText(userInformation.getUserEmail());
                        break;
            case "Telegram": telegram();
                        name.setTag("EditTelegramUsername");
                        additonal.setText(userInformation.getPhoneNum()); break;
            case "Twitter": twitter();
                        name.setTag("EditTwitterUsername");
                        break;
            case "Viber": viber();
                        name.setTag("EditViberUsername");
                        additonal.setText(userInformation.getPhoneNum()); break;
            case "Wechat": wechat();
                        name.setTag("EditWechatUsername");
                        additonal.setText(userInformation.getPhoneNum()); break;
            case "Whatsapp": whatsapp();
                        name.setTag("EditWhatsappUsername");
                        additonal.setText(userInformation.getPhoneNum());break;
            case "Contact Number": phoneNum();
                        name.setTag("EditContactNumberUsername");
                        additonal.setText(userInformation.getPhoneNum()); break;
            case "Contact Number ": contact();
                        name.setTag("EditMyContact");
                        additonal.setText(userInformation.getPhoneNum()); break;
        }
        name.setSelection(name.getText().length());
        additonal.setSelection(additonal.getText().length());
        email.setSelection(email.getText().length());
    }

    public void contact()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.phonecalllogo).into(img);
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        additonal.setVisibility(View.VISIBLE);
        code.setVisibility(View.GONE);

        name.setHint("Name ");
        name.setTag("Name ");
        additonal.setHint("Phone Number");
    }

    public void phoneNum()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.phonecalllogo).into(img);
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        additonal.setVisibility(View.VISIBLE);
        code.setVisibility(View.GONE);

        name.setHint("Name");
        name.setTag("Name ");
        additonal.setHint("Phone Number");
        name.setEnabled(false);
        additonal.setEnabled(false);
    }

    public void email()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.emailogo).into(img);
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.GONE);
        code.setVisibility(View.GONE);

        name.setHint("Username");
        name.setTag("Username");
        email.setHint("Email");
    }

    public void facebook()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.facebooklogo).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);

        name.setHint("Facebook Username");
        name.setTag("Facebook Username");
        additonal.setHint("Facebook URL");
        num = String.format("%s\n%s", "Facebook", "QRCODE");
        qrCode.setText(num);
    }

    public void instagram()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.instagram).into(img);
        name.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        additonal.setVisibility(View.GONE);

        name.setHint("Instagram Username");
        name.setTag("Instagram Username");
        num = String.format("%s\n%s", "Instagram", "NameTag");
        qrCode.setText(num);
    }

    public void line()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.linelogo).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);

        name.setHint("Line Username");
        name.setTag("Line Username");
        additonal.setHint("Line ID");
        num = String.format("%s\n%s", "Line", "QRCODE");
        qrCode.setText(num);
    }

    public void linkedin()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.linkedin).into(img);
        name.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        additonal.setVisibility(View.GONE);

        name.setHint("Linkedin Username");
        name.setTag("Linkedin Username");
        num = String.format("%s\n%s", "Linkedin", "QRCODE");
        qrCode.setText(num);
    }

    public void skype()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.skype).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        code.setVisibility(View.GONE);

        name.setHint("Skype Username");
        name.setTag("Skype Username");
        additonal.setHint("Phone Number");
        email.setHint("Email");
    }

    public void telegram()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.telegram).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        code.setVisibility(View.GONE);

        name.setHint("Telegram Username");
        name.setTag("Telegram Username");
        additonal.setHint("Phone Number");
    }

    public void twitter()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.twitter).into(img);
        name.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        additonal.setVisibility(View.GONE);

        name.setHint("Twitter Username");
        name.setTag("Twitter Username");
        num = String.format("%s\n%s", "Twitter", "QRCODE");
        qrCode.setText(num);
    }

    public void viber()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.viber).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);

        name.setHint("Viber Username");
        name.setTag("Viber Username");
        additonal.setHint("Phone Number");
        num = String.format("%s\n%s", "Viber", "QRCODE");
        qrCode.setText(num);
    }

    public void wechat()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.wechat).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);

        name.setHint("Wechat Username");
        name.setTag("Wechat Username");
        additonal.setHint("Phone Number");
        num = String.format("%s\n%s", "Wechat", "QRCODE");
        qrCode.setText(num);
    }

    public void whatsapp()
    {
        logo.setVisibility(View.GONE);
        Picasso.with(this).load(R.drawable.whatsapp).into(img);
        name.setVisibility(View.VISIBLE);
        additonal.setVisibility(View.VISIBLE);
        email.setVisibility(View.GONE);
        code.setVisibility(View.GONE);

        name.setHint("Whatsapp Username");
        name.setTag("Whatsapp Username");
        additonal.setHint("Phone Number");
    }

    public void ClearEditText()
    {
        name.setText("");
        additonal.setText("");
        email.setText("");
        imageCode.setBackground(this.getResources().getDrawable(R.drawable.retangle_edittext));
    }

    public void setDataBase(UserInformation userInformation, String name)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uID).child("contact").child(name).setValue(userInformation);
    }

    public int setContact(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input All of The Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setAdditional(additonal.getText().toString());
        userInformation.setTitle("Contact Number ");

        if(count == 1){
            userInformation.setId("mycontact" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());}
        return 1;
    }

    public int setEmail(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(email.getText(), ""))
        {
            Toast.makeText(this, "Please Input All of The Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setUserEmail(email.getText().toString());
        userInformation.setTitle("Email");

        if(count == 1){
            userInformation.setId("email" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());}
        return 1;

    }

    public int setFacebook(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input All of The Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setUserID(additonal.getText().toString());
        userInformation.setTitle("Facebook");
        //setDataBase(userInformation, "facebook");

        if(count == 1){
            userInformation.setId("facebook" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}
        return 1;
    }

    public int setInstagram(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setTitle("Instagram");

        if(count == 1){
            userInformation.setId("instagram" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}

        //setDataBase(userInformation,"instagram");
        //addqrCode(uID, uri, pathImage, "instagram");
        return 1;
    }

    public int setLine(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setUserID(additonal.getText().toString());
        userInformation.setTitle("Line");

        if(count == 1){
            userInformation.setId("line" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
                userInformation.setId(this.userInformation.getId());
                setDataBase(userInformation, userInformation.getId());
                if(pathImage==null && uri == null) {
                    extension = this.userInformation.getExtensionCode();
                    pathImage = this.userInformation.getQrCode();
                    addqrCode(uID, uri, pathImage, userInformation.getId());
                }
                else
                    addqrCode(uID, uri, pathImage, userInformation.getId());
            }

        //setDataBase(userInformation,"line");
        //addqrCode(uID, uri, pathImage, "line");
        return 1;
    }

    public int setLinkedin(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setTitle("Linkedin");

        if(count == 1){
            userInformation.setId("linkedin" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}

        //setDataBase(userInformation,"linkedin");
        //addqrCode(uID, uri, pathImage, "linkedin");
        return 1;
    }

    public int setSkype(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(email.getText(), "") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setPhoneNum(additonal.getText().toString());
        userInformation.setUserEmail(email.getText().toString());
        userInformation.setTitle("Skype");

        if(count == 1){
            userInformation.setId("skype" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId()); }
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId()); }
        //setDataBase(userInformation,"skype");
        return 1;
    }

    public int setTelegram(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setPhoneNum(additonal.getText().toString());
        userInformation.setTitle("Telegram");

        if(count == 1){
            userInformation.setId("telegram" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId()); }
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId()); }

            //setDataBase(userInformation,"telegram");
        return 1;
    }

    public int setTwitter(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setTitle("Twitter");

        if(count == 1){
            userInformation.setId("twitter" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}

            //setDataBase(userInformation,"twitter");
        //addqrCode(uID, uri, pathImage, "twitter");
        return 1;
    }

    public int setViber(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setPhoneNum(additonal.getText().toString());
        userInformation.setTitle("Viber");

        if(count == 1){
            userInformation.setId("viber" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}

            //setDataBase(userInformation,"viber");
        //addqrCode(uID, uri, pathImage, "viber");
        return 1;
    }

    public int setWechat(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setPhoneNum(additonal.getText().toString());
        userInformation.setTitle("Wechat");

        if(count == 1){
            userInformation.setId("wechat" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}

            //setDataBase(userInformation,"wechat");
        //addqrCode(uID, uri, pathImage, "wechat");
        return 1;
    }

    public int setWhatsapp(UserInformation userInformation, int count)
    {
        if(TextUtils.equals(name.getText(),"") || TextUtils.equals(additonal.getText(), ""))
        {
            Toast.makeText(this, "Please Input Information", Toast.LENGTH_SHORT).show();
            return 0;
        }
        userInformation.setUsername(name.getText().toString());
        userInformation.setPhoneNum(additonal.getText().toString());
        userInformation.setTitle("Whatsapp");

        if(count == 1){
            userInformation.setId("whatsapp" + System.currentTimeMillis());
            setDataBase(userInformation, userInformation.getId());
            addqrCode(uID, uri, pathImage, userInformation.getId());}
        else{
            userInformation.setId(this.userInformation.getId());
            setDataBase(userInformation, userInformation.getId());
            if(pathImage==null && uri == null) {
                extension = this.userInformation.getExtensionCode();
                pathImage = this.userInformation.getQrCode();
                addqrCode(uID, uri, pathImage, userInformation.getId());
            }
            else
                addqrCode(uID, uri, pathImage, userInformation.getId());}

            //setDataBase(userInformation,"whatsapp");
        return 1;
    }


    @Override
    public void onClick(View v) {
        if(v == backBtn)
        {
            finish();
        }
        else if( v == save)
        {
            int s = 1;
            if(spinner.getSelectedItemPosition() == 0) {
                //Toast.makeText(this, "Please Choose What you want to add frist before click Save.", Toast.LENGTH_SHORT).show();
                s = 0;
            }
            save.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            UserInformation userInformation = new UserInformation();
            switch(name.getTag().toString())
            {
                case "Name ": s = setContact(userInformation, 1); break;
                case "Username": s = setEmail(userInformation, 1); break;
                case "Facebook Username": s = setFacebook(userInformation, 1); break;
                case "Instagram Username": s = setInstagram(userInformation,1); break;
                case "Line Username": s = setLine(userInformation,1); break;
                case "Linkedin Username": s = setLinkedin(userInformation,1); break;
                case "Skype Username": s = setSkype(userInformation,1); break;
                case "Telegram Username": s = setTelegram(userInformation,1); break;
                case "Twitter Username": s = setTwitter(userInformation,1); break;
                case "Viber Username": s = setViber(userInformation,1); break;
                case "Wechat Username": s = setWechat(userInformation,1); break;
                case "Whatsapp Username": s = setWhatsapp(userInformation,1); break;
                case "EditUsername": s= setEmail(userInformation, 2); break;
                case "EditFacebookUsername": s=setFacebook(userInformation,2); break;
                case "EditInstagramUsername": s= setInstagram(userInformation, 2); break;
                case "EditLineUsername": s=setLine(userInformation,2); break;
                case "EditLinkedinUsername": s= setLinkedin(userInformation, 2); break;
                case "EditSkypeUsername": s=setSkype(userInformation,2); break;
                case "EditTelegramUsername": s= setTelegram(userInformation, 2); break;
                case "EditTwitterUsername": s=setTwitter(userInformation,2); break;
                case "EditViberUsername": s= setViber(userInformation, 2); break;
                case "EditWechatUsername": s=setWechat(userInformation,2); break;
                case "EditWhatsappUsername": s=setWhatsapp(userInformation,2); break;
                case "EditMyContact": s = setContact(userInformation, 2); break;

            }
            if(s != 0) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        save.setVisibility(View.VISIBLE);
                        Toast.makeText(AddInfoActivity.this, "Information Added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, 2000);
            }
            else if( s == 0){
                progressBar.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
            }

        }
        else if(v == imageCode)
        {
            gallery();
        }
    }

    //permission for open gallery
    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, Uri.parse(MediaStore.Images.Media.DATA));
        i.setType("image/*");
        startActivityForResult(i,EditProfileActivity.OPEN_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == EditProfileActivity.OPEN_GALLERY)
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
        if(requestCode == EditProfileActivity.OPEN_GALLERY)
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
                Bitmap bitmap = BitmapFactory.decodeFile(pathImage);

                //Picasso.with(this).load(pathImage).into(imageCode);
                imageCode.setImageBitmap(bitmap);
                qrCode.setVisibility(View.GONE);
                pathImage = uri.getLastPathSegment() + System.currentTimeMillis();
                extension = getFileExtension(uri);
                Log.e("ooooo", "Path Image : " + pathImage);
                cursor.close();

            }
        }
    }

    public void gallery()
    {
        if(ContextCompat.checkSelfPermission(AddInfoActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != getPackageManager().PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},EditProfileActivity.OPEN_GALLERY);
        }
        else
        {
            openGallery();
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void addqrCode(String uID, Uri uri, String pathImage, String uploadinto)
    {

        if(EditProfileActivity.mUploadTask == null || !EditProfileActivity.mUploadTask.isInProgress())
            uploadImage(uri);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child("id").child(uID).child("contact").child(uploadinto).child("qrCode").setValue(pathImage);
        mDatabase.child("user").child("id").child(uID).child("contact").child(uploadinto).child("extensionCode").setValue(extension);
    }

    //upload image to Firebase Storage
    public void uploadImage(Uri uri) {
        if(userInformation != null)
        {p = userInformation.getQrCode() + "." + userInformation.getExtensionCode();}
        ref = FirebaseStorage.getInstance().getReference();
        if(uri != null)
        {
            storage = ref.child("qrCode/").child( pathImage + "." + extension);
            EditProfileActivity.mUploadTask = storage.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if(userInformation != null) {storage = ref.child("qrCode/").child(p);
                            storage.delete();}
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
}
