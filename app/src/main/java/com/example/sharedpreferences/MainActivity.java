package com.example.sharedpreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button into, btnReturn;
    private EditText username, password;
    private CheckBox checkBox;
    private String fileName = "login.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //获取存储的用户信息，若有则写入
//        String username2 = redprfs();
//        String username2 = readDataInternal(fileName);
        String username2 = readPrivateExStorage(fileName);
        if (username2 != null) {
            username.setText(username2);
        }
    }

    private String redprfs() {
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        return sp.getString("username", "");
    }

    private void initView() {
        //获取控件对象
        into = findViewById(R.id.btn_into);
        btnReturn = findViewById(R.id.btn_return);
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        checkBox = findViewById(R.id.checkBox);
        //设置登录点击事件的监听器
        into.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_into:
                login();
                break;
            case R.id.btn_return:
                finish();
                break;
        }
    }

    private void login() {
        //获取输入的用户名和密码
        String username1 = username.getText().toString();
        String password1 = password.getText().toString();

        //判断“记住用户名”是否勾选，若已选则存储用户名，若未选则清空存储用户名
        if (checkBox.isChecked()) {
            savePref(username1);
            saveDataInternal(fileName, username1);
            saveDataPrivate(fileName, username1);
        } else {
            clearpref();//删除文件的方式
            deleteDataFile(fileName);
            deletePrivateExStorage(fileName);
        }

        //判断输入的用户名和密码是否正确，正确则跳转到Main2Activity
        if (username1.equals("zhu")&&password1.equals("123")){
            Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intent);
        }else {
            Toast.makeText(MainActivity.this,"您输入的用户名或密码不正确，请重新输入",Toast.LENGTH_LONG).show();
            username.setText("");
            password.setText("");
        }

    }

    private void savePref(String username1) {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putString("username", username1);
        editor.apply();
    }

    private void clearpref() {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        //editor.putString("username","");//只是清掉用户名，并没有将整个文件删除
        editor.clear().apply();//删除整个文件
    }

    //内部存储
    //保存
    private void saveDataInternal(String fileName, String username) {
        //内部存储目录：data/data/包名/files
        try {
            //打开文件输出流
            FileOutputStream out = openFileOutput(fileName, Context.MODE_PRIVATE);
            //创建BufferedWriter对象
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            //写入数据
            writer.write(username);
            //关闭输出流
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取
    private String readDataInternal(String fileName) {
        String data = null;
        try {
            //打开文件输入流
            FileInputStream in = openFileInput(fileName);
            //创建BufferedReader对象
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //读出数据
            data = reader.readLine();
            //关闭输入流
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    //删除内部存储文件
    private void deleteDataFile(String fileName) {
        if (deleteFile(fileName)) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
        }
    }

    //外部存储
    //保存
    private void saveDataPrivate(String fileName, String username) {
        //内部存储目录：storage/emulated/0/android/data/包名/files
        try {
            //打开文件输出流
            File file = new File(getExternalFilesDir(""), fileName);//path+fileName
            FileOutputStream out = new FileOutputStream(file);
            //创建BufferedWriter对象
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            //写入数据
            writer.write(username);
            //关闭输出流
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取
    private String readPrivateExStorage(String fileName) {
        String data = null;
        try {
            //打开文件输出流
            File file = new File(getExternalFilesDir(""), fileName);//path+fileName
            FileInputStream in = new FileInputStream(file);
            //创建BufferedWriter对象
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //读出数据
            data = reader.readLine();
            //关闭输入流
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 删除外部私有存储的文件
    private void deletePrivateExStorage(String fileName) {
        File file = new File(getExternalFilesDir(""), fileName);
        if (file.isFile()) {
            if (file.delete()) {
                Toast.makeText(this, "删除外部公有文件成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "删除外部公有文件失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
