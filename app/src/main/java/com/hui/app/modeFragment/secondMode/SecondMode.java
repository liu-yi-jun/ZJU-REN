package com.hui.app.modeFragment.secondMode;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hui.app.databinding.FragmentSecondModeBinding;

public class SecondMode extends Fragment {

    private SecondModeViewModel mViewModel;
    private FragmentSecondModeBinding binding;

    public static SecondMode newInstance() {
        return new SecondMode();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SecondModeViewModel.class);
        binding = FragmentSecondModeBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }


    private void initView() {
    }

}