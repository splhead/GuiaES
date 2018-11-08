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

    private ArrayList<Meditacao> meditacoes = new ArrayList<>();

    public TabAdapter(FragmentManager fm, ArrayList<Meditacao> colecao) {
        super(fm);
        meditacoes = colecao;
    }

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ContentFragment contentFragment;
        if (meditacoes.size() > 0) {
            contentFragment = ContentFragment.newInstance(meditacoes.get(position));
        } else {
            contentFragment = ContentFragment.newInstance();
        }
        mList[position] = contentFragment;
        return contentFragment;
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
        for (int i = 0; i < getCount(); i++) {
            if (mList[i] != null && meditacoes.get(i) != null) {
                mList[i].update(meditacoes.get(i));
            }
        }
    }

    public void setMeditacoes(ArrayList<Meditacao> meditacoes) {
        this.meditacoes.clear();
        this.meditacoes = meditacoes;
        updateFragments();
    }

    public Meditacao getMeditacao(int position) {
        if (position < meditacoes.size())
            return meditacoes.get(position);
        return null;
    }
}
