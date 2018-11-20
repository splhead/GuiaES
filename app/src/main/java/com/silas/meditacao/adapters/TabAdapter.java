package com.silas.meditacao.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.fragments.ContentFragment;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;

public class TabAdapter extends FragmentPagerAdapter {

    private ContentFragment[] mList = new ContentFragment[getCount()];

    private ArrayList<Meditacao> devotionals = new ArrayList<>();

    public TabAdapter(FragmentManager fm, ArrayList<Meditacao> colecao) {
        super(fm);
        devotionals = colecao;
    }

    public TabAdapter(FragmentManager fm) {
        super(fm);
        for (int type : MainActivity.TYPES) {
            devotionals.add(new Meditacao("", "", "", "",
                    MainActivity.TYPES[type]));
        }
    }

    @Override
    public Fragment getItem(int position) {
        ContentFragment contentFragment = ContentFragment
                .newInstance(devotionals.get(position));

        mList[position] = contentFragment;
        return contentFragment;
    }

    public void setAdIsLoaded() {
        for (int i = 0; i < getCount(); i++) {
            if (mList[i] != null) {
                mList[i].setAdIsLoaded(true);
            }
        }
    }

    @Override
    public int getCount() {
        return MainActivity.TYPES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Meditacao.getNomeTipo(MainActivity.TYPES[position]);
    }

    private void updateFragments() {
        for (int i = 0; i < devotionals.size(); i++) {
            if (mList[i] != null && devotionals.get(i) != null) {
                mList[i].update(devotionals.get(i));
            }
        }
    }

    public void setDevotionals(ArrayList<Meditacao> devotionals) {
        this.devotionals = devotionals;
        updateFragments();
    }

    public Meditacao getMeditacao(int position) {
        return devotionals.get(position);
    }
}
