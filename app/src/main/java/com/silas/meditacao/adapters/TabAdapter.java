package com.silas.meditacao.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.silas.meditacao.fragments.ContentFragment;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

    private List<ContentFragment> mList = new ArrayList<>();

    private ArrayList<Meditacao> meditacoes;

    public TabAdapter(FragmentManager fm, ArrayList<Meditacao> colecao) {
        super(fm);
        meditacoes = colecao;
    }

    public List<ContentFragment> getContentFragmentList() {
        return mList;
    }

    @Override
    public Fragment getItem(int position) {
        return ContentFragment.newInstance(meditacoes.get(position));
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public int getCount() {
        return meditacoes.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Meditacao.getNomeTipo(meditacoes.get(position).getTipo());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof ContentFragment) {
            ContentFragment fragment = (ContentFragment) object;
            mList.add(fragment);
        }
        return object;
    }

    public void updateFragments() {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).update(meditacoes.get(i));
        }
    }
}
