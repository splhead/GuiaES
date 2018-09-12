package com.silas.meditacao.helpers

import android.provider.BaseColumns

class DevotionalContract : BaseColumns {
    companion object {
        val COLUMN_ID: String = BaseColumns._ID
        val TABLE_NAME: String = "meditacao"
        val COLUMN_DATE: String = "data"
        val COLUMN_TITLE: String = "titulo"
        val COLUMN_VERSE: String = "texto_biblico"
        val COLUMN_TEXT: String = "texto"
        val COLUMN_TYPE: String = "tipo"
        val COLUMN_FAVORITE: String = "favorite"
    }
}
