package com.silas.meditacao.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Meditacao implements Parcelable {
    public static final int ADULTO = 1;
    public static final int MULHER = 2;
    public static final int JUVENIL = 3;
    public static final int ABJANELAS = 4;
    public static final Parcelable.Creator<Meditacao> CREATOR = new Parcelable.Creator<Meditacao>() {
        @Override
        public Meditacao createFromParcel(Parcel source) {
            return new Meditacao(source);
        }

        @Override
        public Meditacao[] newArray(int size) {
            return new Meditacao[size];
        }
    };
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

    /*public Meditacao(String titulo, String data, String textoBiblico, String texto, int tipo) {
        this.titulo = titulo;
        this.data = data;
        this.textoBiblico = textoBiblico;
        this.texto = texto;
        this.tipo = tipo;
    }*/

    private Meditacao(Parcel in) {
        this.id = in.readLong();
        this.data = in.readString();
        this.titulo = in.readString();
        this.textoBiblico = in.readString();
        this.texto = in.readString();
        this.tipo = in.readInt();
    }

    public static String getNomeTipo(int tipo) {
        switch (tipo) {
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

    public static String getDevotionalName(int tipo) {
        switch (tipo) {
            case ADULTO:
                return "Meditação dos Adultos";
            case MULHER:
                return "Meditação das Mulheres";
            case JUVENIL:
                return "Inspiração Juvenil";
            case ABJANELAS:
                return "Janelas para Vida";
            default:
                return "";
        }
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

//    public void setTitulo(String titulo) {
//        this.titulo = titulo;
//    }

    public void setData(String data) {
        this.data = data;
    }

//    public void setTextoBiblico(String textoBiblico) {
//        this.textoBiblico = textoBiblico;
//    }

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

//    public void setTexto(String texto) {
//        this.texto = texto;
//    }

    public String getTitulo() {
        return titulo;
    }

    public String getTextoBiblico() {
        return textoBiblico;
    }

    public String getTexto() {
        return texto;
    }

    /*public Fragment createFragment() {
        return ContentFragment.newInstance(this);
    }*/

    public String toString() {
        return this.getTitulo() + " " + this.getData() + " " + getTipo();
    }

    public int getTipo() {
        return tipo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.data);
        dest.writeString(this.titulo);
        dest.writeString(this.textoBiblico);
        dest.writeString(this.texto);
        dest.writeInt(this.tipo);
    }
}
