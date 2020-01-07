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
    //    private static final String TAG = MeditacaoDBAdapter.class.getSimpleName();
    public static final String ROWID = "_id";
    public static final String DATA = "data";
    public static final String TITULO = "titulo";
    public static final String TEXTO_BIBLICO = "texto_biblico";
    public static final String TEXTO = "texto";
    public static final String TIPO = "tipo";
    public static final String FAVORITE = "favorite";

    private static final String BD_TABELA = "meditacao";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public MeditacaoDBAdapter(Context contexto) {
        super(contexto);
    }

    private void add(ArrayList<Meditacao> meditacoes) {

        //Log.d("adapter", licao.toString());
        bancoDados.beginTransaction();
        try {
            int counter = 0;
            for (Meditacao meditacao : meditacoes) {
                // verifica se o registro já exite no banco
                if (this.meditacao(stringToCalendar(meditacao.getData()), meditacao.getTipo()) != null) {
                    Log.w(getClass().getName(),
                            "A meditacao já existe e não será gravada");
                }

                ContentValues valores = new ContentValues();
                valores.put(DATA, meditacao.getData());
                valores.put(TITULO, meditacao.getTitulo().toUpperCase());
                valores.put(TEXTO_BIBLICO, meditacao.getTextoBiblico());
                valores.put(TEXTO, meditacao.getTexto());
                valores.put(TIPO, meditacao.getTipo());
                valores.put(FAVORITE, booleanToInt(meditacao.isFavorite()));


                bancoDados.insert(BD_TABELA, null, valores);
                counter++;
//                Log.i(getClass().getSimpleName(), "Gravando: " + meditacao.toString());
                if (counter > 100) {
                    counter = 0;
                    bancoDados.setTransactionSuccessful();
                    bancoDados.endTransaction();
                    bancoDados.beginTransaction();
                }
            }
            bancoDados.setTransactionSuccessful();
        } catch (SQLException e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
    }

    private int booleanToInt(boolean fav) {
        return fav ? 1 : 0;
    }

    private boolean intToBoolean(int num) {
        return num == 1;
    }

    private void updateFavorite(Meditacao meditacao) {

        String id = String.valueOf(meditacao.getId());

        try {
            ContentValues values = new ContentValues();
            values.put(FAVORITE, booleanToInt(meditacao.isFavorite()));
            bancoDados.beginTransaction();
            bancoDados.update(BD_TABELA, values
                    , ROWID + "=?", new String[]{id});
            bancoDados.setTransactionSuccessful();
//            Log.i(TAG, id + " atualizado");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            bancoDados.endTransaction();
        }
    }

    public void updateDevotionalFavorite(Meditacao meditacao) {
        try {
            abrir();
            updateFavorite(meditacao);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
    }

    private ArrayList<Meditacao> getFavorites() {
        ArrayList<Meditacao> devotionals = new ArrayList<>();
        Cursor c = bancoDados.query(BD_TABELA
                , new String[]{ROWID, TITULO, DATA, TEXTO_BIBLICO, TEXTO, TIPO, FAVORITE}
                , FAVORITE + "=1"
                , null
                , null
                , null
                , DATA + " DESC"
                , null);

        if (c.moveToFirst()) {
            do {
                devotionals.add(new Meditacao(c.getLong(0)
                        , c.getString(1)
                        , c.getString(2)
                        , c.getString(3)
                        , c.getString(4)
                        , c.getInt(5)
                        , intToBoolean(c.getInt(6)))
                );
            } while (c.moveToNext());
            c.close();
        }

        return devotionals;
    }

    public ArrayList<Meditacao> fetchFavorites() {
        try {
            abrir();
            return getFavorites();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
        return null;
    }

    private Meditacao meditacao(Calendar data, int tipo) {
        Calendar dia = data;

        if (tipo == Meditacao.ABJANELAS) {
            dia = Calendar.getInstance();
            dia.setTimeInMillis(data.getTimeInMillis());
            dia.set(Calendar.YEAR, 2017);
        }

        String sData = sdf.format(dia.getTime());

        Cursor c = bancoDados.query(true, BD_TABELA,
                new String[]{ROWID, TITULO, DATA, TEXTO_BIBLICO, TEXTO, TIPO, FAVORITE}
                , DATA + " = ? AND " + TIPO + " = ?"
                , new String[]{sData, String.valueOf(tipo)}
                , null, null, null, null);

        if (c.moveToFirst()) {
            Meditacao meditacao = new Meditacao(c.getLong(0)
                    , c.getString(1)
                    , c.getString(2)
                    , c.getString(3)
                    , c.getString(4)
                    , c.getInt(5)
                    , intToBoolean(c.getInt(6))
            );
//                Log.i(getClass().getName(), meditacao.toString());
            c.close();
            return meditacao;
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
            fechar();
        }
        return null;
    }

    private long[] minMax(int tipo) {
        Cursor c = bancoDados.query(true, BD_TABELA,
                new String[]{"min(" + DATA + ")", "max(" + DATA + ")"}
                , TIPO + " = ?"
                , new String[]{String.valueOf(tipo)}
                , TIPO, null, null, null);

        if (c.moveToFirst()) {
            long[] minMax = new long[2];
            try {
                minMax[0] = sdf.parse(c.getString(0)).getTime();
                minMax[1] = sdf.parse(c.getString(1)).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            c.close();

            return minMax;
//                Log.i(getClass().getName(), meditacao.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
        return null;
    }
}
