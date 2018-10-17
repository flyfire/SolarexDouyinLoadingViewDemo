package com.solarexsoft.solarexdouyinloadingviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.solarexsoft.solarexdouyinloadingview.SolarexDouyinLoadingView;

public class MainActivity extends AppCompatActivity {
    SolarexDouyinLoadingView mSolarexDouyinLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSolarexDouyinLoadingView = findViewById(R.id.sdlv_loading);
        mSolarexDouyinLoadingView.start();
    }
}
