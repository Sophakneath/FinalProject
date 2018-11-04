package com.example.phakneath.contactapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.contactapp.R;
import com.example.phakneath.contactapp.model.ContactInPhone;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactInPhoneDialog extends DialogFragment implements View.OnClickListener{

    View view;
    private CircleImageView img, messagebtn, phonecallbtn;
    private TextView name, phoneNum;
    private ContactInPhone contactInPhone = new ContactInPhone();
    List<String> phone = new ArrayList<>();
    private Button okBtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = LayoutInflater.from(getActivity()).inflate(R.layout.contactinphone_detail_dialog, null);
        AlertDialog dialog =builder.setView(view).setCancelable(false).create();

        initView();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getData();
        messagebtn.setOnClickListener(this::onClick);
        phonecallbtn.setOnClickListener(this::onClick);
        okBtn.setOnClickListener(this::onClick);
        return dialog;
    }

    public void initView()
    {
        img = view.findViewById(R.id.ContactPhoto);
        messagebtn = view.findViewById(R.id.Messagebtn);
        phonecallbtn = view.findViewById(R.id.phoneCallbtn);
        name = view.findViewById(R.id.ContactName);
        phoneNum = view.findViewById(R.id.phoneNumber);
        okBtn = view.findViewById(R.id.okBtn);
    }

    public void updateUI()
    {
        String num = "";
        phone = contactInPhone.getPhone();
        for(int i=0; i<phone.size(); i++)
        {
            num += String.format("%s\n", phone.get(i));
        }

        img.setImageBitmap(contactInPhone.getImg());
        name.setText(contactInPhone.getName());
        phoneNum.setText(num);

    }

    public void getData()
    {
        Bundle bundle = getArguments();
        contactInPhone = bundle.getParcelable("contact");
        updateUI();
    }

    @Override
    public void onClick(View v) {
        if(v == messagebtn)
        {
            openDialogFragment(1);
        }
        else if(v == phonecallbtn)
        {
            openDialogFragment(2);
        }
        else if(v== okBtn)
        {
            this.dismiss();
        }
    }

    public void openDialogFragment(int choose)
    {
        String[] items = new String[phone.size()];
        for(int i = 0; i < phone.size(); i++) items[i] = phone.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a Number");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(choose == 1) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("smsto:"));
                    sendIntent.putExtra("address", items[which]);
                    startActivity(sendIntent);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + items[which]));
                    startActivity(intent);
                }
            }
        });

        builder.setCancelable(true);
        builder.create().show();
    }
}
