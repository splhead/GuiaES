package com.silas.meditacao.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by silas on 07/07/15.
 */
public class DiaMeditacaoFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTablayout;
    private List<Meditacao> mTabs = new ArrayList<>();
    private MeditacaoDBAdapter mdba;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdba = new MeditacaoDBAdapter(getActivity());
        Calendar dia = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDia = sdf.format(dia);

        try {
            Meditacao mAdulto = mdba.buscaMeditacao(sDia, Meditacao.ADULTO);
            Meditacao mMulher = mdba.buscaMeditacao(sDia, Meditacao.MULHER);
            Meditacao mJuvenil = mdba.buscaMeditacao(sDia, Meditacao.JUVENIL);

            mTabs.add(mAdulto);
            mTabs.add(mMulher);
            mTabs.add(mJuvenil);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sampler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        // TODO: 07/07/15 construir o adapter

        mTablayout = (TabLayout) view.findViewById(R.id.tablayout);
        mTablayout.addTab(mTablayout.newTab().setText("Adulto"));
        mTablayout.addTab(mTablayout.newTab().setText("Mulher"));
        mTablayout.addTab(mTablayout.newTab().setText("Juvenil"));


    }
}
