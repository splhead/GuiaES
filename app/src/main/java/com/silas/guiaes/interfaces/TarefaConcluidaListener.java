package com.silas.guiaes.interfaces;

/**
 * Created by silas on 08/09/14.
 */
import org.jsoup.nodes.Document;

public interface TarefaConcluidaListener {
    void quandoTarefaConcluida(Document html);
}