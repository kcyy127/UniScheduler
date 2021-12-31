package com.example.unischeduler.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unischeduler.Activities.LoginActivity;
import com.example.unischeduler.R;

public class InputField1Fragment extends Fragment {

    private static final String TAG = "InputField1Fragment";

    private Context context;

    public EditText etFirst, etSecond;
    public TextView tvChange;

    public InputField1Fragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_inputfields_1, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFirst = (EditText) view.findViewById(R.id.etFirst);
        etSecond = (EditText) view.findViewById(R.id.etSecond);
        tvChange = (TextView) view.findViewById(R.id.tvChange);

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }

    public String getEmail() {
        return etFirst.getText().toString().trim();
    }
}
