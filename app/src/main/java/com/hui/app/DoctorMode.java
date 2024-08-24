package com.hui.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.hui.app.adapter.DoctorModeAdapter;
import com.hui.app.databinding.ActivityDoctorModeBinding;
import com.hui.app.define.BlueItemDefine;
import com.hui.app.define.PulseDefine;
import com.hui.app.modeFragment.firstMode.FirstMode;
import com.hui.app.modeFragment.secondMode.SecondMode;
import com.hui.app.model.DoctorModel;
import com.hui.app.utils.TimeUtil;
import com.hui.app.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class DoctorMode extends AppCompatActivity implements View.OnClickListener {

    private ActivityDoctorModeBinding binding;
    private List<Fragment> fragments;
    private ViewPager2 viewPager;
    private Button saveBtn;
    private DoctorModel doctorModel;
    private int modeType = 1;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorModeBinding.inflate(getLayoutInflater());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(binding.getRoot());
        initBar();
        initsegmented();
        initView();
        initModel();
        initBLE();
    }


    private void initBLE() {
        ECBLE.onBLEConnectionStateChange((boolean ok, int errCode, String errMsg)-> runOnUiThread(()->{
            showAlert("提示","蓝牙断开连接",()->{});
        }));
        ECBLE.onBLECharacteristicValueChange((String str,String strHex)-> runOnUiThread(()->{
            if(strHex.contains("2320532023")) { //# S # 短路
                Util.showAlertRSDialog(this);
            }
        }));
        BlueItemDefine connectDevice = ECBLE.getConnectDevice();
        toolbarTitle.setText(connectDevice.getBlueName());
    }

    private void initModel() {
        doctorModel = new ViewModelProvider(this).get(DoctorModel.class);
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
                modeType = position == 0 ? 1 : 2;
                binding.viewPager.setCurrentItem(position);
            }
        });
    }

    private void initBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.bg_grey)); //设置状态栏背景色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        }

        Toolbar toolbar = binding.scanToolbar.getRoot();
        toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setBackgroundResource(R.color.bg_grey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //设置隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置返回
    }


    // 处理向上按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 这里可以添加你的逻辑，比如调用onBackPressed()
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                Gson gson = new Gson();
                if(modeType==1) {
                    PulseDefine firstMode = doctorModel.getFirstMode();
                    if (firstMode.getAmplitude().length() <= 0 ) {
                        Toast.makeText(this, "幅度不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(firstMode.getAmplitude()) < 0 || Integer.parseInt(firstMode.getAmplitude()) > 400) {
                        Toast.makeText(this, "幅度值为1-400", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (firstMode.getFrequency().length() <= 0) {
                        Toast.makeText(this, "频率不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(firstMode.getFrequency()) < 1 || Integer.parseInt(firstMode.getFrequency()) > 120) {
                        Toast.makeText(this, "频率值为1-120", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (firstMode.getPulseWidth().length() <= 0) {
                        Toast.makeText(this, "脉宽不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(firstMode.getPulseWidth()) < 45 || Integer.parseInt(firstMode.getPulseWidth()) > 400) {
                        Toast.makeText(this, "脉宽值为45-400", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (firstMode.getTime().length() <= 0) {
                        Toast.makeText(this, "时间不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(firstMode.getTime()) < 0 || Integer.parseInt(firstMode.getTime()) > 9999) {
                        Toast.makeText(this, "时间输入错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    firstMode.setAmplitude(TimeUtil.fillInNumberWithDigits(3, firstMode.getAmplitude()));
                    firstMode.setFrequency(TimeUtil.fillInNumberWithDigits(3, firstMode.getFrequency()));
                    firstMode.setPulseWidth(TimeUtil.fillInNumberWithDigits(3, firstMode.getPulseWidth()));
                    firstMode.setTime(TimeUtil.fillInNumberWithDigits(4, firstMode.getTime()));
                    String firstJson = gson.toJson(firstMode);
                    // 存储到SharedPreferences中
                    SharedPreferences firstSharedPreferences = getSharedPreferences("firstMode", MODE_PRIVATE);
                    firstSharedPreferences.edit().putString("firstMode", firstJson).apply();
                }
                if(modeType == 2) {
                    PulseDefine secondMode = doctorModel.getSecondMode();
                    if (secondMode.getAmplitude().length() <= 0) {
                        Toast.makeText(this, "幅度不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(secondMode.getAmplitude()) < 0 || Integer.parseInt(secondMode.getAmplitude())  > 400) {
                        Toast.makeText(this, "幅度值为0-400", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (secondMode.getBetween().length() <= 0) {
                        Toast.makeText(this, "丛间频率不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(secondMode.getBetween())  < 5 || Integer.parseInt(secondMode.getBetween()) > 20) {
                        Toast.makeText(this, "丛间频率值为5-20", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (secondMode.getIntra().length() <= 0) {
                        Toast.makeText(this, "丛内频率不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(secondMode.getIntra()) < 50 || Integer.parseInt(secondMode.getIntra()) > 500) {
                        Toast.makeText(this, "丛内频率值为50-500", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (secondMode.getPulseWidth().length() <= 0) {
                        Toast.makeText(this, "脉冲宽度不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(secondMode.getPulseWidth()) < 45 || Integer.parseInt(secondMode.getPulseWidth()) > 400) {
                        Toast.makeText(this, "脉宽宽度值为45-400", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (secondMode.getPulseNumber().length() <= 0) {
                        Toast.makeText(this, "脉冲个数不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(secondMode.getPulseNumber()) < 1 || Integer.parseInt(secondMode.getPulseNumber()) > 15) {
                        Toast.makeText(this, "脉冲个数值为1-15", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (secondMode.getTime().length() <= 0) {
                        Toast.makeText(this, "时间不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(secondMode.getTime()) < 0 || Integer.parseInt(secondMode.getTime()) > 9999) {
                        Toast.makeText(this, "时间输入错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    secondMode.setAmplitude(TimeUtil.fillInNumberWithDigits(3, secondMode.getAmplitude()));
                    secondMode.setIntra(TimeUtil.fillInNumberWithDigits(3, secondMode.getIntra()));
                    secondMode.setBetween(TimeUtil.fillInNumberWithDigits(3, secondMode.getBetween()));
                    secondMode.setPulseWidth(TimeUtil.fillInNumberWithDigits(3, secondMode.getPulseWidth()));
                    secondMode.setPulseNumber(TimeUtil.fillInNumberWithDigits(2, secondMode.getPulseNumber()));
                    secondMode.setTime(TimeUtil.fillInNumberWithDigits(4, secondMode.getTime()));
                    String secondJson = gson.toJson(secondMode);
                    SharedPreferences secondSharedPreferences = getSharedPreferences("secondMode", MODE_PRIVATE);
                    secondSharedPreferences.edit().putString("secondMode", secondJson).apply();
                }
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    void showAlert(String title, String content, Runnable callback) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("OK", (dialogInterface, i) ->
                        new Thread(callback).start()
                )
                .setCancelable(false)
                .create().show();
    }
}