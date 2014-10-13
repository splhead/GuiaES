package com.silas.meditacao.helpers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;


public class DBHelper extends SQLiteOpenHelper {
	private static final String NOME_BANCO_DADOS = "guia";

	private static final int VERSAO_BANCO_DADOS = 1;

	// Declaracao do SQL de criacao do banco de dados
	private static final String[] SQL_BANCO_DADOS = {
			"CREATE TABLE IF NOT EXISTS meditacao ("
					+ MeditacaoDBAdapter.ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ MeditacaoDBAdapter.TITULO + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.DATA + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.TEXTO_BIBLICO + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.TEXTO + " TEXT NOT NULL"
					+ ");"
	};

	public DBHelper(Context contexto) {
		super(contexto, NOME_BANCO_DADOS, null, VERSAO_BANCO_DADOS);
	}

	// Método chamado durante a criação do banco de dados
	@Override
	public void onCreate(SQLiteDatabase bancodados) {
		for (int i = 0; i < SQL_BANCO_DADOS.length; i++) {
			try {

				bancodados.beginTransaction();
				bancodados.execSQL(SQL_BANCO_DADOS[i]);
				bancodados.setTransactionSuccessful();
                Log.d(getClass().getSimpleName(), "BD criado");

			} finally {
				bancodados.endTransaction();
			}
		}
	}

	// Método chamado durante a atualização do bando de dados, se tiver
	// aumentado o valor
	// da versão do banco de dados
	@Override
	public void onUpgrade(SQLiteDatabase bancodados, int versao_antiga,
			int nova_versao) {
		Log.w(DBHelper.class.getName(),
				"Atualizando o banco de dados da versão " + versao_antiga
						+ " para " + nova_versao
						+ ", que apagará todos os dados da versão antiga");
		bancodados.execSQL("DROP TABLE IF EXISTS meditacao");
		onCreate(bancodados);
	}

}
