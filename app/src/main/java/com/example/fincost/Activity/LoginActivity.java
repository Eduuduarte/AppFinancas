package com.example.fincost.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fincost.Config.ConfiguracaoFirebase;
import com.example.fincost.R;
import com.example.fincost.help.Base64Custom;
import com.example.fincost.help.Preferencias;
import com.example.fincost.usuario.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button buttonCriar;
    private Button buttonEntrar;
    private EditText textEmail;
    private EditText textSenha;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;
    private String identificadorUsuarioLogado;
    private Usuarios usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        recuperarUsuario();

        buttonCriar = findViewById(R.id.buttonCriar);
        buttonEntrar = findViewById(R.id.buttonEntrar);
        textEmail = findViewById(R.id.textEmail);
        textSenha = findViewById(R.id.textSenha);

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuarios = new Usuarios();
                usuarios.setEmail(textEmail.getText().toString());
                usuarios.setSenha(textSenha.getText().toString());
                longarUsuario();
            }
        });


        buttonCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastrarUsuario.class);
                startActivity(intent);
            }
        });
    }
    private void longarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuarios.getEmail(),
                usuarios.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete( Task<AuthResult> task) {
                if(task.isSuccessful()){
                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuarios.getEmail());

                    firebase = ConfiguracaoFirebase.getFirebase()
                            .child("usuarios")
                            .child(identificadorUsuarioLogado);
                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot snapshot) {
                            Usuarios usuarioRecuperado = snapshot.getValue(Usuarios.class);

                            Preferencias preferencias = new Preferencias(LoginActivity.this);
                            preferencias.salvarDados(identificadorUsuarioLogado, usuarioRecuperado.getNome());

                        }

                        @Override
                        public void onCancelled(  DatabaseError error) {

                        }
                    };
                    firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);

                    abrirTelaPrincipal();



                }else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        erroExcecao = "Email Inválido";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Senha não corresponde ao email digitado";
                    }catch (Exception e){
                        erroExcecao = "Erro ao tentar entrar na sua conta. Por favor" +
                                "verifique se o email e senha estão digitados corretamente";
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, erroExcecao, Toast.LENGTH_LONG).show();

                }
            }
        });



    }
    private void abrirTelaPrincipal(){
        Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }
    private void recuperarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }
}