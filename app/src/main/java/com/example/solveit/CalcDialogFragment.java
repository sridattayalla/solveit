package com.example.solveit;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CalcDialogFragment extends DialogFragment {
    String packageName = "com.android.whatsapp", activityName = null;
    TextView sumTextview;
    EditText answerEditText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calc_dialog_fragment, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        packageName = getArguments().getString("packageName");
        activityName = getArguments().getString("activityName");

        Random rand = new Random();
        //setting random numbers
        int operandsCount = 2 + rand.nextInt(2);
        ArrayList<Integer> operands = new ArrayList<>();
        int res = 0;
        for (int i=0; i<operandsCount; i++){
            int temp = 11+ rand.nextInt(99);
             operands.add(temp);
             res += temp;
        }
        final String result = Integer.toString(res);

        String sumString = "";
        for (int i=0; i<operandsCount-1; i++){
            sumString += operands.get(i).toString() + " + ";
        }
        sumString += operands.get(operands.size() - 1).toString();

        //initializing ui
        sumTextview = v.findViewById(R.id.sum_tv);
        answerEditText = v.findViewById(R.id.answer_et);

        //setting focus
        if(answerEditText.requestFocus()) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        //setting sum
        sumTextview.setText(sumString);

        answerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(result.equals(charSequence.toString())){
                    dismiss();
                    openApp();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    void openApp(){
        ComponentName name=new ComponentName(packageName,
                            activityName);
        Intent i=new Intent(Intent.ACTION_MAIN);

        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.setComponent(name);
        startActivity(i);
    }
}
