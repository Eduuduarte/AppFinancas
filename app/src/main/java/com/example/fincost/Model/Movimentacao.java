package com.example.fincost.Model;

import com.example.fincost.Config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data;
    private String ano;
    private String mes;
    private String tipo;
    private String Categoria;
    private String Descricao;
    private Double valor;
    private String idUsuario;

    public Movimentacao() {

    }
    public void salvar (String idUsuarioM, String anoM, String mesM){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
        firebase.child("Movimentacao")
                .child(idUsuarioM)
                .push()
                .setValue(this);

    }
    public void salvarGastosMensal(String idUsuarioMs, String anoMs, String mesMs){
        DatabaseReference firebaseS = ConfiguracaoFirebase.getFirebase();
        firebaseS.child("movimentoM")
                .child(idUsuarioMs)
                .child(anoMs)
                .child(mesMs)
                .setValue(this);

    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }


}
