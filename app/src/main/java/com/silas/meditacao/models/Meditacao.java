package com.silas.meditacao.models;


import android.support.v4.app.Fragment;

import com.google.gson.annotations.SerializedName;
import com.silas.meditacao.fragments.ContentFragment;

/**
 * Created by silas on 08/09/14.
 */
public class Meditacao {
    public static final int ADULTO = 1;
    public static final int MULHER = 2;
    public static final int JUVENIL = 3;
    public static final int ABJANELAS = 4;

    private long id;

    @SerializedName("day")
    private String data;

    @SerializedName("title")
    private String titulo;

    @SerializedName("verse")
    private String textoBiblico;

    @SerializedName("text")
    private String texto;

    @SerializedName("type")
    private int tipo;

    public Meditacao(long id, String titulo, String data, String textoBiblico, String texto, int tipo) {
        this.id = id;
        this.titulo = titulo;
        this.data = data;
        this.textoBiblico = textoBiblico;
        this.texto = texto;
        this.tipo = tipo;
    }

    public Meditacao(String titulo, String data, String textoBiblico, String texto, int tipo) {
        this.titulo = titulo;
        this.data = data;
        this.textoBiblico = textoBiblico;
        this.texto = texto;
        this.tipo = tipo;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataPorExtenso() {
        //yyyy-MM-dd
        String out = "";
        String mes[] = new String[]{"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        try {
            out = data.substring(8) + " de "
                    + mes[Integer.parseInt(data.substring(5, 7)) - 1]
                    + " de " + data.substring(0, 4);
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return out;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTextoBiblico() {
        return textoBiblico;
    }

    public void setTextoBiblico(String textoBiblico) {
        this.textoBiblico = textoBiblico;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String toString() {
        return this.getTitulo() + " " + this.getData() + " " + getTipo();
    }

    public int getTipo() {
        return tipo;
    }

    public String getNomeTipo() {
        switch (getTipo()) {
            case ADULTO:
                return "Adulto";
            case MULHER:
                return "Mulher";
            case JUVENIL:
                return "Juvenil";
            case ABJANELAS:
                return "A. Bullon";
        }
        return "";
    }

    public Fragment createFragment() {
        return ContentFragment.newInstance(this.getTitulo(), this.getTextoBiblico(),
                this.getDataPorExtenso(), this.getTexto());
    }
}
