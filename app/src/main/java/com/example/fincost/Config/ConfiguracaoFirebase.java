package com.example.fincost.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference firebaseReference;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase(){

        if(firebaseReference == null){
            firebaseReference = FirebaseDatabase.getInstance().getReference();
        }
        
        return firebaseReference;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){

        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

}
