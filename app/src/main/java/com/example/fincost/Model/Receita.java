package com.example.fincost.Model;

import com.example.fincost.Config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Receita {

    private String id;
    private String ano;
    private String mes;
    private Double despesaMensal = 0.0;
    private Double receitaMensal = 0.0;

    public Receita() {
    }

        public void salvarReceitaMensal(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
        firebase.child("movimentoMensal")
                .child(getId())
                .child(getAno())
                .child(getMes())
                .setValue(this);
    }

    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
    }

    public Double getDespesaMensal() {
        return despesaMensal;
    }

    public void setDespesaMensal(Double despesaMensal) {
        this.despesaMensal = despesaMensal;
    }

    public Double getReceitaMensal() {
        return receitaMensal;
    }

    public void setReceitaMensal(Double receitaMensal) {
        this.receitaMensal = receitaMensal;
    }
}