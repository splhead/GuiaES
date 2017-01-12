package com.silas.meditacao.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.fragments.DiaMeditacaoFragment;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by silas on 22/06/15.
 */
public class ProcessaMeditacoesTask extends
        AsyncTask<Integer, Void, ArrayList<String>> {
    private Context mContext;
    private ProgressDialog progress;
    private DiaMeditacaoFragment.Updatable mCallback;
    private int mesAnterior = 99;
    private MeditacaoDBAdapter mdba;

    public ProcessaMeditacoesTask(Context context, DiaMeditacaoFragment.Updatable cb, int ma) {
        mContext = context;
        mCallback = cb;
        mesAnterior = ma;
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

        for (Integer tipo : tipos) {
            extrator = howToGet(tipo, Util.getURL(tipo));
            try {
                dias = extrator.extraiMeditacao(tipo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dias == null) {
                switch (tipo) {
                    case Meditacao.ADULTO:
                        status.add(Meditacao.ADULTO, "Meditação dos Adultos indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.MULHER:
                        status.add(Meditacao.MULHER, "Meditação das Mulheres indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.JUVENIL:
                        status.add(Meditacao.JUVENIL, "Inspiração Juvenil indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.ABJANELAS:
                        status.add(Meditacao.ABJANELAS, "Janelas para vida indisponível" +
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
    protected void onPostExecute(ArrayList<String> status) {
        progress.dismiss();

//      Atualiza a tab com o conteúdo baixado
        if (status.size() <= 0) {
            mCallback.onUpdate(Calendar.getInstance(), mesAnterior);
        }

        for (String message : status) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(status);
    }
}