package com.silas.meditacao.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.fragments.ContentFragment;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

    private List<ContentFragment> mList = new ArrayList<>();

    private ArrayList<Meditacao> meditacoes = new ArrayList<>();

    /*public TabAdapter(FragmentManager fm, ArrayList<Meditacao> colecao) {
        super(fm);
        meditacoes = colecao;
    }*/

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        return ContentFragment.newInstance(meditacoes.get(position));
        return ContentFragment.newInstance();
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public int getCount() {
        return MainActivity.TYPES.length;
    }
//    public int getCount() {
//        return meditacoes != null ? meditacoes.size() : 0;
//    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Meditacao.getNomeTipo(MainActivity.TYPES[position]);
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof ContentFragment) {
            ContentFragment fragment = (ContentFragment) object;
            mList.add(fragment);
        }
        return object;
    }

    private void updateFragments() {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).update(meditacoes.get(i));
        }
    }

    public void setMeditacoes(ArrayList<Meditacao> meditacoes) {
        this.meditacoes.clear();
        this.meditacoes = meditacoes;
        updateFragments();
    }

    public Meditacao getMeditacao(int position) {
        return meditacoes.get(position);
    }
}
