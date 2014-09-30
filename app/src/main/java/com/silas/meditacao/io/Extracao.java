package com.silas.meditacao.io;


import android.content.Context;
import android.util.Log;

import com.silas.meditacao.models.Meditacao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by silas on 08/09/14.
 */
public class Extracao {
    private Meditacao meditacao;
    private Context mContext;

    public Extracao(Context context) {
        mContext = context;
    }

    public void extraiMeditacao(String html) {
        Calendar c = GregorianCalendar.getInstance();
        int iMes = c.get(Calendar.MONTH);
        int iDia = 1;
        StringBuilder sbTexto = new StringBuilder();
        Document doc = Jsoup.parse(html);
        Element raiz = doc.select("div#conteudo").first();
        Elements h2Titulos = doc.select("div#conteudo h2");
        for (Element titulo: h2Titulos) {
            //System.out.println(iDia + " " + titulo.text());
            iDia++;

            Element prox = proximo(titulo, raiz);
            int contaHR = 0;


            while (contaHR < 2) {
                if (prox.tagName().equals("hr")) {
                    contaHR += 1;
                }
                //Log.d("eM", prox.tagName() + ' ' + prox.text());
                try {
                    if(prox.previousElementSibling().tagName().equalsIgnoreCase("hr")) {
                        if (prox.text().length() < 2) {
                            prox = prox.nextElementSibling();
                            System.out.println("passou");
                        }
                        System.out.println(titulo.text() +"\ntexto_biblico: " + prox.text());

                    } else if(prox.text().length() > 1 && prox.tagName().equalsIgnoreCase("p")){
                        sbTexto.append(prox.text());
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                }

                // passa para o próximo do mesmo nível
                prox = prox.nextElementSibling();
            }
            System.out.println("\ntexto: " + sbTexto.toString());
            sbTexto.delete(0,sbTexto.length());
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

}
