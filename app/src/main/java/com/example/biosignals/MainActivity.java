package com.example.biosignals;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int duration = 2;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int time = duration * 1000;

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Thread timerThread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(time); //2000
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                } finally
                {
                    if(flag){
                        Intent intent = new Intent(MainActivity.this, Patients.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        };
        timerThread.start();
    }

    //==============================================================================================

    @Override
    public void onBackPressed() {
        flag = false;
        finish();
    }

}