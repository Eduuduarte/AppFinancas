package com.example.fincost.Config;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fincost.R;

import java.util.Calendar;

public class DataPickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DATE);


        return new DatePickerDialog(getActivity(),
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                (DatePickerDialog.OnDateSetListener) getActivity(),
                year, mes, dia);
    }

    public DataPickerFragment() {
    }
}
