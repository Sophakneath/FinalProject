package com.example.phakneath.contactapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;


public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView qrCode, backBtn;
    private TextView scanner;
    Bitmap bitmap;
    BitMatrix bitMatrix;
    private FirebaseAuth mAuth;
    String uID;
    MultiFormatWriter multiFormatWriter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        initview();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        scanner.setOnClickListener(this::onClick);
        backBtn.setOnClickListener(this::onClick);

        getQrCode(uID);
    }

    private void initview() {
        qrCode = findViewById(R.id.qrCode);
        backBtn = findViewById(R.id.backBtn);
        scanner = findViewById(R.id.scanner);
    }

    private void getQrCode(String uID)
    {
        if (uID != null) {
            multiFormatWriter = new MultiFormatWriter();
            try {
                bitMatrix = multiFormatWriter.encode(uID, BarcodeFormat.QR_CODE,
                        500, 500);
                bitmap = new BarcodeEncoder().createBitmap(bitMatrix);

                qrCode.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == scanner)
        {
            startActivity(new Intent(this, ScannerActivity.class));
        }
        else if(v == backBtn)
        {
            finish();
        }
    }


}
