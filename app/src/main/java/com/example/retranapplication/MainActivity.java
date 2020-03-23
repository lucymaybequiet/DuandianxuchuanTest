package com.example.retranapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements AManger.Iprogress,View.OnClickListener{
    private String path = "http://gdown.baidu.com/data/wisegame/91319a5a1dfae322/baidu_16785426.apk";
    private ProgressBar pBar;
    private AManger manger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manger = new AManger(path,this);
        initView();
    }
    private void initView(){
        Button start = findViewById(R.id.btn_restart);
        Button stop = findViewById(R.id.btn_pause);
        pBar = findViewById(R.id.pb_progress);
        pBar.setMax(100);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_restart:
                manger.start();
                break;
            case R.id.btn_pause:
                manger.stop();
                break;
        }
    }

    @Override
    public void onProgress(int progress) {
        pBar.setProgress(progress);
    }
}
