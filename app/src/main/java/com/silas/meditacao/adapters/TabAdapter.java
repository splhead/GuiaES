package com.silas.meditacao.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.silas.meditacao.interfaces.FragmentObserver;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TabAdapter extends FragmentStatePagerAdapter {

    private List<Meditacao> mList = new ArrayList<>();
    private Observable mObservers = new FragmentObserver();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = mList.get(position).createFragment();

        if (fragment instanceof Observer) {
            mObservers.addObserver((Observer) fragment);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getNomeTipo();
    }

    public void setList(List<Meditacao> l) {
        mList = l;
    }

    public void updateFragments() {
        mObservers.notifyObservers();
    }
}
