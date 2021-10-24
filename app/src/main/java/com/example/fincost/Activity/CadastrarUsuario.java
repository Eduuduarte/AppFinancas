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
import com.example.fincost.Model.Receita;
import com.example.fincost.R;
import com.example.fincost.help.Base64Custom;
import com.example.fincost.help.Preferencias;
import com.example.fincost.usuario.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.Calendar;

public class CadastrarUsuario extends AppCompatActivity {

    private Button buttonCadastrar;
    private EditText textNome;
    private EditText textSenha;
    private EditText textEmail;
    private Usuarios usuarios;
    private Receita receita;

    private DatabaseReference firebase;
    private FirebaseAuth autenticacao;

    private String mes;
    private String ano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        textNome = findViewById(R.id.textNome);
        textEmail = findViewById(R.id.textEmailC);
        textSenha = findViewById(R.id.textSenhaC);

        atualizarDataAtual();
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarios = new Usuarios();
                usuarios.setNome(textNome.getText().toString());
                usuarios.setEmail(textEmail.getText().toString());
                usuarios.setSenha(textSenha.getText().toString());

                receita = new Receita();
                receita.setAno(ano);
                receita.setMes(mes);

                cadastrarUsuario();
            }
        });

    }
    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuarios.getEmail(),
                usuarios.getSenha()
        ).addOnCompleteListener(CadastrarUsuario.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Cadastro feito como sucesso", Toast.LENGTH_SHORT )
                            .show();
                    FirebaseUser usuarioFirebase = task.getResult().getUser();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuarios.getEmail());
                    usuarios.setId(identificadorUsuario);
                    usuarios.salvar();

                    receita.setId(identificadorUsuario);
                    receita.salvarReceitaMensal();


                    Preferencias preferencias = new Preferencias(CadastrarUsuario.this);
                    preferencias.salvarDados(identificadorUsuario, usuarios.getNome());



                    AbrirLogin();


                }else{
                    //Tratando erros do cadastro
                    String erroExcecao = "";
                    try {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha contendo oito ou mais caracteres";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Email digitado é inválido, por favor tente outro email";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "Esse email já está em uso no APP";
                    }catch (Exception e){
                        erroExcecao = "Ao cadastrar usuário";
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Erro: " + erroExcecao , Toast.LENGTH_SHORT )
                            .show();
                }
            }
        });
    }

    public void AbrirLogin (){
        autenticacao.signOut();
        Intent intent = new Intent(CadastrarUsuario.this, LoginActivity.class);
        startActivity(intent);
        finish();
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

        mes = mesAtual;
        ano = anoAtual;

    }


}