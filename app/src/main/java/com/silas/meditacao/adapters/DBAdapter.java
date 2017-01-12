package com.silas.meditacao.adapters;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.silas.meditacao.helpers.DBHelper;

class DBAdapter {
	SQLiteDatabase bancoDados;
	private Context contexto;
	private DBHelper dbHelper;


	DBAdapter(Context contexto) {
		this.setContexto(contexto);
	}

	private void setContexto(Context contexto) {
		this.contexto = contexto;
	}

	DBAdapter abrir() throws SQLException
	{
		dbHelper = new DBHelper(contexto);
		bancoDados  = dbHelper.getWritableDatabase();
//		Log.i(getClass().getName(), "Conectando com banco de dados");
		return this;
	}

	void fechar() throws SQLException
	{
//		Log.i(getClass().getName(), "Fechando conexão com banco de dados");
		dbHelper.close();
	}
}
