package com.silas.meditacao.io;

import android.content.Context;
import android.util.Log;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by silas on 02/06/15.
 */
public class ExtraiMeditacao {
    private Meditacao meditacao;

    private Context mContext;
    private MeditacaoDBAdapter mdba;
    private ArrayList<Meditacao> meditacoes = new ArrayList<Meditacao>();

    public ExtraiMeditacao(Context context) {
        mContext = context;
        mdba = new MeditacaoDBAdapter(mContext);
    }

    private processaExtracao(String html, int tipo) {
        StringBuilder sbTexto = new StringBuilder();
        String sData, sTitulo, sTextoBiblico, sTexto;
        Document doc = Jsoup.parse(html);
        Element raiz = doc.select("div[style^=width: 74%]").first();
        Elements titulos = doc.select("div[style^=width: 74%] td[style^=width:67.0%]");
        for(Element eTitulo:titulos) {
            meditacao = new Meditacao("", "", "", "", tipo);
            Element prox = proximo(eTitulo, raiz);
            if(prox.tagName().equalsIgnoreCase('p')) {

            }
        }

    }

    /*
     * Sobe o nível até a raiz para buscar os próximos elementos
	 */
    private Element proximo(Element irmao, Element raiz) {
        Element proximo = irmao;
        //Log.d("oL", "pProximo: " + proximo.tagName() + " " + proximo.id());
        while (!proximo.parent().id().equals(raiz.id())) {
            proximo = proximo.parent();
//            Log.d("oL", "proximo: " + proximo.tagName() + " " + proximo.id());
        }
//        Log.d("oL", "uProximo: " + proximo.tagName());
        return proximo.nextElementSibling();
    }

    private String getURL(int tipo) {
        //inspiracao-juvenil/mensal
        String url = "http://iasdcolonial.org.br/index.php/";

        switch (tipo) {
            case Meditacao.MULHER:
                url += "meditacao-da-mulher/mensal";
                break;
            case Meditacao.JUVENIL:
                url += "inspiracao-juvenil/mensal";
                break;
            case Meditacao.ADULTO:
                url += "meditacao-diaria/mensal";
        }
        Log.d("getUrl", url);
        return url;
    }

    private ArrayList<String> getURLs() {
        String url = "http://iasdcolonial.org.br/index.php/"; //
        ArrayList<String> urls = new ArrayList<String>();
        //adultos
        urls.add(url + "meditacao-diaria/mensal");
        //mulher
        urls.add(url + "meditacao-da-mulher/mensal");
        //juvenil
        urls.add(url + "inspiracao-juvenil/mensal");

        return urls;
    }
}
