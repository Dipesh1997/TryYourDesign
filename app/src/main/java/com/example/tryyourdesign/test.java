package com.example.tryyourdesign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.tryyourdesign.ui.activities.SplashActivity;

public class test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
    public void onMainActivity(View view)
    {
        Intent intent = new Intent(test.this, MainActivity.class);
        startActivity(intent);
    }
    public void onPaint(View view)
    {
        Intent intent = new Intent(test.this, PaintACT.class);
        startActivity(intent);
    }
    public void onPaintActivity(View view)
    {
        Intent intent = new Intent(test.this, PaintingActivity.class);
        startActivity(intent);
    }
    public void onPolygon(View view)
    {
        Intent intent = new Intent(test.this, Polygon.class);
        startActivity(intent);
    }
    public void onHNB(View view)
    {
        Intent intent = new Intent(test.this, HorizontalNtbActivity.class);
        startActivity(intent);
    }
    public void onMainActivity2(View view)
    {
        Intent intent = new Intent(test.this, MainActivity2.class);
        startActivity(intent);
    }
    public void onSplashActivity(View view)
    {
        Intent intent = new Intent(test.this, SplashActivity.class);
        startActivity(intent);
    }

    public void ontest2(View view)
    {
        Intent intent = new Intent(test.this, test2.class);
        startActivity(intent);
    }
}
