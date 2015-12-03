package com.silas.meditacao.io;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.silas.meditacao.models.Meditacao;

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

    public static HashMap<Integer, String> getURLs() {
        String url = "http://iasdcolonial.org.br/index.php/"; //
        HashMap<Integer, String> urls = new HashMap<>();
        //adultos
        urls.put(Meditacao.ADULTO, url + "meditacao-diaria/mensal/pc");
        //mulher
        urls.put(Meditacao.MULHER, url + "meditacao-da-mulher/mensal/pc");
        //juvenil
        urls.put(Meditacao.JUVENIL, url + "inspiracao-juvenil/mensal/pc");

        return urls;
    }
}
