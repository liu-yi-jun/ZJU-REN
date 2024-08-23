package com.hui.app.define;

public class BlueItemDefine {
    private final int blue_decibel;
    private final String blue_name;
    private final String device_id;

    public  BlueItemDefine(String blue_name,int blue_decibel,String device_id) {
        this.blue_name = blue_name;
        this.blue_decibel = blue_decibel;
        this.device_id = device_id;
    }

    public String getBlueName() {
        return blue_name;
    }

    public String getDeviceId() {
        return device_id;
    }

    public int getBlueDecibel() {
        return blue_decibel;
    }
}
