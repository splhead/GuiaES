package com.silas.meditacao.io;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silas.meditacao.interfaces.Extractable;
import com.silas.meditacao.models.Meditacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class JsonExtractable implements Extractable {
    private ArrayList<Meditacao> dias = new ArrayList<>();
    private String contentJSON;

    JsonExtractable(String content) {
        contentJSON = content;
    }

    @NonNull
    @Override
    public ArrayList<Meditacao> extractDevotional() {
        if (contentJSON != null && !contentJSON.isEmpty()) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd");
            Gson gson = gsonBuilder.create();

//          Log.i(getClass().getSimpleName(), gson.fromJson(contentJSON, Meditacao[].class).toString());
            List<Meditacao> meditacoes = Arrays.
                    asList(gson.fromJson(contentJSON, Meditacao[].class));

            for (Meditacao meditacao : meditacoes) {
                Log.i(getClass().getSimpleName() , meditacao.toString());
                dias.add(meditacao);
            }
//          Log.i(getClass().getSimpleName(), meditacoes.size() + " meditacoes loaded.");
        }
        return dias;
    }


    @Override
    public boolean contentIsUpdated() {
        return true;
    }
}
