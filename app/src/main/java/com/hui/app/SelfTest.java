package com.hui.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hui.app.databinding.ActivityScanBluetoothBinding;
import com.hui.app.databinding.ActivitySelfTestBinding;

public class SelfTest extends AppCompatActivity implements View.OnClickListener {

    private ActivitySelfTestBinding binding;
    private Button stimulateBtn;
    private Button doctorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelfTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBar();
        initView();
    }

    private void initView() {
        stimulateBtn = binding.stimulateBtn;
        doctorBtn = binding.doctorBtn;
        //        点击刺激按钮
        stimulateBtn.setOnClickListener(this);
        doctorBtn.setOnClickListener(this);
    }

    private void initBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        Toolbar toolbar = binding.scanToolbar.getRoot();
        TextView toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  //设置隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置返回
        toolbarTitle.setText("Mesh Mi Switch");
    }

    // 处理向上按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 这里可以添加你的逻辑，比如调用onBackPressed()
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stimulate_btn:
                Intent intent = new Intent(SelfTest.this, Stimulate.class);
                startActivity(intent);
                break;
        }
        switch (view.getId()) {
            case R.id.doctorBtn:
                Intent intent = new Intent(SelfTest.this, DoctorMode.class);
                startActivity(intent);
                break;
        }
    }
}