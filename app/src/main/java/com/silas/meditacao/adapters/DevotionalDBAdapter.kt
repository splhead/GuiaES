package com.silas.meditacao.adapters

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.silas.meditacao.helpers.DevotionalContract
import com.silas.meditacao.models.Meditacao
import java.util.*

class DevotionalDBAdapter(context: Context) : BaseDBAdapter(context) {
    fun add(devotionals: ArrayList<Meditacao>) {

        try {
            open()
            db.beginTransaction()

            for (devotional in devotionals) {
                val content = ContentValues()
                content.put(DevotionalContract.COLUMN_DATE, devotional.data)
                content.put(DevotionalContract.COLUMN_TITLE, devotional.titulo)
                content.put(DevotionalContract.COLUMN_VERSE, devotional.textoBiblico)
                content.put(DevotionalContract.COLUMN_TEXT, devotional.texto)
                content.put(DevotionalContract.COLUMN_TYPE, devotional.tipo)

                db.insert(DevotionalContract.TABLE_NAME, null, content)
            }

            db.setTransactionSuccessful()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            close()
        }
    }

    fun getDevotional(date: Calendar, type: Int) {
        var cursor: Cursor
        val columns = arrayOf(DevotionalContract.COLUMN_ID
                , DevotionalContract.COLUMN_TITLE
                , DevotionalContract.COLUMN_DATE
                , DevotionalContract.COLUMN_VERSE
                , DevotionalContract.COLUMN_TEXT
                , DevotionalContract.COLUMN_TYPE
        )
        val selection = "${DevotionalContract.COLUMN_DATE} like '%'" +
                " AND ${DevotionalContract.COLUMN_TYPE}  =  $type"
        try {
            cursor = db.query(true, DevotionalContract.TABLE_NAME, columns, selection,
                    null, null, null, null, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                Meditacao(cursor.getLong(0), cursor.getString(1)
                        , cursor.getString(2), cursor.getString(3)
                        , cursor.getString(4), cursor.getInt(5))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
//            cursor?.close()
            close()
        }
    }
}
