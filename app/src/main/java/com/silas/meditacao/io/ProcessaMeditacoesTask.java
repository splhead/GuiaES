package com.silas.meditacao.io;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.adapters.TabAdapter;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.models.Meditacao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class ProcessaMeditacoesTask extends
        AsyncTask<Integer, Integer, ArrayList<String>> {
    private WeakReference<MainActivity> wr;
    private MeditacaoDBAdapter mdba;
    private Calendar dia;
    private ArrayList<Meditacao> meditacoes = new ArrayList<>();
    private boolean setToDefaultTab = false;

    public ProcessaMeditacoesTask(MainActivity activity, Calendar dia) {
        wr = new WeakReference<>(activity);
        this.dia = dia;
        mdba = new MeditacaoDBAdapter(wr.get());
    }

    public ProcessaMeditacoesTask(MainActivity activity, Calendar dia, boolean moveToDefaultTab) {
        wr = new WeakReference<>(activity);
        this.dia = dia;
        mdba = new MeditacaoDBAdapter(wr.get());
        setToDefaultTab = moveToDefaultTab;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        wr.get().showProgressDialog();

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        wr.get().getProgressDialog().setProgress(values[0]);
    }

    @Override
    protected final ArrayList<String> doInBackground(Integer... tipos) {
        ArrayList<String> status = new ArrayList<>();
        int counter = 0;

        for (int tipo : tipos) {

            counter++;

            Meditacao meditacao = mdba.buscaMeditacao(dia, tipo);

            if (meditacao == null && Util.internetDisponivel(wr.get())) {
                // do download
                status = doExtraction(tipo);
                // search again after download it
                meditacao = mdba.buscaMeditacao(dia, tipo);
            }

            if (meditacao != null) {
                meditacoes.add(meditacao);
            }

            publishProgress((counter * 100) / tipos.length);
        }

        return status;
    }

    @NonNull
    private ArrayList<String> doExtraction(int tipo) {
        ArrayList<Meditacao> dias = null;
        ArrayList<String> status = new ArrayList<>();
        Extractable extrator;

        extrator = howToGet(tipo, Util.getURL(tipo, dia));
        try {
            if (extrator != null) {
                dias = extrator.extractDevotional();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dias == null || dias.size() == 0) {
            status.add(Meditacao.getDevotionalName(tipo)
                    + " indisponível \n tente mais tarde!");
        } else {
            mdba.addMeditacoes(dias);
        }


        return status;
    }

    private Extractable howToGet(int type, String url) {
        switch (type) {
            case Meditacao.ADULTO:
            case Meditacao.MULHER:
            case Meditacao.JUVENIL:
                /*if (dia.get(Calendar.YEAR) == 2018) {
                    return new CPBExtractable(wr.get(), type);
                }*/
            case Meditacao.ABJANELAS:
                String content = Util.getContent(url);
                return new JsonExtractable(content);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> messages) {
        MainActivity activity = wr.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.dismissProgressDialog();

//      Atualiza
        if (messages.isEmpty() && meditacoes.size() > 0) {

            TabAdapter tabAdapter = activity.getTabAdapter();
            if (tabAdapter != null) {
                tabAdapter.setDevotionals(meditacoes);
                activity.setupFABs();
                if (setToDefaultTab) {
                    activity.changeToTabDefault();
                }
            }

        } else {
            for (String status : messages)
                Snackbar.make(activity.getCoordnatorLayout(), status, Snackbar.LENGTH_SHORT).show();
        }
        super.onPostExecute(messages);
    }
}