package com.ecg.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.clj.ecgview.EcgView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EcgView ecgView = findViewById(R.id.ecg_view);

        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4800; i++) {
            list.add(random.nextInt(30));
        }
        ecgView.setDataList(list);


        findViewById(R.id.ecgh_view).setOnClickListener((View view)->{
            MainActivity.this.startActivity(new Intent(MainActivity.this,EcgHActivity.class));
        });
    }
}
