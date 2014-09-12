package com.silas.guiaes.io;

/**
 * Created by silas on 08/09/14.
 */
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.util.Log;

import com.silas.guiaes.interfaces.TarefaConcluidaListener;

import java.util.Map;

public class DocumentDownloadTask extends AsyncTask<String, Void, Document>{
    TarefaConcluidaListener tcl;
    private static final String TAG = "DocumentDownloadTask";
    private static Map<String,String> cookies;
    @Override
    protected Document doInBackground(String... url) {

        try {
            //System.setProperty("http.keepAlive", "false");
            Document doc = null;
            Connection.Response res;
            Connection con = Jsoup.connect(url[0])
                    .timeout(10000)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");
            if (cookies == null) {
                res =  con.execute();
                cookies = res.cookies();
            }
            else {
                res = con.cookies(cookies).execute();
            }
            // obtem o html do endereço


            Log.d(TAG, "Abrindo " + url[0]);
            doc = res.parse();
            return doc;
        } catch (Exception e) {
            Log.e(TAG, "Erro a abrir: " + url[0] + " " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document html) {
        if (html != null) {
            tcl.quandoTarefaConcluida(html);
        }
    }

}
