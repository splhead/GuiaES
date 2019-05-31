package com.silas.meditacao.helpers

import android.provider.BaseColumns

class DevotionalContract : BaseColumns {
    companion object {
        const val COLUMN_ID: String = BaseColumns._ID
        const val TABLE_NAME: String = "meditacao"
        const val COLUMN_DATE: String = "data"
        const val COLUMN_TITLE: String = "titulo"
        const val COLUMN_VERSE: String = "texto_biblico"
        const val COLUMN_TEXT: String = "texto"
        const val COLUMN_TYPE: String = "tipo"
        const val COLUMN_FAVORITE: String = "favorite"
    }
}
