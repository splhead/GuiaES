package com.silas.meditacao.io;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.models.Meditacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {

    public static Boolean internetDisponivel(MainActivity activity) {
        WeakReference<MainActivity> wr = new WeakReference<>(activity);
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) wr.get()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo internet;
            if (connectivityManager != null) {
                internet = connectivityManager
                        .getActiveNetworkInfo();
                if (internet.isConnected()) {
//                Log.i("TestaInternet", "Está conectado.");
                    return true;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        Snackbar.make(wr.get().getCoordnatorLayout(), "Ops! Sem gravata (digo, INTERNET)" +
                        " o poder (a meditação) não desce! ;)",
                Snackbar.LENGTH_LONG).show();
//        Log.i("TestaInternet", "Não está conectado.");
        return false;
    }

    static String getURL(int type) {

        switch (type) {
            case Meditacao.ADULTO:
                //adultos
                return "https://gist.githubusercontent.com/anonymous/a91302485781c31b840f93b055266b0f/raw/16d082773d6122aadc32b63301a2c091046fc918/a_caminho_do_lar.json";
            case Meditacao.MULHER:
                //mulher
                return "https://gist.githubusercontent.com/anonymous/2e25e4eca84fae894d0c10e0cc4e86ae/raw/df61d4a63b5756fbb3441b1a5431304c100977f6/vivendo_seu_amor.json";
            case Meditacao.JUVENIL:
                //juvenil
                return "https://gist.githubusercontent.com/anonymous/00b246e71aa73375621547219fd3aecf/raw/cd80726fb8c14ce7f29a2e3c55a3a944d99a87c2/siga_o_mestre.json";

            case Meditacao.ABJANELAS:
                return "https://gist.githubusercontent.com/anonymous/386f9d8d5ea404c14d6da332e7a4a744/raw/5b2a5f10eaa84e54e5da7029ce121ed1717a5fe5/janelas_para_a_vida.json";
        }
        return "";
    }

    static String getContent(String address) {
        URL url;
        BufferedReader reader = null;
        StringBuffer stringBuffer;

        try {

            // create the HttpURLConnection
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 30 seconds to respond
            connection.setReadTimeout(30 * 1000);

//            if (Build.VERSION.SDK_INT > 13) {
                connection.setRequestProperty("Connection", "close");
//            }

            connection.connect();

            // read the output from the server
            InputStream is = connection.getInputStream();

            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is));
                stringBuffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
//                Log.d("Util", stringBuffer.toString());
                return stringBuffer.toString();
            }

        } catch (ConnectException ce) {
            Log.d("getContent", "Sem Internet!");
            ce.printStackTrace();
        } catch (IOException e) {
            Log.d("getContent", "erro ao baixar o conteúdo!");
            e.printStackTrace();
        } finally {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return "";
    }

    public static void restart(Activity activity) {
        /*Intent intent = activity.getIntent();
        activity.finish();
        Activity nActivity = new Activity();
        nActivity.startActivity(intent);*/
        TaskStackBuilder.create(activity)
                .addNextIntent(new Intent(activity, MainActivity.class))
                .addNextIntent(activity.getIntent())
                .startActivities();
    }
}
