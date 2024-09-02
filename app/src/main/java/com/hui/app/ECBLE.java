package com.hui.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hui.app.define.BlueItemDefine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

interface ECBluetoothAdapterStateChangeCallback {
    void callback(boolean ok, int errCode, String errMsg);
}

interface ECBluetoothDeviceFoundCallback {
    void callback(String id, String name, String mac, int rssi);
}

interface ECBLEConnectionStateChangeCallback {
    void callback(boolean ok, int errCode, String errMsg);
}

interface ECBLECharacteristicValueChangeCallback {
    void callback(String str, String strHex);
}

public class ECBLE {
    private static final String ECBLEChineseTypeUTF8 = "utf8";
    private static final String ECBLEChineseTypeGBK = "gbk";
    private static String ecBLEChineseType = ECBLEChineseTypeUTF8;
    static BlueItemDefine connectDevice;
    static Context context;

    static void setChineseTypeUTF8() {
        ecBLEChineseType = ECBLEChineseTypeUTF8;
    }

    static void setChineseTypeGBK() {
        ecBLEChineseType = ECBLEChineseTypeGBK;
    }

    private static BluetoothAdapter bluetoothAdapter = null;
    private static ECBluetoothAdapterStateChangeCallback ecBluetoothAdapterStateChangeCallback = (boolean ok, int errCode, String errMsg) -> {
    };

    static void onBluetoothAdapterStateChange(ECBluetoothAdapterStateChangeCallback cb) {
        ecBluetoothAdapterStateChangeCallback = cb;
    }

