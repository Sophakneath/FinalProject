package com.example.phakneath.contactapp.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.View;

import com.example.phakneath.contactapp.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class photoPermission extends Activity {

    public static final int OPEN_GALLERY = 1;
    private String pathImage;
    Uri uri;

    private Context context;
    public photoPermission(Context context)
    {
        this.context = context;
    }

    //permission for open gallery
    public Uri openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, Uri.parse(MediaStore.Images.Media.DATA));
        i.setType("image/*");
        startActivityForResult(i,OPEN_GALLERY);
        return uri;
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
                /*String[] column = { MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, column, null, null, null);
                cursor.moveToFirst();

                pathImage = cursor.getString(cursor.getColumnIndex(column[0]));
                Bitmap bitmap = BitmapFactory.decodeFile(pathImage);

                writeCoverPhoto(uID);
                progressBarCover.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBarCover.setVisibility(View.GONE);
                    }
                }, 1000);
                coverPhoto.setImageBitmap(bitmap);
                Log.e("ooooo", "Path Image : " + pathImage);
                cursor.close();*/

            }
        }
    }

    public void gallery()
    {
        if(ContextCompat.checkSelfPermission(context,
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

}
