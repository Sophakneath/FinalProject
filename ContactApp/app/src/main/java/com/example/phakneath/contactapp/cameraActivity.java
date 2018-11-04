package com.example.phakneath.contactapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class cameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private TextView result;
    private Button scanner;
    private SurfaceView surface;
    public static final int requestID = 1;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        scanner = findViewById(R.id.scanner);
        result = findViewById(R.id.result);
        surface = findViewById(R.id.surface);

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan(v);
            }
        });


    }

    public void scan(View view)
    {
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this::handleResult);
        zXingScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        if (result.getText() != null) {
            Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
            //zXingScannerView.resumeCameraPreview(this::handleResult);
            zXingScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
            zXingScannerView.stopCamera(); //<- then stop the camera
            setContentView(R.layout.activity_camera); //<- and set the View again.
            //zXingScannerView.resumeCameraPreview(this::handleResult);
        }
        else zXingScannerView.resumeCameraPreview(this::handleResult);

    }
}
