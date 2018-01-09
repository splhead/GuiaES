package com.silas.meditacao.io;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.models.Meditacao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class ProcessaMeditacoesTask extends
        AsyncTask<Integer, Integer, ArrayList<String>> {
    private ProgressDialog progress;
    //    private ProgressBar progressBar;
    private WeakReference<MainActivity> wr;
    private MeditacaoDBAdapter mdba;
    private Calendar dia;

    public ProcessaMeditacoesTask(MainActivity activity, Calendar dia) {
        wr = new WeakReference<>(activity);
        this.dia = dia;
        mdba = new MeditacaoDBAdapter(wr.get());

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        progressBar = (ProgressBar) wr.get().getProgressBar();
//        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setIndeterminate(true);
        progress = new ProgressDialog(wr.get());
//        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setTitle("Recebendo poder!");
        progress.setMessage("Ore pelo meu criador e aguarde...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
//        progressBar.setProgress(values[0]);
        progress.setProgress(values[0]);
    }

    @Override
    protected final ArrayList<String> doInBackground(Integer... tipos) {
        ArrayList<Meditacao> dias = null;
        ArrayList<String> status = new ArrayList<>();
        Extractable extrator;
        int counter = 0;
//        int tipo = tipos[0];

        for (int tipo : tipos) {

            counter++;
            extrator = howToGet(tipo, Util.getURL(tipo));
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
//                publishProgress((counter * 100) / tipos.length);

        }


        return status;
    }

    private Extractable howToGet(int type, String url) {
        switch (type) {
            case Meditacao.ADULTO:
            case Meditacao.MULHER:
            case Meditacao.JUVENIL:
                return new CPBExtractable(wr.get(), type);
            case Meditacao.ABJANELAS:
                String content = Util.getContent(url);
                return new JsonExtractable(content);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> messages) {
        progress.dismiss();
//        progressBar.setVisibility(View.GONE);

//      Atualiza
        if (messages.isEmpty()) {
            wr.get().initMeditacoes();
        } else {
            for (String status : messages)
                Snackbar.make(wr.get().getCoordnatorLayout(), status, Snackbar.LENGTH_SHORT).show();
        }
        super.onPostExecute(messages);
    }
}