package com.silas.meditacao.helpers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.silas.meditacao.adapters.MeditacaoDBAdapter;


public class DBHelper extends SQLiteOpenHelper {
	private static final String NOME_BANCO_DADOS = "guia";

	private static final int VERSAO_BANCO_DADOS = 5;

	// Declaracao do SQL de criacao do banco de dados
	private static final String[] SQL_BANCO_DADOS = {
			"CREATE TABLE IF NOT EXISTS meditacao ("
					+ MeditacaoDBAdapter.ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ MeditacaoDBAdapter.TITULO + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.DATA + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.TEXTO_BIBLICO + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.TEXTO + " TEXT NOT NULL,"
					+ MeditacaoDBAdapter.TIPO + " INTEGER NOT NULL,"
					+ MeditacaoDBAdapter.FAVORITE + " INTEGER NOT NULL DEFAULT 0"
					+ ");"
	};

	public DBHelper(Context contexto) {
		super(contexto, NOME_BANCO_DADOS, null, VERSAO_BANCO_DADOS);
	}

	// Método chamado durante a criação do banco de dados
	@Override
	public void onCreate(SQLiteDatabase bancodados) {
		for (String SQL_BANCO_DADO : SQL_BANCO_DADOS) {
			try {

				bancodados.beginTransaction();
				bancodados.execSQL(SQL_BANCO_DADO);
				bancodados.setTransactionSuccessful();
//                Log.d(getClass().getSimpleName(), "BD criado");

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
//		Log.w(DBHelper.class.getName(),
//				"Atualizando o banco de dados da versão " + versao_antiga
//						+ " para " + nova_versao
//						+ ", que apagará todos os dados da versão antiga");
//		bancodados.execSQL("DROP TABLE IF EXISTS meditacao");
		Log.w(DBHelper.class.getName(),
				"Atualizando o banco de dados da versão " + versao_antiga
						+ " para " + nova_versao
						+ " add coluna de favoritos"
		);
		//apaga a meditação da mulher antiga
		bancodados.execSQL("ALTER TABLE meditacao ADD COLUMN " + MeditacaoDBAdapter.FAVORITE
				+ " INTEGER NOT NULL DEFAULT 0;");
	}

}
