package com.hui.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.google.gson.Gson;
import com.hui.app.component.ActionSheetDialog;
import com.hui.app.databinding.ActivityStimulateBinding;
import com.hui.app.define.BlueItemDefine;
import com.hui.app.define.PulseDefine;
import com.hui.app.define.TimeDefine;
import com.hui.app.modeFragment.firstStimulate.FirstStimulate;
import com.hui.app.modeFragment.secondStimulate.SecondStimulate;
import com.hui.app.utils.LevelAndState;
import com.hui.app.utils.TimeUtil;
import com.hui.app.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Stimulate extends AppCompatActivity implements View.OnClickListener {

    private ActivityStimulateBinding binding;
    private Button startStimulate;
    private Button stimulateDoctorBtn;
    private ImageView backBtn;
    private PulseDefine firstMode;
    private PulseDefine secondMode;
    private TextView amplitudeValue;
    private TextView frequencyValue;
    private TextView pulseWidthValue;
    private TextView timeValue;
    private TextView actionTitle;
    private FirstStimulate firstStimulate;
    private SecondStimulate secondStimulate;
    private FragmentManager fragmentManager;
    private int modeType = 1;
    private TimeDefine timeData;
    private int countDown = 0;
    private Timer timer;
    private boolean isStart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStimulateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        onMyBackPressed(true, new Runnable() {
//            @Override
//            public void run() {
//                toStopStimulate();
//                finish();
//            }
//        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initBar();
        initView();
        initTimeData();
        initFragment();
    }

    @Override
    public void onResume() {
        initBLE();
        initModel();
        getInfo();
        super.onResume();
    }

    private void getInfo() {
        List<String> arrTime = new ArrayList<>(timeData.getTimeArr());
        if(!arrTime.isEmpty()) {
            String strTime = arrTime.get(arrTime.size() - 1);
            binding.labLast.setText(strTime);
            binding.labNext.setText(nextTime(strTime));
        }
        int count = arrTime.size() > 8 ? 8 : arrTime.size();
        binding.labTime.setText(String.valueOf(8 - count));
    }

    private void initTimeData() {
        Gson gson = new Gson();
        SharedPreferences firstSharedPreferences = getSharedPreferences("timeData", MODE_PRIVATE);
        String timeJson= firstSharedPreferences.getString("timeData", "");
        timeData = gson.fromJson(timeJson, TimeDefine.class);
        if(timeData == null) {
            timeData = new TimeDefine();
            saveTimeData();
        }
    }

    private void saveTimeData() {
        Gson gson = new Gson();
        String timeJson = gson.toJson(timeData);
        SharedPreferences secondSharedPreferences = getSharedPreferences("timeData", MODE_PRIVATE);
        secondSharedPreferences.edit().putString("timeData", timeJson).apply();
    }

    private void initBLE() {
        ECBLE.onBLEConnectionStateChange((boolean ok, int errCode, String errMsg)-> runOnUiThread(()->{
            showAlert("提示","蓝牙断开连接",()->{});
        }));
        ECBLE.onBLECharacteristicValueChange((String str,String strHex)-> runOnUiThread(()->{
            if(strHex.contains("2320503A")) { //# P:
                LevelAndState levelAndState = new LevelAndState(strHex);
                if (levelAndState.getSetState().equals("0")) {
//                toStartStimulate();
                } else {
                    Toast.makeText(this, "电极可能断开，请检查阻抗", Toast.LENGTH_SHORT).show();
                    toStopStimulate();
                }
            }
            if(strHex.contains("2320532023")) { //# S # 短路
                Util.showAlertRSDialog(this);
                toStopStimulate();
            }
        }));
    }

        private void initFragment() {
        firstStimulate = new FirstStimulate();
        secondStimulate = new SecondStimulate();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container,firstStimulate)
                .commit();
    }

    private void initModel() {
        Gson gson = new Gson();
        // 读取SharedPreferences中的JSON字符串
        SharedPreferences firstSharedPreferences = getSharedPreferences("firstMode", MODE_PRIVATE);
        String firstJson= firstSharedPreferences.getString("firstMode", "");
         firstMode = gson.fromJson(firstJson, PulseDefine.class);


        SharedPreferences secondSharedPreferences = getSharedPreferences("secondMode", MODE_PRIVATE);
        String secondJson = secondSharedPreferences.getString("secondMode", "");
         secondMode = gson.fromJson(secondJson, PulseDefine.class);

    }

    private void initBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.stimulate_bg_grey));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        }
    }

