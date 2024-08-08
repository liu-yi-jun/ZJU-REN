package com.hui.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hui.app.databinding.ActivityScanBluetoothBinding;

public class ScanBluetooth extends AppCompatActivity {

    private ActivityScanBluetoothBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBar();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ScanBluetooth.this, SelfTest.class);
                startActivity(intent);
            }
        }, 3000);
    }

    private void initBar() {
        getWindow().setStatusBarColor(getColor(R.color.bg_grey)); //设置状态栏背景色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        Toolbar toolbar = binding.scanToolbar.getRoot();
        TextView toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setBackgroundResource(R.color.bg_grey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  //设置隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);  //设置返回
        toolbarTitle.setText("搜索设备...");
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
}