package com.example.unischeduler.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unischeduler.Activities.AccountActivity;
import com.example.unischeduler.Activities.LoginActivity;
import com.example.unischeduler.R;

import java.util.ArrayList;

public class InputField2Fragment extends Fragment {

    private static final String TAG = "InputField2Fragment";

    private Context context;

    public EditText etFirst;
    public AutoCompleteTextView etSecond;
    public TextView tvChange;

    private ArrayList<String> unis;

    public InputField2Fragment(Context context, ArrayList<String> unis) {
        this.context = context;
        this.unis = unis;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_inputfields_2, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFirst = (EditText) view.findViewById(R.id.etFirst);
        etSecond = (AutoCompleteTextView) view.findViewById(R.id.etSecond);
        tvChange = (TextView) view.findViewById(R.id.tvChange);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner, R.id.item, unis);
        etSecond.setAdapter(adapter);
        etSecond.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.background_input_field));
        etSecond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "clicked " + parent.getItemAtPosition(position));
            }
        });

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }
}
