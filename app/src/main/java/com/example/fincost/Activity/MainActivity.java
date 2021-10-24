package com.example.fincost.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fincost.Adapter.MovimentoAdapter;
import com.example.fincost.Config.ConfiguracaoFirebase;
import com.example.fincost.Model.InvestimentoTotal;
import com.example.fincost.Model.Movimentacao;
import com.example.fincost.Model.Renda;
import com.example.fincost.R;
import com.example.fincost.help.Preferencias;
import com.example.fincost.usuario.Usuarios;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textNomeUsuario;
    //private TextView textDataAtual;
    private TextView valorReceita, valorDespesa, valorSaldo, valorInvestimento;
    private TextView textReceita, textDespesa, textSaldo;
    private ListView lvMovimento;
    private ArrayList <Movimentacao> movimentacao;
    private ArrayAdapter <Movimentacao> adapter;
    private CardView cardViewInvestimento;
    private SwipeRefreshLayout refreshLayout;

    private DecimalFormat df;

    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
    private ValueEventListener valueEventListener;
    private FirebaseAuth autenticação;

    private Toolbar toolbar;
    private FloatingActionButton flReceitas, flDespesas, flInvestimentos;

    private String idUsuario;
    private String nomeUsuario;
    private Double receitaValor = 0.0;
    private Double despesaValor = 0.0;
    private Double saldoValor = 0.0;
    private Double investimeValor = 0.0;
    private String ano, mes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textDataAtual = findViewById(R.id.textDataAtual);
        textNomeUsuario = findViewById(R.id.textNomeUsuario);
        flReceitas = findViewById(R.id.flReceitas);
        flDespesas = findViewById(R.id.flDespesas);
        flInvestimentos = findViewById(R.id.flInvestimento);
        cardViewInvestimento = findViewById(R.id.cardViewInvestimento);
        textReceita = findViewById(R.id.textReceitaTotal);
        textDespesa = findViewById(R.id.textDespesaTotal);
        textSaldo = findViewById(R.id.textSaldoTotal);
        valorReceita = findViewById(R.id.valorReceitaTotal);
        valorDespesa = findViewById(R.id.valorDespesaTotal);
        valorSaldo = findViewById(R.id.valorSaldoTotal);
        valorInvestimento = findViewById(R.id.valorInvestimentoTotalP);
        lvMovimento = findViewById(R.id.lvMovimentacao);
        valorInvestimento.setText("R$ " + investimeValor);
        refreshLayout = findViewById(R.id.refreshlayout);

        Preferencias preferencias = new Preferencias(MainActivity.this);
        idUsuario = preferencias.getIdentificador();
        nomeUsuario = preferencias.getNome();
        textNomeUsuario.setText("Olá, " + nomeUsuario);

        autenticação = ConfiguracaoFirebase.getFirebaseAutenticacao();

        atualizarDataAtual();
        recuperarInvestimento();
        recuperrValorTotal();


        //Listando Movimentacão
        movimentacao = new ArrayList<>();
        adapter = new MovimentoAdapter(MainActivity.this, movimentacao);
        lvMovimento.setAdapter(adapter);

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("Movimentacao")
                .child(idUsuario);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // Limpar lista
                movimentacao.clear();

                if(snapshot.exists()) {

                    for (DataSnapshot dados : snapshot.getChildren()) {
                        Movimentacao movimentacao1 = dados.getValue(Movimentacao.class);
                        movimentacao.add(movimentacao1);

                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        firebase.addValueEventListener(valueEventListener);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Limpar lista
                        movimentacao.clear();

                        if(snapshot.exists()) {

                            for (DataSnapshot dados : snapshot.getChildren()) {
                                Movimentacao movimentacao1 = dados.getValue(Movimentacao.class);
                                movimentacao.add(movimentacao1);

                            }

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                firebase.addValueEventListener(valueEventListener);
                refreshLayout.setRefreshing(false);
            }
        });



       toolbar = findViewById(R.id.toolbarUm);
       toolbar.setTitle("FinCost");
       setSupportActionBar(toolbar);


       flReceitas.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               abrirReceitas();
           }
       });
       flDespesas.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               abrirDespesas();
           }
       });
       flInvestimentos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               abrirInvestimentos();
           }
       });

       cardViewInvestimento.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               abrirMeusInvestimento();
           }
       });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.filtroMes:
                Toast.makeText(getApplicationContext(), "Filtrar mês", Toast.LENGTH_LONG).show();
                return true;
            case R.id.sair:
                deslongarUsuario();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void deslongarUsuario() {
        autenticação.signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
    private void abrirReceitas(){
        Intent intent = new Intent(MainActivity.this, ReceitasActivity.class);
        startActivity(intent);
    }

    private void abrirDespesas(){
        Intent intent = new Intent(MainActivity.this, DespesasActivity.class);
        startActivity(intent);
    }

    private void abrirInvestimentos(){
        Intent intent = new Intent(MainActivity.this, InvestimentosActivity.class);
        startActivity(intent);
    }
    private void abrirMeusInvestimento(){
        Intent intent = new Intent(MainActivity.this, MeusInvestimentos.class);
        startActivity(intent);
    }

    public void atualizarDataAtual(){
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


       ano = anoAtual;
       mes = "julho";
    }
    public void recuperrValorTotal(){
        String identificadorUsuario = idUsuario;

        DatabaseReference usuarioRef = firebase.child("usuarios").child(identificadorUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuarios usuario = snapshot.getValue(Usuarios.class);
                    receitaValor = usuario.getReceitaTotal();
                    despesaValor = usuario.getDespesaTotal();
                    saldoValor = receitaValor - despesaValor;

                    DecimalFormat df = new DecimalFormat("#,##0.##");
                    String resultadoSaldo = df.format(saldoValor);
                    String resultadoreceita = df.format(receitaValor);
                    String resultadoDespesa = df.format(despesaValor);


                    valorReceita.setText("R$ " + resultadoreceita);
                    valorDespesa.setText("R$ " + resultadoDespesa);
                    valorSaldo.setText("R$ " + resultadoSaldo);

                }

            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
    public void recuperarValorMensal(){

    }
    public void recuperarInvestimento(){
        DatabaseReference investirRef = firebase.child("InvestimentoTotal").child(idUsuario);

        investirRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    InvestimentoTotal investimentoTotal = snapshot.getValue(InvestimentoTotal.class);
                    investimeValor = investimentoTotal.getInvestimentoTotal();

                    DecimalFormat dfo = new DecimalFormat("#,##0.##");
                    String resultadoInvestimento = dfo.format(investimeValor);

                    valorInvestimento.setText("R$ " + resultadoInvestimento);


                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListener);
    }
}