package com.example.fincost.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.fincost.Config.ConfiguracaoFirebase;
import com.example.fincost.Config.DataPickerFragment;
import com.example.fincost.Model.Movimentacao;
import com.example.fincost.Model.Receita;
import com.example.fincost.R;
import com.example.fincost.help.Preferencias;
import com.example.fincost.usuario.Usuarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class DespesasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private AutoCompleteTextView autoCompleteTextView;
    private EditText editTextData, editTextDescricao;
    private CurrencyEditText editTextValor;
    private Button buttonCalendario;
    private Toolbar toolbar;

    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
    private ValueEventListener valueEventListener;
    private Preferencias preferencias;

    private String dia, mes, ano;
    private String identificadorUsuario;
    private Double despesaMensal = 0.0;
    private Double despesaTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        editTextData = findViewById(R.id.editTextDateD);
        editTextDescricao = findViewById(R.id.editTextdescricaoD);
        editTextValor = findViewById(R.id.editTextValorD);
        buttonCalendario = findViewById(R.id.calendarioButtonD);

        autoCompleteTextView = findViewById(R.id.textListD);
        String [] opcaoTipo = {"Despesa Fixa", "Despesa Variável"};
        //Adapter o autocompleteTextView
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.opcao, opcaoTipo);
        autoCompleteTextView.setText(arrayAdapter.getItem(0).toString(), false);
        autoCompleteTextView.setAdapter(arrayAdapter);

        //Recuperar identificador do usuário
        preferencias = new Preferencias(DespesasActivity.this);
        identificadorUsuario = preferencias.getIdentificador();

        //Configurando o toolbar
        toolbar = findViewById(R.id.toolbarDespesas);
        toolbar.setTitle("Adicionar Despesas");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);

        atualizarDataAtual();
        recuperarDespesaTotal();
        recuperarDespesaMensal();

        buttonCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

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
        } else{

        editTextData.setText(dayOfMonth + "/" +  month + "/" + year);
        }

        dia = diaAtualC;
        mes = mesAtualC;
        ano = anoAtualC;
    }

    //Atualizar a data do dia atual
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

        if (mesAtualM < 10){
            editTextData.setText(diaAtualD + "/" + "0" + mesAtualM + "/" + anoAtualY);
        } else{
        editTextData.setText(diaAtualD + "/" + mesAtualM + "/" + anoAtualY);
        }
        dia = diaAtual;
        mes = mesAtual;
        ano = anoAtual;

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
                salvarDespesa();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public Boolean validarCampos(){
        Double valorReceita = Double.parseDouble(String.valueOf(editTextValor.getRawValue()));
        String descricaoReceita = editTextDescricao.getText().toString();

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
    public void recuperarDespesaTotal (){
        String idUsuario = identificadorUsuario;

        DatabaseReference usuarioRef = firebase.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuarios usuario = snapshot.getValue(Usuarios.class);
                    despesaTotal = usuario.getDespesaTotal();
                } else {
                    despesaTotal = 0.0;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void recuperarDespesaMensal(){
        DatabaseReference usuarioRef = firebase.child("movimentoMensal").child(identificadorUsuario)
                .child(ano).child(mes);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                Receita receita = snapshot.getValue(Receita.class);
                despesaMensal = receita.getDespesaMensal();
                } else {
                    despesaMensal = 0.0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void atualizarDespesa(Double despesa){
        DatabaseReference usuarioRefe = firebase.child("usuarios").child(identificadorUsuario);
        usuarioRefe.child("despesaTotal").setValue(despesa);

    }
    public void atualizarDespesamensal(Double despesaM){
        DatabaseReference usuarioRefer = firebase.child("movimentoMensal")
                .child(identificadorUsuario).child(ano).child(mes);
        usuarioRefer.child("despesaMensal").setValue(despesaM);
    }
    private void salvarDespesa(){
        if(validarCampos()) {

            Double despesaAtualizada;
            Double despesaMensalAtualizada;

            Movimentacao movimentacao = new Movimentacao();
            Double valor = Double.parseDouble(String.valueOf(editTextValor.getRawValue()));
            Double somarValor = valor / 100;

            despesaAtualizada = despesaTotal + somarValor;
            despesaMensalAtualizada = despesaMensal + somarValor;


            movimentacao.setAno(ano);
            movimentacao.setCategoria("Despesa");
            movimentacao.setData(editTextData.getText().toString());
            movimentacao.setDescricao(editTextDescricao.getText().toString());
            movimentacao.setIdUsuario(identificadorUsuario);
            movimentacao.setMes(mes);
            movimentacao.setValor(somarValor);
            movimentacao.setTipo(autoCompleteTextView.getText().toString());

            atualizarDespesa(despesaAtualizada);
            atualizarDespesamensal(despesaMensalAtualizada);

            movimentacao.salvar(identificadorUsuario, ano, mes);

            finish();


        }
    }
}
