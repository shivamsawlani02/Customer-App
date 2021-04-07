package com.dsdairysytem.clientappfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRCodeGenerator extends AppCompatActivity {

    ImageView qrCode,backButton;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    String inputValue = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        qrCode = findViewById(R.id.qr_code);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QRCodeGenerator.this,MainActivity.class));
                finish();
            }
        });
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDim = Math.min(width, height);
        smallerDim = smallerDim * 3 / 4;
        qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDim);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(QRCodeGenerator.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}