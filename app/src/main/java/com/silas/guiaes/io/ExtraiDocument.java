package com.silas.guiaes.io;

import com.silas.guiaes.interfaces.TarefaConcluidaListener;

import org.jsoup.nodes.Document;

/**
 * Created by silas on 10/09/14.
 */
public class ExtraiDocument implements TarefaConcluidaListener {
    private Document html;
    public ExtraiDocument(String url) {
        new DocumentDownloadTask().execute(url);
    }
    @Override
    public void quandoTarefaConcluida(Document html) {
        html(html);
    }

    public Document html() {
        return html;
    }

    public void html(Document html) {
        this.html = html;
    }
}
