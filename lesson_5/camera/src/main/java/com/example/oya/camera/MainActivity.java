package com.example.oya.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private ImageView imageView;
    private TextView statusText;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        statusText = findViewById(R.id.statusTextView);

        imageView.setOnClickListener(v -> {
            trySystemCamera();
        });
    }

    private void trySystemCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File photoFile = createPhotoFile();

            Uri photoUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
                statusText.setText("Запускаем камеру...");
            } catch (Exception e) {
                createCustomPhoto();
            }

        } catch (Exception e) {
            createCustomPhoto();
        }
    }

    private File createPhotoFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String filename = "photo_" + timestamp + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File file = new File(storageDir, filename);
        photoPath = file.getAbsolutePath();
        return file;
    }

    private void createCustomPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        android.graphics.Paint paint = new android.graphics.Paint();

        // Фон
        paint.setColor(android.graphics.Color.rgb(25, 118, 210));
        canvas.drawRect(0, 0, 600, 800, paint);

        paint.setColor(android.graphics.Color.WHITE);
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawCircle(300, 300, 100, paint);

        paint.setStyle(android.graphics.Paint.Style.FILL);
        paint.setTextSize(30);
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        canvas.drawText("СОХРАНЕННОЕ ФОТО", 300, 500, paint);
        canvas.drawText(sdf.format(new Date()), 300, 550, paint);

        try {
            File photoFile = createPhotoFile();
            FileOutputStream fos = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.close();

            imageView.setImageBitmap(bitmap);
            statusText.setText("Фото создано и сохранено\n" + photoFile.getName());

            Toast.makeText(this, "Фото сохранено в файл", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            imageView.setImageBitmap(bitmap);
            statusText.setText("Фото создано (ошибка сохранения)");
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                imageView.setImageBitmap(bitmap);

                File photoFile = new File(photoPath);
                statusText.setText("Системная камера\n" + photoFile.getName());

                Toast.makeText(this, "Фото сохранено", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                statusText.setText("Ошибка загрузки фото");
            }
        }
    }
}