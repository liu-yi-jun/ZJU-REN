package com.hui.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hui.app.adapter.BlueItemAdapter;
import com.hui.app.databinding.ActivityScanBluetoothBinding;
import com.hui.app.define.BlueItemDefine;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ScanBluetooth extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    static class DeviceInfo {
        String id;
        String name;
        String mac;
        int rssi;

        DeviceInfo(String id, String name, String mac, int rssi) {
            this.id = id;
            this.name = name;
            this.mac = mac;
            this.rssi = rssi;
        }
    }

    private ActivityScanBluetoothBinding binding;
    private ListView listView;
    private BlueItemAdapter adapter;
    List<DeviceInfo> deviceListData = new ArrayList<>();
    List<BlueItemDefine> deviceListDataShow = new ArrayList<>();
    AlertDialog connectDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBluetoothBinding.inflate(getLayoutInflater());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(binding.getRoot());
        initBar();
        initView();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(ScanBluetooth.this, SelfTest.class);
//                startActivity(intent);
//            }
//        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        deviceListData.clear();
        deviceListDataShow.clear();
        adapter.notifyDataSetChanged();

        openBluetoothAdapter();
    }

    private void initBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.bg_grey)); //设置状态栏背景色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //设置状态栏字体颜色
        }

        Toolbar toolbar = binding.scanToolbar.getRoot();
        TextView toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setBackgroundResource(R.color.bg_grey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  //设置隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);  //设置返回
        toolbarTitle.setText("搜索设备...");
    }

    private void initView() {
        listView = binding.listView;
        //权限
        openBluetoothAdapter();
        listRefresh();
        adapter = new BlueItemAdapter(ScanBluetooth.this, deviceListDataShow);
        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            showConnectDialog();
            BlueItemDefine blueItem = (BlueItemDefine) listView.getItemAtPosition(i);
            ECBLE.onBLEConnectionStateChange((boolean ok, int errCode, String errMsg) -> runOnUiThread(() -> {
                hideConnectDialog();
                if (ok) {
//                ECBLE.stopBluetoothDevicesDiscovery(this);
                    startActivities(new Intent[]{new Intent().setClass(this, SelfTest.class)});
                } else {
                    showToast("蓝牙连接失败,errCode=" + errCode + ",errMsg=" + errMsg);
                    showAlert("提示", "蓝牙连接失败,errCode=" + errCode + ",errMsg=" + errMsg, () -> {
                    });
                }
            }));
            ECBLE.createBLEConnection(this, blueItem);
        });
        listView.setAdapter(adapter);
    }



    void listRefresh() {
        new Handler().postDelayed(() -> {
            deviceListDataShow.clear();
            for (DeviceInfo tempDevice : deviceListData) {
                deviceListDataShow.add(new BlueItemDefine(tempDevice.name, tempDevice.rssi,tempDevice.mac));
            }
            adapter.notifyDataSetChanged();
            listRefresh();
        }, 400);
    }

    void startBluetoothDevicesDiscovery() {
        ECBLE.onBluetoothDeviceFound((String id, String name, String mac, int rssi) -> runOnUiThread(() -> {
//            Log.e("Discovery", name + "|" + mac + "|" + rssi);
            boolean isExist = false;
            for (DeviceInfo tempDevice : deviceListData) {
                if (tempDevice.id.equals(id)) {
                    tempDevice.rssi = rssi;
                    tempDevice.name = name;
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {

                deviceListData.add(new DeviceInfo(id, name, mac, rssi));
            }
        }));
        ECBLE.startBluetoothDevicesDiscovery(this);
    }


    void showConnectDialog() {
        if (connectDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScanBluetooth.this);
            builder.setMessage("连接中...");
            builder.setCancelable(false);
            connectDialog = builder.create();
        }
        connectDialog.show();
    }

    void hideConnectDialog() {
        if (connectDialog != null) connectDialog.dismiss();
    }

    void openBluetoothAdapter() {
        ECBLE.onBluetoothAdapterStateChange((boolean ok, int errCode, String errMsg) -> runOnUiThread(() -> {
            if (!ok) {
                showAlert("提示", "openBluetoothAdapter error,errCode=" + errCode + ",errMsg=" + errMsg, () -> runOnUiThread(() -> {
                    if (errCode == 10001) {
                        //蓝牙开关没有打开
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    }
                    if (errCode == 10002) {
                        //定位开关没有打开
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                    //获取定位权限失败
                    if (errCode == 10003) {
                        new AppSettingsDialog.Builder(this)
                                .setTitle("提示")
                                .setRationale("请打开应用的定位权限")
                                .build().show();
                    }
                    //获取蓝牙连接附近设备的权限失败
                    if (errCode == 10004) {
                        new AppSettingsDialog.Builder(this)
                                .setTitle("提示")
                                .setRationale("请打开应用的蓝牙权限，允许应用使用蓝牙连接附近的设备")
                                .build().show();
                    }
                }));
            } else {
//                showToast("openBluetoothAdapter ok");
                Log.e("openBluetoothAdapter", "ok");
                startBluetoothDevicesDiscovery();
            }
        }));
        ECBLE.openBluetoothAdapter(this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        ECBLE.onPermissionsGranted(this, requestCode, perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ECBLE.onPermissionsDenied(requestCode, perms);
    }
}