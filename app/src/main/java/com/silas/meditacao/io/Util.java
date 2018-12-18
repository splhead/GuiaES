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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    static Boolean internetDisponivel(MainActivity activity) {
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

    public static boolean notShabbat(Calendar hoje) {
        return !((hoje.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && hoje.get(Calendar.HOUR_OF_DAY) > 17) ||
                (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && hoje.get(Calendar.HOUR_OF_DAY) < 18));
    }

    public static String preparaCompartilhamento(Meditacao meditacao) {
        String sb = "Olhe que aplicativo bacana \"* Meditação Cristã Adventista *\"\n" +
                "https://play.google.com/store/apps/details?id=com.silas.guiaes.app";

        if (meditacao != null) {
            sb = Meditacao.getDevotionalName(meditacao.getTipo()) +
                    "\n\n*" +
                    meditacao.getTitulo() +
                    "*\n\n_" +
                    meditacao.getDataPorExtenso() +
                    "_\n\n*" +
                    meditacao.getTextoBiblico() +
                    "*\n\n" +
                    meditacao.getTexto();
        }

        return sb;
    }

    public static String prepareTextToSpeak(Meditacao meditacao) {

        if (meditacao == null) {
            return "Texto indisponível no momento. Por favor tente novamente mais tarde.";
        }

        String tmpOut = Meditacao.getDevotionalName(meditacao.getTipo()) + "... " +
                meditacao.getTitulo() + "... " +
                meditacao.getDataPorExtenso() + "... " +
                meditacao.getTextoBiblico() + "... " +
                meditacao.getTexto();

        String out = tmpOut.replace(":", " ").replace("\n", "");

        return changeAbbreviation(out);
    }

    private static String changeAbbreviation(String in) {
        Pattern pattern = Pattern.compile("[A-Za-z]{2}(?=\\s+\\d)");
        Matcher matcher = pattern.matcher(in);
        StringBuffer sb = new StringBuffer(in.length());
        Map<String, String> map = new HashMap<>();
        map.put("gn", "Gênisis");
        map.put("êx", "Êxodo");
        map.put("lv", "Levítico");
        map.put("nm", "Números");
        map.put("dt", "Deuteronômio");
        map.put("js", "Josué");
        map.put("jz", "Juízes");
        map.put("rt", "Rute");
        map.put("sm", "Samuel");
        map.put("rs", "Reis");
        map.put("cr", "Crônicas");
        map.put("ed", "Esdras");
        map.put("ne", "Neemias");
        map.put("et", "Ester");
        map.put("jó", "Jó");
        map.put("sl", "Salmos");
        map.put("pv", "Provérbios");
        map.put("ec", "Eclesiastes");
        map.put("ct", "Cantares");
        map.put("is", "Isaías");
        map.put("jr", "Jeremias");
        map.put("lm", "Lamentações de Jeremias");
        map.put("ez", "Ezequiel");
        map.put("dn", "Daniel");
        map.put("os", "Oséias");
        map.put("jl", "Joel");
        map.put("am", "Amós");
        map.put("ob", "Obadias");
        map.put("jn", "Jonas");
        map.put("mq", "Miquéias");
        map.put("na", "Naum");
        map.put("hc", "Habacuque");
        map.put("sf", "Sofonias");
        map.put("ag", "Ageu");
        map.put("zc", "Zacarias");
        map.put("ml", "Malaquias");
        map.put("mt", "Mateus");
        map.put("mc", "Marcos");
        map.put("lc", "Lucas");
        map.put("jo", "João");
        map.put("at", "Atos dos Apóstolos");
        map.put("rm", "Romanos");
        map.put("co", "Corintios");
        map.put("gl", "Gálatas");
        map.put("ef", "Efésios");
        map.put("fp", "Filipenses");
        map.put("cl", "Colossenses");
        map.put("ts", "Tessalonissenses");
        map.put("tm", "Timóteo");
        map.put("tt", "Tito");
        map.put("fm", "Filemon");
        map.put("hb", "Hebreus");
        map.put("tg", "Tiago");
        map.put("pe", "Pedro");
        map.put("jd", "Judas");
        map.put("ap", "Apocalipse");

        while (matcher.find()) {
            String abbreviation = matcher.group().toLowerCase();
            if (map.containsKey(abbreviation)) {
                abbreviation = map.get(abbreviation);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(abbreviation));
            }
//            Log.i("Encontrou", matcher.group());
        }

        matcher.appendTail(sb);
//        Log.i("Encontrou", sb.toString());
        return sb.toString();
    }

    static String getURL(int type, Calendar day) {

        int year = day.get(Calendar.YEAR);

        switch (type) {
            case Meditacao.ADULTO:
                //adultos
                if (year == 2017) {
                    return "https://gist.githubusercontent.com/anonymous/a91302485781c31b840f93b055266b0f/raw/16d082773d6122aadc32b63301a2c091046fc918/a_caminho_do_lar.json";
                } else if (year == 2018) {
                    return "https://gist.githubusercontent.com/anonymous/ad386a6c961d177e37394b458c9e9458/raw/4a3b41541373230d2c91d7809b43b4bf9af88ace/um_dia_inesquecivel_2018.json";
                } else {
                    return "http://chief-rodent.glitch.me/nossa_esperanca_2019.json";
                }
            case Meditacao.MULHER:
                //mulher
                if (year == 2017) {
                    return "https://gist.githubusercontent.com/anonymous/2e25e4eca84fae894d0c10e0cc4e86ae/raw/df61d4a63b5756fbb3441b1a5431304c100977f6/vivendo_seu_amor.json";
                } else if (year == 2018) {
                    return "https://gist.githubusercontent.com/anonymous/1435d6eadd5b15686ca5197cdd20c4e9/raw/8d0ec691fd0fc024f46cbc809e79e7b810b543d4/amor_eterno_2018.json";
                } else {
                    return "http://chief-rodent.glitch.me/toque_de_alegria_2019.json";
                }
            case Meditacao.JUVENIL:
                //juvenil
                if (year == 2017) {
                    return "https://gist.githubusercontent.com/anonymous/00b246e71aa73375621547219fd3aecf/raw/cd80726fb8c14ce7f29a2e3c55a3a944d99a87c2/siga_o_mestre.json";
                } else if (year == 2018) {
                    return "https://gist.githubusercontent.com/anonymous/2e076da9d6c8a22ef40fb32ea7269ee8/raw/c9426c37dea0b213f56bc519e0143ba088c36432/pense_bem_2018.json";
                } else {
                    return "http://chief-rodent.glitch.me/natureza_viva_2019.json";
                }
            case Meditacao.ABJANELAS:
                return "https://gist.githubusercontent.com/anonymous/386f9d8d5ea404c14d6da332e7a4a744/raw/5b2a5f10eaa84e54e5da7029ce121ed1717a5fe5/janelas_para_a_vida.json";

        }
        return "";
    }

    /*public static int getAdViewHeightInDP(Activity activity) {
        int adHeight;

        int screenHeightInDP = getScreenHeightInDP(activity);
        if (screenHeightInDP < 400)
            adHeight = 32;
        else if (screenHeightInDP <= 720)
            adHeight = 50;
        else
            adHeight = 90;

        return adHeight;
    }

    private static int getScreenHeightInDP(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        float screenHeightInDP = displayMetrics.heightPixels / displayMetrics.density;

        return Math.round(screenHeightInDP);
    }*/

    /*static String getURI(int type) {
        final String BASE_URL = "https://mais.cpb.com.br/?post_type=meditacao&p=45805";
        final String POST_TYPE_PARAM = "post_type";
        final String POST_TYPE_VALUE = "meditacao";
        Uri.Builder uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(POST_TYPE_PARAM, POST_TYPE_VALUE);

        switch (type) {
            case Meditacao.ADULTO:
                //adultos
                return "https://gist.githubusercontent.com/anonymous/c144323796e2e6b8e30825081fc933eb/raw/63f7e72f8f6b43d7eb2180d891d2858087562158/um_dia_inesquecivel_2018.json";
            case Meditacao.MULHER:
                //mulher
                //return "https://gist.githubusercontent.com/anonymous/2e25e4eca84fae894d0c10e0cc4e86ae/raw/df61d4a63b5756fbb3441b1a5431304c100977f6/vivendo_seu_amor.json";
                return "https://gist.githubusercontent.com/anonymous/4f7a5cd888863ec1b5bbe405af9266ef/raw/bba851802a884d4ee7494ed37e21718bf82ce6be/amor_eterno_2018.json";
            case Meditacao.JUVENIL:
                //juvenil
                //return "https://gist.githubusercontent.com/anonymous/00b246e71aa73375621547219fd3aecf/raw/cd80726fb8c14ce7f29a2e3c55a3a944d99a87c2/siga_o_mestre.json";
                return "https://gist.githubusercontent.com/anonymous/2e076da9d6c8a22ef40fb32ea7269ee8/raw/c9426c37dea0b213f56bc519e0143ba088c36432/pense_bem_2018.json";
            case Meditacao.ABJANELAS:
                return "https://gist.githubusercontent.com/anonymous/386f9d8d5ea404c14d6da332e7a4a744/raw/5b2a5f10eaa84e54e5da7029ce121ed1717a5fe5/janelas_para_a_vida.json";
        }
        return "";
    }*/

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

            connection.getURL();

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
