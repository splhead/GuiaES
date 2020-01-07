package com.silas.meditacao.interfaces

import com.silas.meditacao.models.Meditacao


interface Extractable {
    //    encapsula o algoritmo de extração que pode variar conforme a fonte
    fun extractDevotional(): List<Meditacao>

    //    verifica se esta extraindo conteudo atual
//    fun contentIsUpdated(): Boolean

}

