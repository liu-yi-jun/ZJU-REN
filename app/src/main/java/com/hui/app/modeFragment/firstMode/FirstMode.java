package com.hui.app.modeFragment.firstMode;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hui.app.R;
import com.hui.app.databinding.FragmentFirstModeBinding;
import com.hui.app.define.PulseDefine;
import com.hui.app.model.DoctorModel;
import com.hui.app.utils.TimeUtil;

public class FirstMode extends Fragment implements View.OnClickListener {

    private FragmentFirstModeBinding binding;
    private EditText amplitude;
    private EditText frequency;
    private EditText pulse_width;
    private EditText time;
    private DoctorModel doctorModel;
    private ImageView plus;
    private ImageView minus;
    private PulseDefine firstMode;

    public static FirstMode newInstance() {
        return new FirstMode();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        doctorModel = new ViewModelProvider(getActivity()).get(DoctorModel.class);
        binding = FragmentFirstModeBinding.inflate(inflater, container, false);
        initView();
        initModel();
        return binding.getRoot();
    }

    private void initModel() {
        Gson gson = new Gson();
        // 读取SharedPreferences中的JSON字符串
        SharedPreferences firstSharedPreferences = getActivity().getSharedPreferences("firstMode", Context.MODE_PRIVATE);
        String firstJson= firstSharedPreferences.getString("firstMode", "");
        firstMode = gson.fromJson(firstJson, PulseDefine.class);
        if (firstMode != null) {
            amplitude.setText(firstMode.getAmplitude());
            frequency.setText(firstMode.getFrequency());
            pulse_width.setText(firstMode.getPulseWidth());
            time.setText(firstMode.getTime());
        }
    }

    private void initView() {
        amplitude = binding.amplitude;
        frequency = binding.frequency;
        pulse_width = binding.pulseWidth;
        time = binding.time;
        plus = binding.plus;
        minus = binding.minus;
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        amplitude.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        frequency.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        pulse_width.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        time.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        amplitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doctorModel.getFirstMode().setAmplitude(s.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        frequency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doctorModel.getFirstMode().setFrequency(s.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        pulse_width.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doctorModel.getFirstMode().setPulseWidth(s.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doctorModel.getFirstMode().setTime(s.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    @Override
    public void onClick(View view) {
        int num;
        switch (view.getId()) {
            case R.id.plus:
                if(amplitude.getText().toString().isEmpty()) {
                    num = 0;
                }else {
                    num = Integer.parseInt(amplitude.getText().toString());
                }
                num++;
                if (num > 400) {
                    Toast.makeText(getActivity(), "幅度最大不大于400", Toast.LENGTH_SHORT).show();
                    return;
                }
                amplitude.setText(String.valueOf(num));
                break;
            case R.id.minus:
                if(amplitude.getText().toString().isEmpty()) {
                    num = 0;
                }else {
                    num = Integer.parseInt(amplitude.getText().toString());
                }
                num--;
                if (num < 0) {
                    Toast.makeText(getActivity(), "幅度最小不少于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                amplitude.setText(String.valueOf(num));
        }
    }
}