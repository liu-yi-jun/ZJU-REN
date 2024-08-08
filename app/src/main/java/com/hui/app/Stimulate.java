package com.hui.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hui.app.databinding.ActivityStimulateBinding;

public class Stimulate extends AppCompatActivity implements View.OnClickListener {

    private ActivityStimulateBinding binding;
    private Button startStimulate;
    private Button stimulateDoctorBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStimulateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onMyBackPressed(true, new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent();
//                intent.putExtra("data_return", "Hello FirstActivity");
//                setResult(RESULT_OK, intent);
                finish();
            }
        });
        initView();
    }

    private void onMyBackPressed(boolean isEnable, final Runnable callback) {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(isEnable) {
            @Override
            public void handleOnBackPressed() {
                callback.run();
            }
        });
    }

    private void initView() {
        startStimulate = binding.startStimulate;
        stimulateDoctorBtn = binding.stimulateDoctorBtn;
        backBtn = binding.backBtn;
        startStimulate.setOnClickListener(this);
        stimulateDoctorBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stimulate_doctor_btn:
                Intent intent = new Intent(Stimulate.this, DoctorMode.class);
                startActivity(intent);
                break;
            case  R.id.back_btn:
                onBackPressed();
                break;
        }
    }
}