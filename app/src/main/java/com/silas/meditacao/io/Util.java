package com.silas.meditacao.io;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.silas.meditacao.activity.MainActivity;
import com.silas.meditacao.models.Meditacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {

    public static Boolean internetDisponivel(Context con) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
//                Log.i("TestaInternet", "Está conectado.");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(con, "OPS! Parece que você está sem gravata (digo, INTERNET)" +
                        " assim o poder (a meditação) não desce! ;)",
                Toast.LENGTH_LONG).show();
//        Log.i("TestaInternet", "Não está conectado.");
        return false;
    }

    static String getURL(int type) {
        String url = "http://iasdcolonial.org.br/index.php/"; //

        switch (type) {
            case Meditacao.ADULTO:
                //adultos
                return url + "meditacao-diaria/mensal";
            case Meditacao.MULHER:
                //mulher
                return url + "meditacao-da-mulher/mensal";
            case Meditacao.JUVENIL:
                //juvenil
                return url + "inspiracao-juvenil/mensal";

            case Meditacao.ABJANELAS:
                //janelas
                return "https://gist.githubusercontent.com/splhead/c5e0e611a917bc6f2df9a7c2493a576f/raw/e6f4a51ea5d87583e9fcca1f2a26cb54b969ee02/med_janelas_para_vida.json";
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

            if (Build.VERSION.SDK_INT > 13) {
                connection.setRequestProperty("Connection", "close");
            }

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

        } catch (IOException e) {
            e.printStackTrace();
            /*if (tentativa < 3) {
                tentativa++;
                Log.d("tentativa", String.valueOf(tentativa));
                getContent(con, address, tentativa);
            }
            Toast.makeText(con, "Indisponível, tente novamente mais tarde!", Toast.LENGTH_SHORT)
                    .show();*/
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
