package com.example.fincost.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fincost.Config.ConfiguracaoFirebase;
import com.example.fincost.Model.Renda;
import com.example.fincost.R;
import com.example.fincost.help.Preferencias;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView textNomeUsuario;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListener;
    private FirebaseAuth autenticação;

    private Toolbar toolbar;
    private FloatingActionButton flReceitas, flDespesas, flInvestimentos;

    private String idUsuario;
    private String nomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textNomeUsuario = findViewById(R.id.textNomeUsuario);
        flReceitas = findViewById(R.id.flReceitas);
        flDespesas = findViewById(R.id.flDespesas);
        flInvestimentos = findViewById(R.id.flInvestimento);


        Preferencias preferencias = new Preferencias(MainActivity.this);
        idUsuario = preferencias.getIdentificador();
        nomeUsuario = preferencias.getNome();
        textNomeUsuario.setText("Olá, " + nomeUsuario);

        autenticação = ConfiguracaoFirebase.getFirebaseAutenticacao();


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


    }

    private void adicionarDadosRenda(String idUsuarioLogado, Renda renda){
        firebase = ConfiguracaoFirebase.getFirebase().child("renda");
        firebase.child(idUsuarioLogado).setValue(renda);
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

}