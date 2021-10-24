package com.example.fincost.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.fincost.Config.ConfiguracaoFirebase;
import com.example.fincost.Mascara.MaskTextWatcher;
import com.example.fincost.Mascara.SimpleMaskFormatter;
import com.example.fincost.Model.InvestimentoTotal;
import com.example.fincost.Model.Investimentos;
import com.example.fincost.Model.Movimentacao;
import com.example.fincost.R;
import com.example.fincost.help.Preferencias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;


public class ResgatarFragment extends Fragment {

    private EditText editTextDateinvestirR;
    private CurrencyEditText editTextInvestirR;
    private AutoCompleteTextView textListInvestirR;
    private AutoCompleteTextView textListModalidadeR;
    private Button buttonInvestirR;
    private Button calendarioInvestirR;
    private EditText editTextDescricao;

    private String dia, mes, ano;
    private String identificadorUsuario;
    private Double investimentoTotal = 0.0;
    private Double valorModalidade = 0.0;
    private String [] opcaoModalidade;

    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
    private ValueEventListener valueEventListener;

    private Preferencias preferencias;


    public ResgatarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resgatar, container, false);

        editTextDateinvestirR = view.findViewById(R.id.editTextDateInvestirR);
        editTextInvestirR = view.findViewById(R.id.editTextInvestR);
        editTextDescricao = view.findViewById(R.id.editTextdescricaoInvestR);
        buttonInvestirR = view.findViewById(R.id.buttonInvestirR);
        textListInvestirR = view.findViewById(R.id.textListInvestirR);
        textListModalidadeR = view.findViewById(R.id.textListModalidadeR);
        calendarioInvestirR = view.findViewById(R.id.calendarioInvestirR);

        atualizarDataAtual();

        preferencias = new Preferencias(getActivity());
        identificadorUsuario = preferencias.getIdentificador();

        String [] opcaoTipo = {"Renda Fixa", "Renda Variável", "Tesouro Direto"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.opcao, opcaoTipo);
        textListInvestirR.setText(arrayAdapter.getItem(0).toString(), false);
        textListInvestirR.setAdapter(arrayAdapter);

        opcaoModalidade  = new String[]{"CDB", "LCI", "CRI", "Debênture"};
        ArrayAdapter arrayAdapterM = new ArrayAdapter(getActivity(), R.layout.opcao, opcaoModalidade);
        textListModalidadeR.setText(arrayAdapterM.getItem(0).toString(), false);
        textListModalidadeR.setAdapter(arrayAdapterM);

        recuperarInvestimento();
        recuperarModalidade();

        textListInvestirR.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pos = textListInvestirR.getAdapter().getItem(position).toString();

                switch (pos){
                    case "Renda Fixa":
                        opcaoModalidade  = new String[]{"CDB", "LCI", "CRI", "Debênture"};
                        break;
                    case "Renda Variável":
                        opcaoModalidade = new String[]{"Ações", "ETFs", "FII"};
                        break;
                    case "Tesouro Direto":
                        opcaoModalidade = new String[]{"Selic", "IPCA", "Prefixado"};
                        break;
                    default:
                        opcaoModalidade = new String []{"Escolha o tipo de investimento"};

                }
                ArrayAdapter arrayAdapter1 = new ArrayAdapter(getActivity(), R.layout.opcao, opcaoModalidade);
                textListModalidadeR.setText(arrayAdapter1.getItem(0).toString(), false);
                textListModalidadeR.setAdapter(arrayAdapter1);
                recuperarInvestimento();
                recuperarModalidade();
            }
        });

        textListModalidadeR.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recuperarModalidade();
            }
        });

        calendarioInvestirR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDateinvestirR.setEnabled(true);
                SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NN/NN/NNNN");
                MaskTextWatcher mascarData = new MaskTextWatcher(editTextDateinvestirR, simpleMaskFormatter);

                editTextDateinvestirR.addTextChangedListener(mascarData);
            }
        });

        buttonInvestirR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarInvestimento();
            }
        });


        return view;
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

        if(mesAtualM<10){
            editTextDateinvestirR.setText(diaAtualD + "/" + 0 + mesAtualM + "/" + anoAtualY);
        }else {
            editTextDateinvestirR.setText(diaAtualD + "/" + mesAtualM + "/" + anoAtualY);
        }


        dia = diaAtual;
        mes = mesAtual;
        ano = anoAtual;

    }
    public void recuperarInvestimento(){
        DatabaseReference investirRef = firebase.child("InvestimentoTotal").child(identificadorUsuario);

        investirRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    InvestimentoTotal investiT = snapshot.getValue(InvestimentoTotal.class);
                    investimentoTotal = investiT.getInvestimentoTotal();
                } else{
                    investimentoTotal = 0.0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recuperarModalidade(){
        String textTipo = textListInvestirR.getText().toString();
        String textModalidade = textListModalidadeR.getText().toString();

        DatabaseReference investirMRef = firebase.child("Investimento").child(identificadorUsuario).child(textTipo)
                .child(textModalidade);

        investirMRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Investimentos investimentosM = snapshot.getValue(Investimentos.class);
                    valorModalidade = investimentosM.getValor();
                } else{
                    valorModalidade = 0.0;
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    public void atualizarInvestimentoTotal(Double investirTotal){
        DatabaseReference investir = firebase.child("InvestimentoTotal").child(identificadorUsuario);
        investir.child("investimentoTotal").setValue(investirTotal);
    }
    public void atualizarInvestModalidade(String tipoI, String modaI, Double valorM){
        DatabaseReference modalI = firebase.child("Investimento").child(identificadorUsuario)
                .child(tipoI).child(modaI);
        modalI.child("valor").setValue(valorM);
    }

    public void salvarInvestimento (){
        Double investimentoToAtual;
        Double valorMoAtual;

        recuperarInvestimento();
        recuperarModalidade();

        String tipoTextI = textListInvestirR.getText().toString();
        String modalText = textListModalidadeR.getText().toString();
        String descricao = editTextDescricao.getText().toString();
        String data = editTextDateinvestirR.getText().toString();

        Movimentacao movimentacao = new Movimentacao();
        Double valor = Double.parseDouble(String.valueOf(editTextInvestirR.getRawValue()));
        Double somarValor = valor/100;

        investimentoToAtual = investimentoTotal - somarValor;
        valorMoAtual = valorModalidade - somarValor;

        movimentacao.setCategoria("Investimento");
        movimentacao.setTipo(modalText);
        movimentacao.setValor(somarValor);
        movimentacao.setMes(mes);
        movimentacao.setIdUsuario(identificadorUsuario);
        movimentacao.setDescricao(descricao);
        movimentacao.setData(data);
        movimentacao.setAno(ano);

        atualizarInvestimentoTotal(investimentoToAtual);
        atualizarInvestModalidade(tipoTextI, modalText, valorMoAtual);

        movimentacao.salvar(identificadorUsuario, ano, mes);

        editTextDescricao.setText("");
        editTextInvestirR.setValue(0);

    }
}