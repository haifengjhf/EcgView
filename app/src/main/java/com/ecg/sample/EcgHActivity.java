package com.ecg.sample;

import com.clj.ecgview.EcgHorizontalView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Thread.sleep;

public class EcgHActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecgh);

        EcgHorizontalView ecgView = findViewById(R.id.ecg_view);

        new Thread(()->{
            while (true){
                List<Integer> list = new ArrayList<>();
                Random random = new Random();
                list.add(0);
                for (int i = 0; i < 3; i++) {
                    list.add(random.nextInt(30));
                }
                EcgHActivity.this.runOnUiThread(()->{
                    ecgView.appendData(list);
                });

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
