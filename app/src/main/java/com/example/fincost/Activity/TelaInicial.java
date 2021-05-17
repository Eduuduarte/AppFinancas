package com.example.fincost.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.fincost.R;

public class TelaInicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        Thread logoTimer = new Thread(){
            public void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e){
                    Log.d("Exception", "Exception" + e);
                } finally {
                    Intent intent = new Intent(TelaInicial.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }

        };

        logoTimer.start();
    }
}