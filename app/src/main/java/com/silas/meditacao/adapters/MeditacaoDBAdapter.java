package com.silas.meditacao.adapters;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.silas.meditacao.models.Meditacao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MeditacaoDBAdapter extends DBAdapter {
    public static final String ROWID = "_id";
    public static final String DATA = "data";
    public static final String TITULO = "titulo";
    public static final String TEXTO_BIBLICO = "texto_biblico";
    public static final String TEXTO = "texto";
    public static final String TIPO = "tipo";

    private static final String BD_TABELA = "meditacao";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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
                if (this.meditacao(stringToCalendar(meditacao.getData()), meditacao.getTipo()) != null) {
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

    /*public String calendarToString(Calendar data) {
        return sdf.format(data.getTime());
    }*/

    private Calendar stringToCalendar(String data) {
        try {
            Calendar dia = Calendar.getInstance();
            dia.setTime(sdf.parse(data));
            return dia;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addMeditacoes(ArrayList<Meditacao> meditacoes) {
        try {
            abrir();
            add(meditacoes);
        } finally {
            try {
                fechar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private Meditacao meditacao(Calendar data, int tipo) {
        Cursor c = null;
        Calendar dia = data;

        if (tipo == Meditacao.ABJANELAS) {
            dia = Calendar.getInstance();
            dia.setTimeInMillis(data.getTimeInMillis());
            dia.set(Calendar.YEAR, 2017);
        }

        String sData = sdf.format(dia.getTime());

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
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public Meditacao buscaMeditacao(Calendar data, int iTipo) {
        try {
            abrir();
            return meditacao(data, iTipo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fechar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private long[] minMax(int tipo) {
        Cursor c = null;
        try {
            c = bancoDados.query(true, BD_TABELA,
                    new String[]{"min(" + DATA + ")", "max(" + DATA + ")"}
                    , TIPO + " = " + tipo
                    , null, TIPO, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                return new long[]{sdf.parse(c.getString(0)).getTime(),
                        sdf.parse(c.getString(1)).getTime()};
               /* return new Meditacao(c.getLong(0), c.getString(1), c.getString(2)
                        , c.getString(3), c.getString(4), c.getInt(5));*/
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
     * Busca intervalo de data disponível
     */
    public long[] buscaDataMinMax(int iTipo) {
        try {
            abrir();
            return minMax(iTipo);
        } finally {
            try {
                fechar();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

	public List<Meditacao> buscaTodasMeditacoes(long trimestreID) {
		try {
			abrir();
			return licoes(trimestreID);
		} finally {
			fechar();
		}
	}*/
}
