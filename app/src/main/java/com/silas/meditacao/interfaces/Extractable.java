package com.silas.meditacao.interfaces;

import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;

/**
 * Created by silas on 03/05/16.
 */
public interface Extractable {
    //    encapsula o algoritmo de extração que pode variar conforme a fonte
    ArrayList<Meditacao> extraiMeditacao(int type);

    //    verifica se esta extraindo conteudo atual
    boolean conteudoEstaAtualizado();

}

