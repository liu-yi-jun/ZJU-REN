package com.hui.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hui.app.databinding.ActivitySelfTestBinding;
import com.hui.app.define.BlueItemDefine;
import com.hui.app.utils.LevelAndState;

public class SelfTest extends AppCompatActivity implements View.OnClickListener {

    private ActivitySelfTestBinding binding;
    private Button stimulateBtn;
    private Button doctorBtn;
    private TextView labTime;
    private TextView labBattery;
    private TextView labResistance;
    private TextView labBleState;
    private TextView recheck;
    private ImageView resistanceStatues;
    private TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelfTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBar();
        initView();
        initBLE();
    }

    private void initBLE() {
        ECBLE.setChineseTypeUTF8();
        ECBLE.onBLEConnectionStateChange((boolean ok, int errCode, String errMsg)-> runOnUiThread(()->{
            labBleState.setText("蓝牙未连接");
            labBleState.setTextColor(Color.parseColor("#ED9E57"));
            showAlert("提示","蓝牙断开连接",()->{});
        }));
        ECBLE.onBLECharacteristicValueChange((String str,String strHex)-> runOnUiThread(()->{
            LevelAndState levelAndState = new LevelAndState(strHex);
            labBattery.setText("当前电量：" + (int)(levelAndState.getP() * 100) + "%");
            float time = levelAndState.getP() * 4.2f / 60;
            if (time > 1.0) {
                labTime.setText(String.format("预计还可使用%.1fh", time));
            } else {
                labTime.setText(String.format("预计还可使用%.0fmin", time * 60));
            }
            if (levelAndState.getSetState().equals("0")) {
                labResistance.setText("正常");
                resistanceStatues.setBackgroundResource(R.drawable.resistancegreen);
            } else {
                resistanceStatues.setBackgroundResource(R.drawable.resistanceorange);
                if (levelAndState.getSetState().equals("1")) {
                    labResistance.setText("偏低");
                } else {
                    labResistance.setText("偏高");
                }
            }
        }));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BlueItemDefine connectDevice = ECBLE.getConnectDevice();
                labBleState.setText("蓝牙已连接");
                labBleState.setTextColor(Color.parseColor("#453A68"));
                toolbarTitle.setText(connectDevice.getBlueName());
                sendCheck();
            }
        }, 1000);

    }

    private void sendCheck() {
        ECBLE.writeBLECharacteristicValue("# 11 PR #", false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","onDestroy");
        ECBLE.onBLECharacteristicValueChange((str,strHex)->{});
        ECBLE.onBLEConnectionStateChange((ok,errCode,errMsg)->{});
        ECBLE.closeBLEConnection();
    }


    private void initView() {
        stimulateBtn = binding.stimulateBtn;
        doctorBtn = binding.doctorBtn;
        labTime = binding.labTime;
        labBattery = binding.labBattery;
        labResistance = binding.labResistance;
        labBleState = binding.labBleState;
        recheck = binding.recheck;
        resistanceStatues = binding.resistanceStatues;
        //        点击刺激按钮
        stimulateBtn.setOnClickListener(this);
        doctorBtn.setOnClickListener(this);
        recheck.setOnClickListener(this);
    }


    private void initBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        Toolbar toolbar = binding.scanToolbar.getRoot();
        toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  //设置隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置返回
    }

    // 处理向上按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stimulate_btn:
                Intent intent1 = new Intent(SelfTest.this, Stimulate.class);
                startActivity(intent1);
                break;
            case R.id.doctorBtn:
                Intent intent2 = new Intent(SelfTest.this, DoctorMode.class);
                startActivity(intent2);
                break;
            case R.id.recheck:
                Toast.makeText(this, "状态已更新", Toast.LENGTH_SHORT).show();
                sendCheck();
                break;
        }


    }

    void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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