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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by silas on 22/06/15.
 */
public class ProcessaMeditacoesTask extends
        AsyncTask<HashMap<Integer, String>, Void, HashMap<Integer, String>> {
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

    @SafeVarargs
    @Override
    protected final HashMap<Integer, String> doInBackground(HashMap<Integer, String>... tmp) {
        HashMap<Integer, String> urls = tmp[0];
        ArrayList<Meditacao> dias;
        HashMap<Integer, String> status = new HashMap<>();
        Extractable extrator;

        for (Map.Entry<Integer, String> url : urls.entrySet()) {
            String html = Util.getHTML(url.getValue());
            extrator = new ColonialExtractable(html);
            dias = extrator.extraiMeditacao(url.getKey());
            if (dias.size() == 0) {
                switch (url.getKey()) {
                    case Meditacao.ADULTO:
                        status.put(Meditacao.ADULTO, "Meditação dos Adultos indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.MULHER:
                        status.put(Meditacao.MULHER, "Meditação das Mulheres indisponível" +
                                " \n tente mais tarde!");
                        break;
                    case Meditacao.JUVENIL:
                        status.put(Meditacao.JUVENIL, "Inspiração Juvenil indisponível" +
                                " \n tente mais tarde!");
                        break;
                }
            } else {
                mdba.addMeditacoes(dias);
            }

        }
        return status;
    }

    @Override
    protected void onPostExecute(HashMap<Integer, String> status) {
        progress.dismiss();

//      Atualiza a tab com o conteúdo baixado
        mCallback.onUpdate(Calendar.getInstance(), mesAnterior);

        for (Map.Entry<Integer, String> message : status.entrySet()) {
            Toast.makeText(mContext, message.getValue(), Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(status);
    }
}