package com.hui.app.define;

public class PulseDefine {
    private String amplitude = ""; //幅度
    private String frequency = ""; //频率
    private String pulseWidth = ""; //脉宽

    private String between = ""; //丛间频率
    private String intra = "";  //丛内频率
    private String pulseNumber = "";

    private String time = ""; //刺激时间

    public String getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude = amplitude;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPulseWidth() {
        return pulseWidth;
    }

    public void setPulseWidth(String pulseWidth) {
        this.pulseWidth = pulseWidth;
    }

    public String getBetween() {
        return between;
    }

    public void setBetween(String between) {
        this.between = between;
    }

    public String getIntra() {
        return intra;
    }

    public void setIntra(String intra) {
        this.intra = intra;
    }

    public String getPulseNumber() {
        return pulseNumber;
    }

    public void setPulseNumber(String pulseNumber) {
        this.pulseNumber = pulseNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
