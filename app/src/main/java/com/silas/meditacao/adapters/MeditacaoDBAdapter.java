package com.silas.meditacao.adapters;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;

public class MeditacaoDBAdapter extends DBAdapter {
    public static final String ROWID = "_id";
    public static final String DATA = "data";
    public static final String TITULO = "titulo";
    public static final String TEXTO_BIBLICO = "texto_biblico";
    public static final String TEXTO = "texto";
    public static final String TIPO = "tipo";

    private static final String BD_TABELA = "meditacao";

    public MeditacaoDBAdapter(Context contexto) {
        super(contexto);
    }

    /*private long add(Meditacao meditacao) {
        // verifica se o registro já exite no banco
        if (this.meditacao(meditacao.getData(), meditacao.getTipo()) != null) {
            Log.w(getClass().getName(),
                    "A meditacao já existe e não será gravada.");
            return -1;
        }
        //Log.d("adapter", licao.toString());
        ContentValues valores = new ContentValues();
        valores.put(DATA, meditacao.getData());
        valores.put(TITULO, meditacao.getTitulo());
        valores.put(TEXTO_BIBLICO, meditacao.getTextoBiblico());
        valores.put(TEXTO, meditacao.getTexto());
        valores.put(TIPO, meditacao.getTipo());

//        Log.i(getClass().getSimpleName(), "Gravando: " + meditacao.toString());
        try {
            bancoDados.beginTransaction();
            return bancoDados.insert(BD_TABELA, null, valores);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    */

    /**
     * Grava a meditacao no banco
     *
     *//*
    public long addMeditacao(Meditacao meditacao) {
        try {
            abrir();
            return add(meditacao);
        } finally {
            fechar();
        }
    }*/

    private void add(ArrayList<Meditacao> meditacoes) {

        //Log.d("adapter", licao.toString());
        bancoDados.beginTransaction();
        try {
            for (Meditacao meditacao : meditacoes) {
                // verifica se o registro já exite no banco
                if (this.meditacao(meditacao.getData(), meditacao.getTipo()) != null) {
                    Log.w(getClass().getName(),
                            "A meditacao já existe e não será gravada");
                }
                ContentValues valores = new ContentValues();
                valores.put(DATA, meditacao.getData());
                valores.put(TITULO, meditacao.getTitulo());
                valores.put(TEXTO_BIBLICO, meditacao.getTextoBiblico());
                valores.put(TEXTO, meditacao.getTexto());
                valores.put(TIPO, meditacao.getTipo());


                bancoDados.insert(BD_TABELA, null, valores);
//                Log.i(getClass().getSimpleName(), "Gravando: " + meditacao.toString());
            }
            bancoDados.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bancoDados.endTransaction();
        }
    }

    public void addMeditacoes(ArrayList<Meditacao> meditacoes) {
        try {
            abrir();
            add(meditacoes);
        } finally {
            fechar();
        }
    }


    private Meditacao meditacao(String sData, int tipo) {
        Cursor c = null;
        try {
            c = bancoDados.query(true, BD_TABELA,
                    new String[]{ROWID, TITULO, DATA, TEXTO_BIBLICO, TEXTO, TIPO}
                    , DATA + " like '" + sData + "%' AND " + TIPO + " = " + tipo
                    , null, null, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                return new Meditacao(c.getLong(0), c.getString(1), c.getString(2)
                        , c.getString(3), c.getString(4), c.getInt(5));
//                Log.i(getClass().getName(), meditacao.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    /**
     * Busca a lição com base em seu número!
     *
     */
    public Meditacao buscaMeditacao(String sData, int iTipo) {
        try {
            abrir();
            return meditacao(sData, iTipo);
        } finally {
            fechar();
        }
    }
    /*
	private List<Meditacao> meditacoes(long trimestreID) {
		List<Licao> licoesList = new ArrayList<Licao>();
		Cursor c = bancoDados.query(BD_TABELA, 
				new String[] {ROWID, DATA, NUMERO, TITULO, TRIMESTREID, CAPA}
				, TRIMESTREID + "=" + trimestreID, null, null, null, null);
		try {			
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					Licao licao = new Licao(c.getLong(0), c.getString(1),
							c.getInt(2), c.getString(3), c.getLong(4),
							c.getBlob(5));
					
					licoesList.add(licao);
					Log.d(getClass().getName(), licao.toString());
					
				} while (c.moveToNext());				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return licoesList;
	}
	
	*//**
     * Busca todos as lições
     *
     * @return List<Licao>
     *//*
	public List<Meditacao> buscaTodasMeditacoes(long trimestreID) {
		try {
			abrir();
			return licoes(trimestreID);
		} finally {
			fechar();
		}
	}*/
}
