package com.silas.meditacao.models;

/**
 * Created by silas on 08/09/14.
 */
public class Meditacao {
    public static final int ADULTO = 1;
    public static final int MULHER = 2;
    public static final int JUVENIL = 3;
    private long id;
    private String data;
    private String titulo;
    private String textoBiblico;
    private String texto;
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


}
