package com.silas.meditacao.io;

import android.content.Context;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by silas on 02/06/15.
 */
public class ExtraiMeditacao {

    private MeditacaoDBAdapter mdba;
    private ArrayList<Meditacao> meditacoes = new ArrayList<>();
    private Calendar c = Calendar.getInstance();

    public ExtraiMeditacao(Context context) {
        mdba = new MeditacaoDBAdapter(context);
    }

    public void processaExtracao(String html, int tipo) {
        Document doc = Jsoup.parse(html);
        Element raiz = doc.select("div[style^=width: 74%]").first();
        Elements titulos = doc.select("div[style^=width: 74%] td[style^=width:67.0%]");

        if (mesCorreto(doc)) {
            c.set(Calendar.DAY_OF_MONTH, 1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            StringBuilder sbTexto = new StringBuilder();
            String sData, sTitulo, sTextoBiblico, sTexto;

            for (Element eTitulo : titulos) {
                Meditacao meditacao = new Meditacao("", "", "", "", tipo);

                sTitulo = eTitulo.text();
                //            Log.d("titulo", sTitulo);

                sData = sdf.format(c.getTime());

                Element prox = proximo(eTitulo, raiz);
                //procura pelo proximo <p>
                while (!prox.tagName().equalsIgnoreCase("p")) {
                    prox = prox.nextElementSibling();
                }

                sTextoBiblico = prox.text();
                //            Log.d("textoBiblico", sTextoBiblico);

                //passa para texto da meditacao
                prox = prox.nextElementSibling();

                while (prox.tagName().equalsIgnoreCase("p")) {
                    if (prox.children().size() == 0 || !prox.child(0).tagName().equalsIgnoreCase("strong")) {
                        sbTexto.append(prox.text());
                        sbTexto.append("\n\n");
                    }

                    //passa para o proximo elemento
                    prox = prox.nextElementSibling();
                }

                sTexto = sbTexto.toString();
                //            Log.d("texto", sTexto);

                meditacao.setTitulo(sTitulo);
                meditacao.setData(sData);
                meditacao.setTextoBiblico(sTextoBiblico);
                meditacao.setTexto(sTexto);

                //            Log.d("Meditacao", meditacao.toString());

                meditacoes.add(meditacao);

                sbTexto.delete(0, sbTexto.length());
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            mdba.addMeditacoes(meditacoes);
        }
    }

    /**
     * Verifica se o mes no site é o mes atual
     */

    private boolean mesCorreto(Document doc) {
        Calendar calendar = Calendar.getInstance();
        String[] meses = {
                "janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        };

        Element eTd = doc.select("div[style^=width: 74%] td[style^=width:33.0%]").first();

        String mes = meses[calendar.get(Calendar.MONTH)];

        return eTd.text().toLowerCase().contains(mes);

    }

    /*
     * Sobe o nível até a raiz para buscar os próximos elementos
	 */
    private Element proximo(Element irmao, Element raiz) {
        Element proximo = irmao;
        //Log.d("oL", "pProximo: " + proximo.tagName() + " " + proximo.id());
        while (!proximo.parent().equals(raiz)) {
            proximo = proximo.parent();
//            Log.d("oL", "proximo: " + proximo.tagName() + " " + proximo.id());
        }
//        Log.d("oL", "uProximo: " + proximo.tagName());
        return proximo.nextElementSibling();
    }
}
