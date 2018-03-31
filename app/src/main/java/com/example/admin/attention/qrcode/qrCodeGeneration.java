package com.example.admin.attention.qrcode;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.admin.attention.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class qrCodeGeneration extends AppCompatActivity {
    private ImageView qrImage;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generation);
        qrImage=findViewById(R.id.qrImage);


        QRGEncoder qrgEncoder = new QRGEncoder(FirebaseAuth.getInstance().getUid(), null, QRGContents.Type.TEXT, 350);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.i("error qr code", e.toString());
        }

    }
}
