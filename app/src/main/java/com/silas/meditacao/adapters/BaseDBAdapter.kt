//package com.silas.meditacao.adapters
//
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import com.silas.meditacao.helpers.DevotionalDBHelper
//
//open class BaseDBAdapter(private val context: Context) {
//    protected lateinit var db: SQLiteDatabase
//    private lateinit var ddbh: DevotionalDBHelper
//
//    fun open(): SQLiteDatabase? {
//        ddbh = DevotionalDBHelper(context)
//        db = ddbh.writableDatabase
//        return db
//    }
//
//    fun close() {
//        ddbh.close()
//    }
//
//}
