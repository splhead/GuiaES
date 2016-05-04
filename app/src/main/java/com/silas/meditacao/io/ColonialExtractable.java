package com.silas.meditacao.io;

import com.silas.meditacao.interfaces.Extractable;
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
 * Created by silas on 03/05/16.
 */
public class ColonialExtractable implements Extractable {
    private ArrayList<Meditacao> dias = new ArrayList<>();
    private Calendar c = Calendar.getInstance();
    private Document doc;

    public ColonialExtractable(String content) {
        doc = Jsoup.parse(content);
    }

    @Override
    public ArrayList<Meditacao> extraiMeditacao(int type) {
        Element raiz = getRoot();
        Elements titulos = getTitles();

        if (conteudoEstaAtualizado()) {
            c.set(Calendar.DAY_OF_MONTH, 1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            StringBuilder sbTexto = new StringBuilder();
            String sData, sTitulo, sTextoBiblico, sTexto;

            for (Element eTitulo : titulos) {
                Meditacao meditacao = new Meditacao("", "", "", "", type);

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
                    if (prox.children().size() == 0 || !prox.child(0).tagName()
                            .equalsIgnoreCase("strong")) {
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

//                Log.d("Meditacao", meditacao.toString());

                dias.add(meditacao);

                sbTexto.delete(0, sbTexto.length());
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            return dias;
        }
        return null;
    }

    private Element getRoot() {
        Element raiz = null;
        try {
//        Element raiz = doc.select("div[style^=width: 74%]").first();
            raiz = doc.select("div[style^= background-color]").first();
//        correção para meditação da mulher 04/2016
            if (raiz == null) {
                raiz = doc.select("div.img").first();
            }
//        Log.d("raiz", raiz.text());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return raiz;
    }

    private Elements getTitles() {
        Elements titulos = null;

        try {
            titulos = getRoot().select("td[style^=width:67]");
//        correção para meditação da mulher 04/2016
            if (titulos.first() == null) {
                titulos = getRoot().select("div.header-title");
            }
//        Log.d("titulos", titulos.first().text());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return titulos;
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

    @Override
    public boolean conteudoEstaAtualizado() {
        Calendar calendar = Calendar.getInstance();
        String[] meses = {
                "janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        };
        String mes = meses[calendar.get(Calendar.MONTH)];

        Element eTd;

        try {
            if (getTitles().first().siblingElements().isEmpty()) {
//        Correção da meditacao da mulher 04/2016
                eTd = getRoot().select("div.header-dia").first();
            } else {
                eTd = getTitles().first().nextElementSibling();
            }
            return eTd.text().toLowerCase().contains(mes);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }
}