    static void openBluetoothAdapter(AppCompatActivity ctx) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            ecBluetoothAdapterStateChangeCallback.callback(false, 10000, "此设备不支持蓝牙");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            ecBluetoothAdapterStateChangeCallback.callback(false, 10001, "请打开设备蓝牙开关");
            return;
        }

        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!(gps || network)) {
            ecBluetoothAdapterStateChangeCallback.callback(false, 10002, "请打开设备定位开关");
            return;
        }

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!EasyPermissions.hasPermissions(ctx, perms)) {
            // 没有权限，进行权限请求
            EasyPermissions.requestPermissions(ctx, "请打开应用的定位权限", 0xECB001, perms);
            return;
        }

        //安卓12或以上，还需要蓝牙连接附近设备的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perms = new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
            if (!EasyPermissions.hasPermissions(ctx, perms)) {
                // 没有蓝牙权限，进行权限请求
                EasyPermissions.requestPermissions(ctx, "请打开应用的蓝牙权限，允许应用使用蓝牙连接附近的设备", 0xECB002, perms);
                return;
            }
        }

        ecBluetoothAdapterStateChangeCallback.callback(true, 0, "");

        if (bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            bluetoothGatt.close();
        }
    }

    static void onPermissionsGranted(AppCompatActivity ctx, int requestCode, @NonNull List<String> perms) {
        if (requestCode == 0xECB001) {//获取定位权限失败
            openBluetoothAdapter(ctx);
        }
        if (requestCode == 0xECB002) {//获取蓝牙连接附近设备的权限失败
            openBluetoothAdapter(ctx);
        }
    }

    static void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 0xECB001) {//获取定位权限失败
            ecBluetoothAdapterStateChangeCallback.callback(false, 10003, "请打开设备定位权限");
        }
        if (requestCode == 0xECB002) {//获取蓝牙连接附近设备的权限失败
            ecBluetoothAdapterStateChangeCallback.callback(false, 10004, "请打开设备蓝牙权限");
        }
    }

    //--------------------------------------------------------------------------------------------
    private static final List<BluetoothDevice> deviceList = new ArrayList<>();
    private static boolean scanFlag = false;
    private static ECBluetoothDeviceFoundCallback ecBluetoothDeviceFoundCallback = (String id, String name, String mac, int rssi) -> {
    };

    static void onBluetoothDeviceFound(ECBluetoothDeviceFoundCallback cb) {
        ecBluetoothDeviceFoundCallback = cb;
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @SuppressLint("MissingPermission")
    private static final BluetoothAdapter.LeScanCallback leScanCallback = (BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) -> {
        try {
//        Log.e("bytes",bytesToHexString(bytes));
//        String name = getBluetoothName(bytes);

            String name = bluetoothDevice.getName();
            if (name == null || name.isEmpty()) return;

            String mac = bluetoothDevice.getAddress();
            if (mac == null || mac.isEmpty()) return;
            mac = mac.replace(":", "");
            // 解析广告数据
//            byte[] uuidBytes = new byte[16];
//            System.arraycopy(bytes, 9, uuidBytes, 0, 16);
//            String uuid = bytesToHex(uuidBytes);
//            // uuid就是设备的UUID
//            String uuid32 = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
//            System.out.println("uuid"+uuid32);
            // uuid就是设备的UUID

//            ParcelUuid[] uuids = bluetoothDevice.getUuids();
//            System.out.println("uuids"+uuids);
//            if (uuids != null && uuids.length > 0) {
//                String uuid = uuids[0].getUuid().toString();
//                System.out.println("uuid"+uuid);
//                // do something with the UUID
//            }


//        Log.e("bleDiscovery", name + "|" + mac +"|"+ rssi);


            boolean isExist = false;
            for (BluetoothDevice tempDevice : deviceList) {
                if (tempDevice.getAddress().replace(":", "").equals(mac)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                deviceList.add(bluetoothDevice);
            }
            ecBluetoothDeviceFoundCallback.callback(mac, name, mac, rssi);
        } catch (Throwable e) {
            Log.e("LeScanCallback", "Throwable");
        }
    };

    static void startBluetoothDevicesDiscovery(Context ctx) {
        if (!scanFlag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            if (bluetoothAdapter != null) {
                bluetoothAdapter.startLeScan(leScanCallback);
                scanFlag = true;
            }
        }
    }

    static void stopBluetoothDevicesDiscovery(Context ctx) {
        if (scanFlag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            if (bluetoothAdapter != null) {
                bluetoothAdapter.stopLeScan(leScanCallback);
                scanFlag = false;
            }
        }
    }

    //--------------------------------------------------------------------------------------------
    private static BluetoothGatt bluetoothGatt = null;
    private static boolean connectFlag = false;
    private static int reconnectTime = 0;
    private static ECBLEConnectionStateChangeCallback ecBLEConnectionStateChangeCallback = (boolean ok, int errCode, String errMsg) -> {
    };

    static void onBLEConnectionStateChange(ECBLEConnectionStateChangeCallback cb) {
        ecBLEConnectionStateChangeCallback = cb;
    }

    private static ECBLEConnectionStateChangeCallback connectCallback = (boolean ok, int errCode, String errMsg) -> {
    };
    private static ECBLECharacteristicValueChangeCallback ecBLECharacteristicValueChangeCallback = (String str, String hexStr) -> {
    };

    static void onBLECharacteristicValueChange(ECBLECharacteristicValueChangeCallback cb) {
        ecBLECharacteristicValueChangeCallback = cb;
    }

    private static final String ecCharacteristicWriteUUID = "00000000-8e22-4541-9d4c-21edae82ed19";
    private static final String ecCharacteristicNotifyUUID = "00000001-8e22-4541-9d4c-21edae82ed19";
    private static BluetoothGattCharacteristic ecCharacteristicWrite;

    private static final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e("onConnectionStateChange", "status=" + status + "|" + "newState=" + newState);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.close();
                if (connectFlag) {
                    ecBLEConnectionStateChangeCallback.callback(false, 10000, "onConnectionStateChange:" + status + "|" + newState);
                } else {
                    connectCallback.callback(false, 10000, "onConnectionStateChange:" + status + "|" + newState);
                }
                connectFlag = false;
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                connectCallback.callback(true, 0, "");
                ecBLEConnectionStateChangeCallback.callback(true, 0, "");
                connectFlag = true;
                return;
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
                if (connectFlag) {
                    ecBLEConnectionStateChangeCallback.callback(false, 0, "");
                } else {
                    connectCallback.callback(false, 0, "");
                }
                connectFlag = false;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.e("ble-service", "onServicesDiscovered");
            bluetoothGatt = gatt;
            List<BluetoothGattService> bluetoothGattServices = bluetoothGatt.getServices();
            new Thread(() -> {
                try {
                    for (BluetoothGattService service : bluetoothGattServices) {
//                        String serviceUUID = service.getUuid().toString();
                        UUID uuid = service.getUuid();
                        if (uuid.getLeastSignificantBits() != 0x1800 &&
                                uuid.getLeastSignificantBits() != 0x1801) {
                            // 这不是 GAP 或 GATT 服务
                            Log.e("ble-service", "UUID=" + uuid.toString());
                            List<BluetoothGattCharacteristic> listGattCharacteristic = service.getCharacteristics();
                            for (BluetoothGattCharacteristic characteristic : listGattCharacteristic) {
                                Log.e("ble-char", "UUID=:" + characteristic.getUuid().toString());
                                //notify
//                            if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                                notifyBLECharacteristicValueChange(characteristic);
//                                Thread.sleep(800);
//                            }
//                            //notify
//                            if (characteristic.getUuid().toString().equals(ecCharacteristicNotifyUUID)) {
//                                notifyBLECharacteristicValueChange(characteristic);
//                            }
////                            //write
//                            if (characteristic.getUuid().toString().equals(ecCharacteristicWriteUUID)) {
//                                ecCharacteristicWrite = characteristic;
//                            }
                                System.out.println("isNotifiable"+characteristic.getUuid()+":"+(characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY));
                                System.out.println("isWritable"+characteristic.getUuid()+":"+(characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE));

                                boolean isNotifiable  = (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
                                boolean isWritable = (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
                                        || (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0;

                                if (isNotifiable) {
                                    // 特征支持通知操作
                                    notifyBLECharacteristicValueChange(characteristic);
                                }

                                if (isWritable) {
                                    // 这个特性是可写的
                                    ecCharacteristicWrite = characteristic;
                                }
                            }
                        }
//                        Log.e("ble-service", "UUID=" + serviceUUID);
//                        if(!serviceUUID.equals("00001801-0000-1000-8000-00805f9b34fb") && !serviceUUID.equals("00001800-0000-1000-8000-00805f9b34fb")) {
//
//                        }

                    }
                } catch (Throwable ignored) {
                }
            }).start();
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    setMtu();
                } catch (Throwable ignored) {
                }
            }).start();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] bytes = characteristic.getValue();
            Log.e("ble-receive","1" );
            if (bytes != null) {
                String str = "";
                if (Objects.equals(ecBLEChineseType, ECBLEChineseTypeGBK)) {
                    try {
                        str = new String(bytes, "GBK");
                    } catch (Throwable ignored) {
                    }
                } else {
                    str = new String(bytes);
                }
                String strHex = bytesToHexString(bytes);
                Log.e("ble-receive", "读取成功[string]:" + str);
                Log.e("ble-receive", "读取成功[strHex]:" + strHex);
                ecBLECharacteristicValueChangeCallback.callback(str, strHex);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (BluetoothGatt.GATT_SUCCESS == status) {
                Log.e("BLEService", "onMtuChanged success MTU = " + mtu);
            } else {
                Log.e("BLEService", "onMtuChanged fail ");
            }
        }
    };

    @SuppressLint("MissingPermission")
    private static void notifyBLECharacteristicValueChange(BluetoothGattCharacteristic characteristic) {
        boolean res = bluetoothGatt.setCharacteristicNotification(characteristic, true);
        if (!res) {
            return;
        }
        for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
            if (dp != null) {
                Log.e("notify",characteristic.getProperties() + "");
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                    dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                    dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                }
                bluetoothGatt.writeDescriptor(dp);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private static void setMtu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothGatt.requestMtu(247);
        }
    }

    @SuppressLint("MissingPermission")
    static void closeBLEConnection() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        };
    }

    private static void _createBLEConnection(Context ctx, String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                connectCallback.callback(false, 10001, "permission error");
                return;
            }
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        for (BluetoothDevice tempDevice : deviceList) {
            if (tempDevice.getAddress().replace(":", "").equals(id)) {
                bluetoothGatt = tempDevice.connectGatt(ctx, false, bluetoothGattCallback);
                return;
            }
        }
        connectCallback.callback(false, 10002, "id error");
    }

    static BlueItemDefine getConnectDevice() {
        return connectDevice;
    }

    static void createBLEConnection(Context ctx, BlueItemDefine blueItem) {
        context = ctx;
        String id = blueItem.getDeviceId();
        connectDevice = blueItem;
        reconnectTime = 0;
        connectCallback = (boolean ok, int errCode, String errMsg) -> {
            Log.e("connectCallback", ok + "|" + errCode + "|" + errMsg);
            if (!ok) {
                reconnectTime = reconnectTime + 1;
                if (reconnectTime > 4) {
                    reconnectTime = 0;
                    ecBLEConnectionStateChangeCallback.callback(false, errCode, errMsg);
                } else {
                    new Thread(() -> {
                        try {
                            Thread.sleep(300);
                            _createBLEConnection(ctx, id);
                        } catch (Throwable ignored) {
                        }
                    }).start();
                }
            }
        };
        _createBLEConnection(ctx, id);
    }

    @SuppressLint("MissingPermission")
    public static void writeBLECharacteristicValue(String data, boolean isHex) {
        byte[] byteArray;
        if (isHex) {
            byteArray = hexStrToBytes(data);
        } else {
            if (Objects.equals(ecBLEChineseType, ECBLEChineseTypeGBK)) {
                try {
                    byteArray = data.getBytes("GBK");
                } catch (Throwable e) {
                    return;
                }
            } else {
                byteArray = data.getBytes();
            }
        }
        if (ecCharacteristicWrite != null) {
            ecCharacteristicWrite.setValue(byteArray);
            //设置回复形式(这个设置会导致设备无法接收到)
//            ecCharacteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            //开始写数据
            if (bluetoothGatt != null) {
                bluetoothGatt.writeCharacteristic(ecCharacteristicWrite);
            }
        }
    }

    //--------------------------------------------------------------------------------------------

    @NonNull
    static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder str = new StringBuilder();
        for (byte b : bytes) {
            str.append(String.format("%02X", b));
        }
        return str.toString();
    }

    @NonNull
    static byte[] hexStrToBytes(@NonNull String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    @NonNull
    private static String getBluetoothName(byte[] bytes) {
        for (int i = 0; i < 62; i++) {
            if (i >= bytes.length) return "";
            int tempLen = bytes[i];
            int tempType = bytes[i + 1];
            if ((tempLen == 0) || (tempLen > 30)) {
                return "";
            }
            if ((tempType == 9) || (tempType == 8)) {
                byte[] nameBytes = new byte[tempLen - 1];
                System.arraycopy(bytes, i + 2, nameBytes, 0, tempLen - 1);
                return new String(nameBytes);
            }
            i += tempLen;
        }
        return "";
    }
}
