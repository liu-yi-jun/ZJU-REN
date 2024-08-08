package com.hui.app.modeFragment.firstMode;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hui.app.databinding.FragmentFirstModeBinding;

public class FirstMode extends Fragment {

    private FirstModeViewModel mViewModel;
    private FragmentFirstModeBinding binding;

    public static FirstMode newInstance() {
        return new FirstMode();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(FirstModeViewModel.class);
        binding = FragmentFirstModeBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
    }


}