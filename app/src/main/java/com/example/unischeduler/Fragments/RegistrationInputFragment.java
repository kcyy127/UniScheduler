package com.example.unischeduler.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.unischeduler.Activities.LoginActivity;
import com.example.unischeduler.R;
import com.example.unischeduler.databinding.FragmentInputfields1Binding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationInputFragment extends Fragment {

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;
    public static final int INPUT_TYPE_1 = 63;
    public static final int INPUT_TYPE_2 = 64;

    private FragmentInputfields1Binding binding;

    private Context context;

    private int inputType;

    public EditText etFirst, etSecond;

    public RegistrationInputFragment(Context context, int inputType) {
        this.context = context;
        this.inputType = inputType;
    }

    public RegistrationInputFragment() {}

    public static RegistrationInputFragment newInstance(String param1, String param2) {
        RegistrationInputFragment fragment = new RegistrationInputFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputfields_1, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFirst = view.findViewById(R.id.etFirst);
        etSecond = view.findViewById(R.id.etSecond);

        TextView tvChange = view.findViewById(R.id.tvChange);
        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        switch (inputType) {
            case INPUT_TYPE_1:
                etFirst.setHint("Email");
                etSecond.setHint("Password");
                etSecond.setTransformationMethod(new PasswordTransformationMethod());
                break;
            case INPUT_TYPE_2:
                etFirst.setHint("Full name");
                etFirst.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_account, 0, 0, 0);
                etSecond.setHint("University");
                etSecond.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_university, 0, 0, 0);
                break;
        }
    }
}