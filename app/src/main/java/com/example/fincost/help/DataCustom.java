package com.example.fincost.help;

import java.text.SimpleDateFormat;

public class DataCustom {

    public static String dataAtual(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormat = simpleDateFormat.format(data);
        return dataFormat;
    }

    public static String mesAnoData(String data){
        String dataRetorno[] = data.split("/");
        String dia = dataRetorno[0];
        String mes  = dataRetorno[1];
        String ano = dataRetorno[2];

        String mesAno = mes + ano;
        return mesAno;
    }
}
