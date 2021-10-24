package com.example.fincost.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fincost.Model.Movimentacao;
import com.example.fincost.R;
import com.example.fincost.help.Preferencias;

import java.util.ArrayList;

public class MovimentoAdapter extends ArrayAdapter<Movimentacao> {
    private Context context;
    private ArrayList <Movimentacao> movimentacao;


    public MovimentoAdapter(@NonNull Context c, ArrayList<Movimentacao> objects) {
        super(c, 0, objects);
        this.context = c;
        this.movimentacao = objects;

    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       View view = null;
       if(movimentacao != null){

           //Recuperar Usuário
           Preferencias preferencias = new Preferencias(context);
           String idUsuario = preferencias.getIdentificador();

           //Iniciar montagem do layout
           LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

           Movimentacao movimento = movimentacao.get(position);

           view = layoutInflater.inflate(R.layout.movimentacao_list, parent, false);

           //Recuperar elementos

           TextView tipoTexto = view.findViewById(R.id.tipoMovimento);
           TextView descricao = view.findViewById(R.id.descricaoMovimento);
           TextView dataMovimento = view.findViewById(R.id.dataMovimento);
           TextView valorMovimento = view.findViewById(R.id.valorMovimento);

           tipoTexto.setText(movimento.getTipo());
           descricao.setText(movimento.getDescricao());
           dataMovimento.setText(movimento.getData());

           if(movimento.getCategoria().equals("Despesa")) {
               valorMovimento.setText("-" + movimento.getValor().toString());
               valorMovimento.setTextColor(Color.rgb(178,34,34));
           } else if (movimento.getCategoria().equals("Receita")){
               valorMovimento.setText(movimento.getValor().toString());
               valorMovimento.setTextColor(Color.rgb(0,255,127));
           }else {
               valorMovimento.setText(movimento.getValor().toString());
               valorMovimento.setTextColor(Color.rgb(135,206,235));
           }

       }

        return view;
    }
}
