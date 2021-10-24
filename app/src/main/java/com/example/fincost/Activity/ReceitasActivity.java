package com.example.fincost.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.fincost.Config.ConfiguracaoFirebase;
import com.example.fincost.Config.DataPickerFragment;
import com.example.fincost.Mascara.MaskTextWatcher;
import com.example.fincost.Mascara.SimpleMaskFormatter;
import com.example.fincost.Model.Movimentacao;
import com.example.fincost.Model.Receita;
import com.example.fincost.R;
import com.example.fincost.help.Preferencias;
import com.example.fincost.help.Resultado;
import com.example.fincost.usuario.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class ReceitasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private AutoCompleteTextView autoCompleteTextView;
    private Toolbar toolbar;
    private EditText editTextData, editTexDescricao;
    private CurrencyEditText editTextValor;
    private Button calendarioButton;

    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
    private Preferencias preferencias;
    private ValueEventListener valueEventListener;

    private String identificadorUsuario;
    private String dia, mes, ano;
    private Double receitaTotal = 0.0;
    private Double receitaMensal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        autoCompleteTextView = findViewById(R.id.textList);
        editTextData = findViewById(R.id.editTextDate);
        editTexDescricao = findViewById(R.id.editTextdescricao);
        editTextValor = findViewById(R.id.editTextValor);
        calendarioButton = findViewById(R.id.calendarioButton);

        preferencias = new Preferencias(ReceitasActivity.this);
        identificadorUsuario = preferencias.getIdentificador();

        String [] opcaoTipo = {"Receita Fixa", "Receita Variável"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.opcao, opcaoTipo);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        toolbar = findViewById(R.id.toolbarReceitas);
        toolbar.setTitle("Adicionar Receitas");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);


        atualizarDataAtual();
        recuperarReceitaTotal();
        recuperarReceitaMensal();

        calendarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

    }

    private void atualizarDataAtual(){
        Calendar c = Calendar.getInstance();
        c.get(Calendar.YEAR);
        c.get(Calendar.MONTH);
        c.get(Calendar.DATE);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        String[] splitDate = currentDate.split("de");
        String[] splitDay = splitDate[0].split(",");

        String diaAtual = splitDay[1].trim();
        String mesAtual = splitDate[1].trim();
        String anoAtual = splitDate[2].trim();
        int mesAtualM = c.get(Calendar.MONTH)+1;
        int diaAtualD = c.get(Calendar.DATE);
        int anoAtualY = c.get(Calendar.YEAR);

        if(diaAtualD < 10){
            diaAtualD = Integer.parseInt("0" + diaAtualD);
        }

        if (mesAtualM < 10) {

            editTextData.setText(diaAtualD + "/" + "0" + mesAtualM + "/" + anoAtualY);
        }else {
            editTextData.setText(diaAtualD + "/" + mesAtualM + "/" + anoAtualY);
        }
        dia = diaAtual;
        mes = mesAtual;
        ano = anoAtual;

    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cC = Calendar.getInstance();
        cC.set(Calendar.YEAR, year);
        cC.set(Calendar.MONTH, month);
        cC.set(Calendar.DATE, dayOfMonth);

        String currentDateC = DateFormat.getDateInstance(DateFormat.FULL).format(cC.getTime());
        String[] splitDateC = currentDateC.split("de");
        String[] splitDayC = splitDateC[0].split(",");

        String diaAtualC = splitDayC[1].trim();
        String mesAtualC = splitDateC[1].trim();
        String anoAtualC = splitDateC[2].trim();

        month = month + 1;

        if(dayOfMonth < 0){
            dayOfMonth = Integer.parseInt("0" + dayOfMonth);
        }

        if(month < 10){
            editTextData.setText(dayOfMonth + "/" + "0" +  month + "/" + year);
        }else{
        editTextData.setText(dayOfMonth + "/" +  month + "/" + year);
        }
        dia = diaAtualC;
        mes = mesAtualC;
        ano = anoAtualC;

        //Toast.makeText(getApplicationContext(), mesAtualC, Toast.LENGTH_SHORT).show();
    }

    public Boolean validarCampos(){

        Double valorReceita = Double.parseDouble(String.valueOf(editTextValor.getRawValue()));
        String descricaoReceita = editTexDescricao.getText().toString();

        if (valorReceita != 0){
            if (!descricaoReceita.isEmpty()){
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "Descrição não foi preenchida", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceitaTotal (){
        String idUsuario = identificadorUsuario;

        DatabaseReference usuarioRef = firebase.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuarios usuario = snapshot.getValue(Usuarios.class);
                    receitaTotal = usuario.getReceitaTotal();
                } else{
                    receitaTotal = 0.0;
                }

            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }
    public void recuperarReceitaMensal(){
        DatabaseReference usuarioRef = firebase.child("movimentoMensal").child(identificadorUsuario)
                .child(ano).child(mes);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Receita receita = snapshot.getValue(Receita.class);
                    receitaMensal = receita.getReceitaMensal();
                } else{
                    receitaMensal = 0.0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void atualizarReceita(Double receita){
        DatabaseReference usuarioRefe = firebase.child("usuarios").child(identificadorUsuario);
        usuarioRefe.child("receitaTotal").setValue(receita);

    }
    public void atualizarReceitamensal(Double receitaM){
        DatabaseReference usuarioRefer = firebase.child("movimentoMensal")
                .child(identificadorUsuario).child(ano).child(mes);
        usuarioRefer.child("receitaMensal").setValue(receitaM);
    }
    private void salvarReceita(){
        if(validarCampos()) {

            Double receitaAtualizada;
            Double receitaMensalAtualizada;


            Movimentacao movimentacao = new Movimentacao();
            Double valor = Double.parseDouble(String.valueOf(editTextValor.getRawValue()));
            Double somarValor = valor / 100;

            receitaAtualizada = receitaTotal + somarValor;
            receitaMensalAtualizada = receitaMensal + somarValor;


            movimentacao.setAno(ano);
            movimentacao.setCategoria("Receita");
            movimentacao.setData(editTextData.getText().toString());
            movimentacao.setDescricao(editTexDescricao.getText().toString());
            movimentacao.setIdUsuario(identificadorUsuario);
            movimentacao.setMes(mes);
            movimentacao.setValor(somarValor);
            movimentacao.setTipo(autoCompleteTextView.getText().toString());

            atualizarReceita(receitaAtualizada);
            atualizarReceitamensal(receitaMensalAtualizada);

            movimentacao.salvar(identificadorUsuario, ano, mes);

            finish();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_red, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.salvar:
                salvarReceita();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}