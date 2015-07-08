package com.silas.meditacao.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.fragments.DiaMeditacaoFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TestActivity extends AppCompatActivity implements DiaMeditacaoFragment.Updatable {
    public static final String DIA = "dia";
    private Calendar dia = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        if (savedInstanceState == null) {
            updateFragment();
        }
    }

    public void updateFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        String sDia = sdf.format(dia.getTime());
        args.putString(DIA, sDia);
        DiaMeditacaoFragment fragment = new DiaMeditacaoFragment();
        fragment.setArguments(args);
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onUpdate(Calendar c) {
        dia = c;
        updateFragment();
    }
}
