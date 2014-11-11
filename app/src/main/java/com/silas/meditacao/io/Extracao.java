package com.silas.meditacao.io;


import android.content.Context;
import android.util.Log;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.models.Meditacao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by silas on 08/09/14.
 */
public class Extracao {
    private Meditacao meditacao = new Meditacao("", "", "", "");
    ;
    private Context mContext;
    private MeditacaoDBAdapter mdba;

    public Extracao(Context context) {
        mContext = context;
        mdba = new MeditacaoDBAdapter(mContext);
    }

    public void extraiMeditacao(String html) {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        int iMes = c.get(Calendar.MONTH);
        int iDia = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sbTexto = new StringBuilder();
        String sData, sTitulo, sTextoBiblico, sTexto;
        Document doc = Jsoup.parse(html);
        Element raiz = doc.select("div#conteudo").first();
        Elements h2Titulos = doc.select("div#conteudo h2");
        for (Element eTitulo : h2Titulos) {
            //System.out.println(iDia + " " + titulo.text());
            if(eTitulo.nextElementSibling() != null) {
                continue;
            }

            Element prox = proximo(eTitulo, raiz);
            int contaHR = 0;


            while (contaHR < 2) {
                if (prox.tagName().equals("hr")) {
                    contaHR += 1;
                }
                //Log.d("eM", prox.tagName() + ' ' + prox.text());
                try {
                    if (prox.previousElementSibling().tagName().equalsIgnoreCase("hr")) {
                        if (prox.text().length() < 2) {
                            prox = prox.nextElementSibling();
                        }
                        sData = sdf.format(c.getTime());
                        //correção do titulo em dois h2
                        if (eTitulo.previousElementSibling() != null) {
                            sTitulo = eTitulo.previousElementSibling().text() + " " + eTitulo.text();
                        } else {
                            sTitulo = eTitulo.text();
                        }
                        Log.d("titulo", sTitulo);
                        sTextoBiblico = prox.text();
                        //                        System.out.println( sData + " " +
                        //                                 sTitulo +"\ntexto_biblico: " + sTextoBiblico);

                        meditacao.setData(sData);
                        meditacao.setTitulo(sTitulo);
                        meditacao.setTextoBiblico(sTextoBiblico);

                    } else if (prox.text().length() > 1 && prox.tagName().equalsIgnoreCase("p")) {
                        sbTexto.append(prox.text() + "\n\n");
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                }

                // passa para o próximo do mesmo nível
                prox = prox.nextElementSibling();
            }
            sTexto = sbTexto.toString();
//            System.out.println("\ntexto: " + sTexto);


            meditacao.setTexto(sTexto);
//            Log.d(this.getClass().getSimpleName(),meditacao.toString());

            mdba.addMeditacao(meditacao);

            sbTexto.delete(0, sbTexto.length());
            c.add(Calendar.DAY_OF_MONTH, 1);

        }
    }

    public boolean ePaginaMeditacao(String html) {
        Document doc = Jsoup.parse(html);
        return !doc.select("title").contains("Login");
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
