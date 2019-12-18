package com.example.sharedpreferences;

import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private Button save, read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.iv_image);
        save = findViewById(R.id.save);
        read = findViewById(R.id.read);

        save.setOnClickListener(this);
        read.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                saveToSD("one.jpg");
                break;
            case R.id.read:
                readToSD("one.jpg");
                break;
        }
    }

    //SD卡：外部的公有文件夹，需要申请运行时权限
    private void readToSD(String fileName) {
        //申请读SD的权限，要求android的版本大于6.0（Build.VERSION_CODES_M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                return;
            }
        }

        //读SD卡的步骤
        //读取SD卡上的文件
        String path = Environment.getExternalStoragePublicDirectory("").getPath()
                + File.separator
                + Environment.DIRECTORY_PICTURES;
        File file = new File(path, fileName);
        try {
            //创建file的文件输入流
            FileInputStream inputStream = new FileInputStream(file);
            //将文件流写入imageView
            imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            //关闭文件输入流
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //SD卡：外部的公有文件夹，需要申请运行时权限
    private void saveToSD(String fileName) {
        //申请写SD的权限，要求android的版本大于6.0（Build.VERSION_CODES_M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        //写SD卡的步骤
        //获取SD卡的Download目录，创建需要的存储的文件
        String path = Environment.getExternalStoragePublicDirectory("").getPath()
                + File.separator
                + Environment.DIRECTORY_PICTURES;
        File file = new File(path, fileName);
        try {
            if (file.createNewFile()) {
                //获取ImageView的Bitmap图片对象
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                //将Bitmap对象写入SD卡
                FileOutputStream outputStream = new FileOutputStream(file);
                //带缓冲
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                //关闭请求的资源
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "权限申请被拒绝", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case 1:
                saveToSD("one.jpg");
                break;
            case 2:
                readToSD("one.jpg");
                break;
        }
    }
}
