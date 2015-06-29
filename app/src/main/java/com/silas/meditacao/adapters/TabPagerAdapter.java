package com.silas.meditacao.adapters;

/**
 * Created by splhead on 01/06/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.silas.meditacao.fragments.SwipeTabFragment;
import com.silas.meditacao.models.Meditacao;
/*import java.util.HashMap;
import java.util.Map;*/

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private String dia;
    /*private FragmentManager mFragmentManager;
    private Map<Integer,String> mFragments = new HashMap<>();*/

    public TabPagerAdapter(FragmentManager fm, String data) {
        super(fm);
        dia = data;
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
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
        bundle.putString(SwipeTabFragment.DATA, dia);
        bundle.putInt(SwipeTabFragment.TIPO, tipo);
        SwipeTabFragment swipeTabFragment = new SwipeTabFragment();
        swipeTabFragment.setArguments(bundle);
        return swipeTabFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    /* @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if(obj instanceof Fragment) {
            Fragment fragment = (Fragment)obj;
            mFragments.put(position,fragment.getTag());
        }
        return obj;
    }

    public Fragment getFragment(int postition) {
        String tag = mFragments.get(postition);
        if(tag == null) {
            return null;
        }
        return mFragmentManager.findFragmentByTag(tag);
    }*/
}