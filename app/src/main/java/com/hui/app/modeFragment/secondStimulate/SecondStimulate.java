package com.hui.app.modeFragment.secondStimulate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.hui.app.databinding.FragmentSecondStimulateBinding;
import com.hui.app.define.PulseDefine;
import com.hui.app.model.DoctorModel;

public class SecondStimulate  extends Fragment {
    private FragmentSecondStimulateBinding binding;
    private TextView amplitudeValue;
    private TextView betweenValue;
    private TextView pulseWidthValue;
    private TextView timeValue;
    private TextView intraValue;
    private TextView pulseNumberValue;
    private PulseDefine secondMode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DoctorModel doctorModel = new ViewModelProvider(getActivity()).get(DoctorModel.class);
        binding = FragmentSecondStimulateBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        initModel();
        super.onResume();
    }


    private void initView() {
        amplitudeValue = binding.amplitudeValue;
        betweenValue = binding.betweenValue;
        intraValue = binding.intraValue;
        pulseWidthValue = binding.pulseWidthValue;
        pulseNumberValue = binding.pulseNumberValue;
        timeValue = binding.timeValue;
    }
    private void initModel() {
        Gson gson = new Gson();
        // 读取SharedPreferences中的JSON字符串
        SharedPreferences firstSharedPreferences = getActivity().getSharedPreferences("secondMode", Context.MODE_PRIVATE);
        String firstJson= firstSharedPreferences.getString("secondMode", "");
        secondMode = gson.fromJson(firstJson, PulseDefine.class);
        if (secondMode != null) {
            amplitudeValue.setText(String.format("%.1f mA",(float)  Integer.parseInt(secondMode.getAmplitude().isEmpty()?"0":secondMode.getAmplitude()) / 10));
            betweenValue.setText(String.format("%d Hz",  Integer.parseInt(secondMode.getBetween().isEmpty()?"0":secondMode.getBetween())));
            intraValue.setText(String.format("%d Hz",  Integer.parseInt(secondMode.getIntra().isEmpty()?"0":secondMode.getIntra())));
            pulseWidthValue.setText(String.format("%d µs", Integer.parseInt(secondMode.getPulseWidth().isEmpty()?"0":secondMode.getPulseWidth())));
            pulseNumberValue.setText(String.format("%d", Integer.parseInt(secondMode.getPulseNumber().isEmpty()?"0":secondMode.getPulseNumber())));
            timeValue.setText(String.format("%d s", Integer.parseInt(secondMode.getTime().isEmpty()?"0":secondMode.getTime())));

        }
    }
}
