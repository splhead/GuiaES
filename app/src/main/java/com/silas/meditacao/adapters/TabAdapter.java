package com.silas.meditacao.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.fragments.ContentFragment;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;

public class TabAdapter extends FragmentPagerAdapter {

    private SparseArray<String> mFragmentTagList;
    private FragmentManager mFragmentManager;
    private ArrayList<Meditacao> devotionals;

    public TabAdapter(FragmentManager fm, ArrayList<Meditacao> colecao) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTagList = new SparseArray<>();
        devotionals = new ArrayList<>();
        devotionals = colecao;
    }

    public TabAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTagList = new SparseArray<>();
        devotionals = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        if (devotionals.size() > position) {
            return ContentFragment.newInstance(devotionals.get(position));
        }
        return ContentFragment.newInstance(position + 1);
    }

    public void setAdIsLoaded() {
        for (int i = 0; i < getCount(); i++) {
            ContentFragment contentFragment = getFragment(i);
            if (contentFragment != null) {
                contentFragment.setAdIsLoaded(true);
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
            ContentFragment contentFragment = getFragment(i);
            if (contentFragment != null && devotionals.get(i) != null) {
                contentFragment.update(devotionals.get(i));
            }
        }
    }

    public void setDevotionals(ArrayList<Meditacao> devotionals) {
        this.devotionals = devotionals;
        updateFragments();
    }

    public Meditacao getMeditacao(int position) {
        if (position < mFragmentTagList.size()) {
            ContentFragment contentFragment = getFragment(position);
            if (contentFragment != null) return contentFragment.getMeditacao();
        }
        return new Meditacao("", "", "", "", position + 1);
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof ContentFragment) {
            ContentFragment fragment = (ContentFragment) obj;
            String tag = fragment.getTag();
            mFragmentTagList.put(position, tag);
        }
        return obj;
    }

    private ContentFragment getFragment(int position) {
        String tag = mFragmentTagList.get(position);
        if (tag != null && mFragmentManager != null) {
            return (ContentFragment) mFragmentManager.findFragmentByTag(tag);
        }
        return null;
    }
}
