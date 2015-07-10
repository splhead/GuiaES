package com.silas.meditacao.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.fragments.DiaMeditacaoFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements DiaMeditacaoFragment.Updatable {
    public static final String DIA = "dia";
    public static final String MES_ANTERIOR = "mes_anterior";
    public static final String MES_ATUAL = "mes_atual";
    private Calendar dia = Calendar.getInstance();
    private int mesAnterior = dia.get(Calendar.MONTH);
    private int mesAtual = dia.get(Calendar.MONTH);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            updateFragment();
        }
    }

    public void updateFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        String sDia = sdf.format(dia.getTime());
        args.putString(DIA, sDia);
        args.putInt(MES_ANTERIOR, mesAnterior);
        args.putInt(MES_ATUAL, mesAtual);
        DiaMeditacaoFragment fragment = new DiaMeditacaoFragment();
        fragment.setArguments(args);
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onUpdate(Calendar c, int ma) {
        mesAtual = c.get(Calendar.MONTH);
        mesAnterior = ma;
        dia = c;
        updateFragment();
    }

}
