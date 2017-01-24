package com.silas.meditacao.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.interfaces.Updateable;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by silas on 22/06/15.
 */
public class ProcessaMeditacoesTask extends
        AsyncTask<Integer, Void, String> {
    private Context mContext;
    private ProgressDialog progress;
    //    private DiaMeditacaoFragment.Updatable mCallback;
//    private int mesAnterior = 99;
    private Updateable mCallback;
    private MeditacaoDBAdapter mdba;
    private int tipo;
    private Calendar dia;

    public ProcessaMeditacoesTask(Context context, Updateable mCallback, Calendar dia) {
        mContext = context;
        this.mCallback = mCallback;
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
    protected final String doInBackground(Integer... tipos) {
        ArrayList<Meditacao> dias = null;
        String status = "";
        Extractable extrator;
        tipo = tipos[0];

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
                    status = "Meditação dos Adultos indisponível" +
                            " \n tente mais tarde!";
                    break;
                case Meditacao.MULHER:
                    status = "Meditação das Mulheres indisponível" +
                            " \n tente mais tarde!";
                    break;
                case Meditacao.JUVENIL:
                    status = "Inspiração Juvenil indisponível" +
                            " \n tente mais tarde!";
                    break;
                case Meditacao.ABJANELAS:
                    status = "Janelas para vida indisponível" +
                            " \n tente mais tarde!";
                    break;
            }
        } else {
            mdba.addMeditacoes(dias);
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
    protected void onPostExecute(String status) {
        progress.dismiss();

//      Atualiza o fragment com o conteúdo baixado
        if (status.isEmpty()) {
            mCallback.onUpdate(Calendar.getInstance(), tipo);
        } else {
            Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(status);
    }
}