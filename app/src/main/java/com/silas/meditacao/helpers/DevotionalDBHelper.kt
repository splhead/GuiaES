package com.silas.meditacao.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DevotionalDBHelper(context: Context) : SQLiteOpenHelper(context,
        DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        val DATABASE_VERSION = 4
        val DATABASE_NAME = "guia"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_BANCO_DADOS = "CREATE TABLE IF NOT EXISTS " +
                "${DevotionalContract.TABLE_NAME} (" +
                "${DevotionalContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DevotionalContract.COLUMN_TITLE} TEXT NOT NULL," +
                "${DevotionalContract.COLUMN_DATE} TEXT NOT NULL," +
                "${DevotionalContract.COLUMN_VERSE} TEXT NOT NULL," +
                "${DevotionalContract.COLUMN_TEXT} TEXT NOT NULL," +
                "${DevotionalContract.COLUMN_TYPE} INTEGER NOT NULL" +
                ");"

        try {
            db.beginTransaction()
            db.execSQL(SQL_BANCO_DADOS)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersionNumber: Int, newVersionNumber: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
