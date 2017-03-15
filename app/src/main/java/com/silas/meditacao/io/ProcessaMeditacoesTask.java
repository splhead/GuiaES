package com.silas.meditacao.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.models.Meditacao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class ProcessaMeditacoesTask extends
        AsyncTask<Integer, Void, ArrayList<String>> {
    private Context mContext;
    private ProgressDialog progress;
    private WeakReference<MainActivity> wr;
    private MeditacaoDBAdapter mdba;
    private Calendar dia;

    public ProcessaMeditacoesTask(Context context, MainActivity activity, Calendar dia) {
        mContext = context;
        wr = new WeakReference<>(activity);
        this.dia = dia;
        mdba = new MeditacaoDBAdapter(mContext);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(mContext);
        progress.setIndeterminate(true);
        progress.setTitle("Recebendo poder!");
        progress.setMessage("Ore pelo meu criador e aguarde...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected final ArrayList<String> doInBackground(Integer... tipos) {
        ArrayList<Meditacao> dias = null;
        ArrayList<String> status = new ArrayList<>();
        Extractable extrator;
//        int tipo = tipos[0];

        for (int tipo : tipos) {
            extrator = howToGet(tipo, Util.getURL(tipo));
            try {
                if (extrator != null) {
                    dias = extrator.extraiMeditacao(dia, tipo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dias == null || dias.size() == 0) {
                switch (tipo) {
                    case Meditacao.ADULTO:
                        status.add("Meditação dos Adultos indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.MULHER:
                        status.add("Meditação das Mulheres indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.JUVENIL:
                        status.add("Inspiração Juvenil indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.ABJANELAS:
                        status.add("Janelas para vida indisponível" +
                                " \n tente mais tarde!");
                        break;
                }
            } else {
                mdba.addMeditacoes(dias);
            }
        }


        return status;
    }

    private Extractable howToGet(int type, String url) {
        String content = Util.getContent(url);

        switch (type) {
            case Meditacao.ADULTO:
            case Meditacao.MULHER:
            case Meditacao.JUVENIL:
                return new ColonialExtractable(content);
            case Meditacao.ABJANELAS:
                return new JsonExtractable(content);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> messages) {
        progress.dismiss();

//      Atualiza
        if (messages.isEmpty()) {
            wr.get().initMeditacoes();
        } else {
            for (String status : messages) {
                Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
            }
        }
        super.onPostExecute(messages);
    }
}