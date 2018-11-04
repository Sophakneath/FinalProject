package com.example.phakneath.contactapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phakneath.contactapp.R;
import com.example.phakneath.contactapp.model.User;
import com.example.phakneath.contactapp.model.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailInformationDialog extends DialogFragment implements View.OnClickListener {

    View view;
    private RelativeLayout colorLogos, conUsername, conAdditional, conEmail, conQrCode;
    private ImageView logos;
    private TextView username, txtUsername, additional, txtAdditional, email, txtEmail;
    private ImageView qrCode;
    private Button okBtn;
    private UserInformation userInformation;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = LayoutInflater.from(getActivity()).inflate(R.layout.information_detail_dialog, null);
        AlertDialog dialog =builder.setView(view).setCancelable(false).create();

        int width = getResources().getDimensionPixelSize(R.dimen.width);
        int height = getResources().getDimensionPixelSize(R.dimen.height);
        initView();

        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        okBtn.setOnClickListener(this::onClick);

        getData();
        return dialog;
    }

    public void getData()
    {
        Bundle bundle = getArguments();
        userInformation = (UserInformation) bundle.getSerializable("info");
        //Toast.makeText(getContext(), userInformation.getTitle(), Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void initView() {
        colorLogos = view.findViewById(R.id.colorLogos);
        logos = view.findViewById(R.id.logos);
        username = view.findViewById(R.id.username);
        txtUsername = view.findViewById(R.id.txtUsername);
        additional = view.findViewById(R.id.additional);
        txtAdditional = view.findViewById(R.id.txtaddiontal);
        email = view.findViewById(R.id.email);
        txtEmail = view.findViewById(R.id.txtemail);
        qrCode = view.findViewById(R.id.code);
        okBtn = view.findViewById(R.id.okBtn);
        conUsername = view.findViewById(R.id.conUsername);
        conAdditional = view.findViewById(R.id.conAdditional);
        conEmail = view.findViewById(R.id.conEmail);
        conQrCode = view.findViewById(R.id.conQrCode);
    }

    public void updateUI()
    {
        switch(userInformation.getTitle())
        {
            case "Email": setEmail(); break;
            case "Facebook": setFacebook(); break;
            case "Instagram": setInstagram(); break;
            case "Line": setLine(); break;
            case "Linkedin": setLinkedin(); break;
            case "Skype": setSkype(); break;
            case "Telegram": setTelegram(); break;
            case "Twitter": setTwitter(); break;
            case "Viber": setViber(); break;
            case "Wechat": setWechat(); break;
            case "Whatsapp": setWhatsapp(); break;
            case "Contact Number": setPhoneNum(); break;
            case "Contact Number ": setPhoneNum(); break;
        }
    }

    public void setPhoneNum()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.whatsappColor));
        Picasso.with(getContext()).load(R.drawable.phonecalllogo1).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.GONE);

        txtUsername.setText("Username : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("Phone Number : ");
        additional.setText(userInformation.getPhoneNum());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.whatsappColor));
        additional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + additional.getText().toString()));
                startActivity(intent);
            }
        });
    }

    public void setEmail()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.emailColor));
        Picasso.with(getContext()).load(R.drawable.emailfordetail).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.VISIBLE);
        conAdditional.setVisibility(View.GONE);
        conQrCode.setVisibility(View.GONE);

        txtUsername.setText("Username : ");
        username.setText(userInformation.getUsername());
        txtEmail.setText("Email : ");
        email.setText(userInformation.getUserEmail());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.emailColor));
    }

    public void setFacebook()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.fbColor));
        Picasso.with(getContext()).load(R.drawable.facebooklogo).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Facebook : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("URL : ");
        additional.setText(userInformation.getUserID());
        Linkify.addLinks(additional, Linkify.WEB_URLS);
        getImage(getContext(), userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.fbColor));
    }

    public void setInstagram()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.instagramColor));
        Picasso.with(getContext()).load(R.drawable.instagram).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.GONE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Instagram : ");
        username.setText(userInformation.getUsername());
        getImage(getContext(), userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.instagramColor));
    }

    public void setLine()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.lineColor));
        Picasso.with(getContext()).load(R.drawable.linelogo).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Line : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("LineID : ");
        additional.setText(userInformation.getUserID());
        getImage(getContext(), userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.lineColor));
    }

    public void setLinkedin()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.linkedinColor));
        Picasso.with(getContext()).load(R.drawable.linkedin).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.GONE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Linkedin : ");
        username.setText(userInformation.getUsername());
        getImage(getContext(), userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.linkedinColor));
    }

    public void setSkype()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.skypeColor));
        Picasso.with(getContext()).load(R.drawable.skype).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.VISIBLE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.GONE);

        txtUsername.setText("Skype : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("Phone Number : ");
        additional.setText(userInformation.getPhoneNum());
        txtEmail.setText("Email : ");
        email.setText(userInformation.getUserEmail());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.skypeColor));
    }

    public void setTelegram()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.telegramColor));
        Picasso.with(getContext()).load(R.drawable.telegram).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.GONE);

        txtUsername.setText("Telegram : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("Phone Number : ");
        additional.setText(userInformation.getPhoneNum());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.telegramColor));
    }

    public void setTwitter()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.twitterColor));
        Picasso.with(getContext()).load(R.drawable.twitter).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.GONE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Twitter : ");
        username.setText(userInformation.getUsername());
        getImage(getContext(), userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.twitterColor));
    }

    public void setViber()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.viberColor));
        Picasso.with(getContext()).load(R.drawable.viber).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Viber : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("Phone Number : ");
        additional.setText(userInformation.getPhoneNum());
        getImage(getContext(),userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.viberColor));
    }

    public void setWechat()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.wechatColor));
        Picasso.with(getContext()).load(R.drawable.wechat).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.VISIBLE);

        txtUsername.setText("Wechat : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("Phone Number : ");
        additional.setText(userInformation.getPhoneNum());
        getImage(getContext(),userInformation, qrCode, userInformation.getQrCode() + "." + userInformation.getExtensionCode());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.wechatColor));
    }

    public void setWhatsapp()
    {
        colorLogos.setBackground(getContext().getResources().getDrawable(R.color.whatsappColor));
        Picasso.with(getContext()).load(R.drawable.whatsapp).into(logos);
        conUsername.setVisibility(View.VISIBLE);
        conEmail.setVisibility(View.GONE);
        conAdditional.setVisibility(View.VISIBLE);
        conQrCode.setVisibility(View.GONE);

        txtUsername.setText("Whatsapp : ");
        username.setText(userInformation.getUsername());
        txtAdditional.setText("Phone Number : ");
        additional.setText(userInformation.getPhoneNum());
        okBtn.setBackground(getContext().getResources().getDrawable(R.color.whatsappColor));
    }

    //get image from firebase database
    public static void getImage(Context context, UserInformation user, ImageView img, String getImage)
    {
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("qrCode/"+getImage);
        try {
            /*final File localFile = File.createTempFile("Image", user.getExtensionCode());
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    img.setImageBitmap(my_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });*/

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Glide.with(context).load(uri).into(img);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
