package com.hui.app.utils;

import com.hui.app.ECBLE;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class LevelAndState {

    private final float adc;
    private final float v;
    private final float p;
    private final String setState;

    public LevelAndState(String string) {
        String[] components = string.split("2320503A"); //# P:
        String[] subComponents = components[1].split("20523A"); // R:
        String[] subSubComponents = subComponents[1].split("2023");// #
        float adc = Integer.parseInt(subComponents[0], 16);
        float v = 2 * adc * 3.3f / 4096;
        float p = (v - 3) / 1.08f;
        this.adc = adc;
        this.v = v;
        this.p = p;
        this.setState =  String.valueOf((char)Integer.parseInt(subSubComponents[0], 16));
    }

    public String getSetState() {
        return setState;
    }

    public float getP() {
        return p;
    }

    public float getV() {
        return v;
    }

    public float getAdc() {
        return adc;
    }



}
