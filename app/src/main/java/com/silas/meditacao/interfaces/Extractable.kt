package com.silas.meditacao.interfaces

import com.silas.meditacao.models.Meditacao
import java.util.*


interface Extractable {
    //    encapsula o algoritmo de extração que pode variar conforme a fonte
    fun extractDevotional(): ArrayList<Meditacao>

    //    verifica se esta extraindo conteudo atual
//    fun contentIsUpdated(): Boolean

}

