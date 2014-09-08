package com.silas.guiaes.models;

/**
 * Created by silas on 08/09/14.
 */
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class Extracao {
    public Elements buscaElementos(Document html, String busca) {
        return html.select(busca);
    }

    public Element buscaElemento(Document html, String busca) {
        return html.select(busca).first();
    }

    public abstract void iniciaExtracao(Document html);
}