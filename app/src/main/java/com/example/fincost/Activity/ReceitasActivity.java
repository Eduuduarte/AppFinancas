package com.example.fincost.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.fincost.Mascara.MaskTextWatcher;
import com.example.fincost.Mascara.SimpleMaskFormatter;
import com.example.fincost.R;



import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceitasActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private Toolbar toolbar;
    private EditText editTextData, editTextValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        editTextData = findViewById(R.id.editTextDate);
        editTextValor = findViewById(R.id.editTextValor);
        //Criando mascara data
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editTextData, smf);
        editTextData.addTextChangedListener(mtw);

        //Criando mascara money
        SimpleMaskFormatter smfValor = new SimpleMaskFormatter("NNN.NN");
        MaskTextWatcher mtwValor = new MaskTextWatcher(editTextValor, smfValor);
        editTextValor.addTextChangedListener(mtwValor);


        autoCompleteTextView = findViewById(R.id.textList);
        String [] opcaoTipo = {"Fixo", "Variável"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.opcao, opcaoTipo);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        toolbar = findViewById(R.id.toolbarReceitas);
        toolbar.setTitle("Adicionar Receitas");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);


    }

}