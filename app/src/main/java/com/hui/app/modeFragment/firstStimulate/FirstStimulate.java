package com.hui.app.modeFragment.firstStimulate;

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
import com.hui.app.databinding.FragmentFirstModeBinding;
import com.hui.app.databinding.FragmentFirstStimulateBinding;
import com.hui.app.define.PulseDefine;
import com.hui.app.model.DoctorModel;

public class FirstStimulate  extends Fragment {

    private FragmentFirstStimulateBinding binding;
    private TextView amplitudeValue;
    private TextView frequencyValue;
    private TextView pulseWidthValue;
    private TextView timeValue;
    private PulseDefine firstMode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DoctorModel doctorModel = new ViewModelProvider(getActivity()).get(DoctorModel.class);
        binding = FragmentFirstStimulateBinding.inflate(inflater, container, false);
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
        frequencyValue = binding.frequencyValue;
        pulseWidthValue = binding.pulseWidthValue;
        timeValue = binding.timeValue;
    }
    private void initModel() {
        Gson gson = new Gson();
        // 读取SharedPreferences中的JSON字符串
        SharedPreferences firstSharedPreferences = getActivity().getSharedPreferences("firstMode", Context.MODE_PRIVATE);
        String firstJson= firstSharedPreferences.getString("firstMode", "");
        firstMode = gson.fromJson(firstJson, PulseDefine.class);
        if (firstMode != null) {
            amplitudeValue.setText(String.format("%.1f mA", (float) Integer.parseInt(firstMode.getAmplitude().isEmpty()?"0":firstMode.getAmplitude()) / 10));
            frequencyValue.setText(String.format("%d Hz",  Integer.parseInt(firstMode.getFrequency().isEmpty()?"0":firstMode.getFrequency())));
            pulseWidthValue.setText(String.format("%d µs", Integer.parseInt(firstMode.getPulseWidth().isEmpty()?"0":firstMode.getPulseWidth())));
            timeValue.setText(String.format("%d s", Integer.parseInt(firstMode.getTime().isEmpty()?"0":firstMode.getTime())));
        }
    }

}
