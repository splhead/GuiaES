package com.silas.meditacao.io;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by silas on 15/12/16.
 */

public class JsonExtractable implements Extractable {
    private ArrayList<Meditacao> dias = new ArrayList<>();
    private String contentJSON;
    private Gson gson;
    private Calendar c = Calendar.getInstance();

    public JsonExtractable(String content) {
        contentJSON = content;
    }

    @Override
    public ArrayList<Meditacao> extraiMeditacao(int type) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        gson = gsonBuilder.create();

//        List<Meditacao> meditacoes = Arrays.asList(gson.fromJson(contentJSON, Meditacao[].class));
//        Log.i("JsonExtractable", gson.fromJson(contentJSON, Meditacao[].class).toString());
        List<Meditacao> meditacoes = Arrays.asList(gson.fromJson(contentJSON, Meditacao[].class));

//        Log.i("Json", meditacoes.size() + " meditacoes loaded.");
        for (Meditacao meditacao : meditacoes) {
            Log.i("PostActivity", meditacao.toString());
            dias.add(meditacao);
        }
//        Log.i("JsonExtractable", meditacoes.size() + " meditações carregadas.");
//        for (Meditacao m : meditacoes) {
//            Log.d("JsonExtractable", m.toString());
//        }
//
//
//        return new ArrayList<Meditacao>(meditacoes);
        return dias;
    }

    @Override
    public boolean conteudoEstaAtualizado() {
        return true;
    }
}
