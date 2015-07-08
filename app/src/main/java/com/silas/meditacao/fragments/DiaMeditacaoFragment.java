package com.silas.meditacao.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.activity.AboutActivity;
import com.silas.meditacao.activity.TestActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.io.ProcessaMeditacoesTask;
import com.silas.meditacao.io.Util;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DiaMeditacaoFragment extends Fragment implements Toolbar.OnMenuItemClickListener,
        DatePickerDialog.OnDateSetListener {

    private List<Meditacao> mTabs = new ArrayList<>();
    private Calendar dia = Calendar.getInstance();
    private ViewPager mViewPager;
    private Updatable mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {

            MeditacaoDBAdapter mdba = new MeditacaoDBAdapter(getActivity());

            String sDia = args.getString(TestActivity.DIA);
            Meditacao mAdulto, mMulher, mJuvenil;

            try {
                mAdulto = mdba.buscaMeditacao(sDia, Meditacao.ADULTO);
                mMulher = mdba.buscaMeditacao(sDia, Meditacao.MULHER);
                mJuvenil = mdba.buscaMeditacao(sDia, Meditacao.JUVENIL);

                if (mAdulto == null || mMulher == null || mJuvenil == null) {
                    // TODO: 07/07/15 Corrigir para não baixar no futuro ne 
                    if (Util.internetDisponivel(getActivity())) {
                        new ProcessaMeditacoesTask(getActivity(), mCallback).execute(Util.getURLs());
                    }
                }

                mTabs.add(mAdulto);
                mTabs.add(mMulher);
                mTabs.add(mJuvenil);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sampler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.inflateMenu(R.menu.main);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);

        TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
        tabAdapter.setList(mTabs);
        mViewPager.setAdapter(tabAdapter);

        TabLayout mTablayout = (TabLayout) view.findViewById(R.id.tablayout);

        mTablayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        Log.d("onMenuItemClick frag", item.toString());
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                Intent i = new Intent(getActivity(), AboutActivity.class);
                startActivity(i);
                break;
            case R.id.action_date:
                DatePickerDialog mDateDialog = new DatePickerDialog(getActivity(), this, dia.get(Calendar.YEAR),
                        dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH));
                mDateDialog.setTitle("Qual dia?");
                mDateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", mDateDialog);
                mDateDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Escolher", mDateDialog);
                mDateDialog.show();
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, preparaCompartilhamento());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                break;

        }
        return false;
    }

    private String preparaCompartilhamento() {
        Meditacao meditacao = mTabs.get(mViewPager.getCurrentItem());

        if (meditacao == null) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        switch (meditacao.getTipo()) {
            case Meditacao.ADULTO:
                out.append("Meditação Matinal\n\n");
                break;
            case Meditacao.MULHER:
                out.append("Meditação da Mulher\n\n");
                break;
            case Meditacao.JUVENIL:
                out.append("Inspiração Juvenil\n\n");
                break;
        }

        out.append(meditacao.getTitulo());
        out.append("\n\n");
        out.append(meditacao.getTextoBiblico());
        out.append("\n\n");
        out.append(meditacao.getDataPorExtenso());
        out.append("\n\n");
        out.append(meditacao.getTexto());

        return out.toString();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dia.set(year, month, day);
        mCallback.onUpdate(dia);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (Updatable) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement Updatable");

        }
    }

    public interface Updatable {
        void onUpdate(Calendar c);
    }
}
