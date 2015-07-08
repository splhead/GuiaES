package com.silas.meditacao.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private List<Meditacao> mList = new ArrayList<>();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (mList.get(position) != null) {
            return mList.get(position).createFragment();
        }
        return new Meditacao("", "", "", "", position + 1).createFragment();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mList.get(position) != null) {
            return mList.get(position).getNomeTipo();
        }
        return "";
    }

    public void setList(List<Meditacao> l) {
        mList = l;
    }
}