//    private void onMyBackPressed(boolean isEnable, final Runnable callback) {
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(isEnable) {
//            @Override
//            public void handleOnBackPressed() {
//                callback.run();
//            }
//        });
//    }

    private void sendBLEMes(String mes) {
        ECBLE.writeBLECharacteristicValue(mes, false);
    }

    private void initView() {
        startStimulate = binding.startStimulate;
        stimulateDoctorBtn = binding.stimulateDoctorBtn;
        backBtn = binding.backBtn;
        actionTitle = binding.actionTitle;
        startStimulate.setOnClickListener(this);
        stimulateDoctorBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        actionTitle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_stimulate:
                if(!isStart) {
                    toStartStimulate();
                }else {
                    toStopStimulate();
                }
                break;
            case R.id.stimulate_doctor_btn:
                Intent intent = new Intent(Stimulate.this, DoctorMode.class);
                startActivity(intent);
                toStopStimulate();
                break;
            case R.id.action_title:
                new ActionSheetDialog(Stimulate.this)
                        .builder()
                        .setTitle("请选择模式")
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("模式一", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        actionTitle.setText("模式一");
                                        if(modeType != 1){
                                            countDown = 0;
                                            toStopStimulate();
                                        }
                                        modeType = 1;
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragment_container, firstStimulate)
                                                .commit();
                                    }
                                })
                        .addSheetItem("模式二", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        actionTitle.setText("模式二");
                                        if(modeType != 2){
                                            countDown = 0;
                                            toStopStimulate();
                                        }
                                        modeType = 2;
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragment_container, secondStimulate)
                                                .commit();
                                    }
                                })
                        .addSheetItem("模式三", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        Toast.makeText(Stimulate.this, "选择模式三", Toast.LENGTH_SHORT).show();
                                    }

                                }).show();
                break;
            case  R.id.back_btn:
                toStopStimulate();
                finish();
                break;
        }
    }

    private void toStartStimulate() {
        if( modeType == 1 && firstMode == null) {
            Toast.makeText(this, "请先设置刺激参数", Toast.LENGTH_SHORT).show();
            return;
        }
        if( modeType == 2 && secondMode == null) {
            Toast.makeText(this, "请先设置刺激参数", Toast.LENGTH_SHORT).show();
            return;
        }
        isStart = true;
        int time = 0;
        if(modeType == 1 ) {
            time = Integer.parseInt(firstMode.getTime());
            String str = String.format("# 0%d %s %s %s %s #", modeType, firstMode.getAmplitude(), firstMode.getFrequency(), firstMode.getPulseWidth(), firstMode.getTime());
            //"# 01 205 110 200 0060 #"
            sendBLEMes(str);
        }
        if(modeType == 2 && secondMode != null) {
            time = Integer.parseInt(secondMode.getTime());
            String str = String.format("# %d%s%s%s%s%s%s #",
                    modeType,
                    secondMode.getAmplitude(),
                    secondMode.getBetween(),
                    secondMode.getIntra(),
                    secondMode.getPulseWidth(),
                    secondMode.getPulseNumber(),
                    secondMode.getTime());
            // "# 2200005050200090015 #"
            sendBLEMes(str);
        }
        updateInfo();
        if(countDown == 0) {
            countDown = time;
        }
        startStimulate.setText(String.format("刺激还剩%ds,点击停止", countDown));
        startCountdown();
    }

    private void toStopStimulate() {
        if(!isStart) {
            return;
        }
        isStart = false;
        startStimulate.setText("开始刺激");
        if (modeType == 1 && firstMode != null) {
            String str = String.format("# 05 %s %s %s %s #", firstMode.getAmplitude(), firstMode.getFrequency(), firstMode.getPulseWidth(), firstMode.getTime());
            sendBLEMes(str);
        }
        if(modeType == 2 && secondMode != null) {
            String str = String.format("# 5%s%s%s%s%s%s #",
                    secondMode.getAmplitude(),
                    secondMode.getBetween(),
                    secondMode.getIntra(),
                    secondMode.getPulseWidth(),
                    secondMode.getPulseNumber(),
                    secondMode.getTime());;
            sendBLEMes(str);
        }
        stopTimer();
    }


    private void updateInfo() {
        String strToday = TimeUtil.getDay();
        String strDay = timeData.getDay();
        List<String> arrTime = new ArrayList<>(timeData.getTimeArr());
        String strTime = TimeUtil.getHourMinutes();

        if (!strDay.equals(strToday)) {
            arrTime.clear();
        }

        arrTime.add(strTime);
        timeData.setTimeArr(arrTime);
        binding.labLast.setText(strTime);
        binding.labNext.setText(nextTime(strTime));
        arrTime = new ArrayList<>(timeData.getTimeArr());
        timeData.setDay(strToday);

        int count = arrTime.size() > 8 ? 8 : arrTime.size();
        binding.labTime.setText(String.valueOf(8 - count));
        saveTimeData();
    }

    private String nextTime(String time) {
        SimpleDateFormat inputDateFormatter = new SimpleDateFormat("HH:mm");

        try {
            // Parse the input string into a Date object
            Date inputDate = inputDateFormatter.parse(time);

            // Add 15 minutes
            Date fifteenMinutesLater = new Date(inputDate.getTime() + (15 * 60 * 1000));

            // Format the resulting date back into a string
            String outputTimeStr = inputDateFormatter.format(fifteenMinutesLater);

            return outputTimeStr;
        } catch (ParseException e) {
            // Handle parsing error
            return null;
        }
    }


    // 开始倒计时
    public void startCountdown() {
        // 创建并启动定时器，每秒更新倒计时
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateCountdown();
            }
        };
        timer.schedule(task, 1000, 1000);

    }

    // 更新倒计时
    private void updateCountdown() {
        // 每次定时器触发时减少倒计时
        countDown--;
        // 更新UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startStimulate.setText("刺激还剩" + countDown + "s,点击停止");
            }
        });
        // 如果倒计时为0，停止定时器
        if (countDown <= 0) {
            sendBLEMes("SD");
            stopTimer();
            // 更新UI显示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startStimulate.setText("开始刺激");
                    //btnStart.setSelected(false);
                }
            });
        }
    }
    private void stopTimer() {
        // 停止计时器
        if (timer != null) {
            timer.cancel();
            timer = null;
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