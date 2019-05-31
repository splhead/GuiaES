package com.silas.meditacao.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DATABASE_VERSION = 4
const val DATABASE_NAME = "guia"
class DevotionalDBHelper(context: Context) : SQLiteOpenHelper(context,
        DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = "CREATE TABLE IF NOT EXISTS " +
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
            db.execSQL(sql)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersionNumber: Int, newVersionNumber: Int) {
//        nothing
    }
}
