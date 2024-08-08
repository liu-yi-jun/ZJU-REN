package com.hui.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.hui.app.adapter.DoctorModeAdapter;
import com.hui.app.databinding.ActivityDoctorModeBinding;
import com.hui.app.modeFragment.firstMode.FirstMode;
import com.hui.app.modeFragment.secondMode.SecondMode;

import java.util.ArrayList;
import java.util.List;


public class DoctorMode extends AppCompatActivity implements View.OnClickListener {

    private ActivityDoctorModeBinding binding;
    private List<Fragment> fragments;
    private ViewPager2 viewPager;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDoctorModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBar();
        initsegmented();
        initView();
    }

    private void initView() {
        viewPager = binding.viewPager;
        saveBtn = binding.saveBtn;
        if (fragments == null) {
            fragments = new ArrayList<>();
            fragments.add(new FirstMode());
            fragments.add(new SecondMode());
        }
        viewPager.setOffscreenPageLimit(1);
        DoctorModeAdapter adapter = new DoctorModeAdapter(fragments, this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); //禁止左右滑动
        saveBtn.setOnClickListener(this);
    }

    private void initsegmented() {
        //      SegmentedButtonGroup点击胶囊
        SegmentedButtonGroup gradientButtonGroup = binding.segmentedButtonGroup;
        gradientButtonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                // Handle stuff here
                System.out.println("position: " + position);
                binding.viewPager.setCurrentItem(position);
            }
        });
    }

    private void initBar() {
        getWindow().setStatusBarColor(getColor(R.color.bg_grey)); //设置状态栏背景色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        Toolbar toolbar = binding.scanToolbar.getRoot();
        TextView toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setBackgroundResource(R.color.bg_grey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //设置隐藏标题
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
            case R.id.saveBtn:
                finish();
                break;
        }
    }
}