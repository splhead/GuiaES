package com.silas.meditacao.io;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.silas.meditacao.models.Meditacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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

    public static HashMap<Integer, String> getURLs(int type) {
        String url = "http://iasdcolonial.org.br/index.php/"; //
        HashMap<Integer, String> urls = new HashMap<>();
        switch (type) {
            case Meditacao.ADULTO:
                //adultos
                urls.put(Meditacao.ADULTO, url + "meditacao-diaria/mensal");
                break;
            case Meditacao.MULHER:
                //mulher
                urls.put(Meditacao.MULHER, url + "meditacao-da-mulher/mensal");
                break;
            case Meditacao.JUVENIL:
                //juvenil
                urls.put(Meditacao.JUVENIL, url + "inspiracao-juvenil/mensal");
                break;
            default:
                //adultos
                urls.put(Meditacao.ADULTO, url + "meditacao-diaria/mensal");
                //mulher
                urls.put(Meditacao.MULHER, url + "meditacao-da-mulher/mensal");
                //juvenil
                urls.put(Meditacao.JUVENIL, url + "inspiracao-juvenil/mensal");
                break;
        }
        return urls;
    }

    public static String getHTML(String address) {
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
                return stringBuffer.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
            /*if (tentativa < 3) {
                tentativa++;
                Log.d("tentativa", String.valueOf(tentativa));
                getHTML(con, address, tentativa);
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
}
