package com.silas.meditacao.classes;

/**
 * Created by splhead on 01/06/15.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silas.meditacao.fragments.SwipeTabFragment;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TabPagerAdapter extends FragmentPagerAdapter {
    private Calendar ca = Calendar.getInstance();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data = sdf.format(ca.getTime());
        int tipo = 0;

        switch (index+1) {
            case Meditacao.ADULTO:
                tipo = Meditacao.ADULTO;
                break;
            case Meditacao.MULHER:
                tipo = Meditacao.MULHER;
                break;
            case Meditacao.JUVENIL:
                tipo = Meditacao.JUVENIL;
                break;
        }
        bundle.putString(SwipeTabFragment.DATA,data);
        bundle.putInt(SwipeTabFragment.TIPO, tipo);
        SwipeTabFragment swipeTabFragment = new SwipeTabFragment();
        swipeTabFragment.setArguments(bundle);
        return swipeTabFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}