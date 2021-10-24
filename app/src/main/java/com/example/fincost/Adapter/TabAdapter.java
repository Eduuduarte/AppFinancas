package com.example.fincost.Adapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fincost.Fragment.InvestirFragment;
import com.example.fincost.Fragment.ResgatarFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    private String[] titulosAbas = {"INVESTIR", "RESGATAR"};

    public TabAdapter(@NonNull FragmentManager fm) {

        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new InvestirFragment();
                break;
            case 1:
                fragment = new ResgatarFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {

        return titulosAbas.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titulosAbas[position];
    }
}
