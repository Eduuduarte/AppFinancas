package com.example.fincost.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.fincost.Adapter.TabAdapter;
import com.example.fincost.R;
import com.example.fincost.tablayout.SlidingTabLayout;

public class InvestimentosActivity extends AppCompatActivity {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Toolbar toolbarInvestimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investimentos);

        slidingTabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.vp_pagina);
        toolbarInvestimentos = findViewById(R.id.toolbarInvestimentos);


        //Configurando a toolbarInvestimentos
        toolbarInvestimentos.setTitle("Investimentos");
        toolbarInvestimentos.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbarInvestimentos);

        //Configurar sliding Tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));



        //Configurar Adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setViewPager(viewPager);


    }
}