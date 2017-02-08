package com.silas.meditacao.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silas.meditacao.fragments.ContentFragment;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    public List<ContentFragment> getContentFragmentList() {
        return mList;
    }

    private List<ContentFragment> mList = new ArrayList<>();
    private int [] tipos = {Meditacao.ADULTO, Meditacao.MULHER,
        Meditacao.JUVENIL, Meditacao.ABJANELAS};
    private Calendar dia;

    public TabAdapter(FragmentManager fm, Calendar dia) {
        super(fm);
        this.dia = dia;
        initFragments();
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return tipos.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Meditacao.getNomeTipo(position + 1);
    }

    public void updateFragments(Calendar dia) {
        this.dia = dia;
        for (ContentFragment fragment: mList) {
            fragment.onUpdate(dia, fragment.getTipo());
        }
    }

    private void initFragments() {
        if (mList.size() == 0) {
            for (int tipo:tipos ) {
                mList.add(ContentFragment.newInstance(dia, tipo));
            }
        }
    }
}
