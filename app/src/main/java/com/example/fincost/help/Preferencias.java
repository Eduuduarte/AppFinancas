package com.example.fincost.help;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {
    private Context contexto;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "fincost.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private String CHAVE_INDENTIFICADOR = "indentificadorUsuarioLogado";
    private String CHAVE_NOME = "nomeUsuarioLogado";

    public Preferencias (Context contextoParametros){
        contexto = contextoParametros;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }
    public void salvarDados(String identificadorUSuario, String nomeUsuario){
        editor.putString(CHAVE_INDENTIFICADOR, identificadorUSuario);
        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.commit();
    }

    public String getIdentificador(){
        return preferences.getString(CHAVE_INDENTIFICADOR, null);
    }
    public String getNomeUSuario(){
        return  preferences.getString(CHAVE_NOME, null);
    }
}
