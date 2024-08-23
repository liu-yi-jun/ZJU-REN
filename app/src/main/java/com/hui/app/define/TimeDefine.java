package com.hui.app.define;

import java.util.ArrayList;
import java.util.List;

public class TimeDefine {
    private List<String> timeArr = new ArrayList(); //当日刺激时间数组
    private String day = ""; //最后一次的刺激日期

    public TimeDefine() {}

    public List<String> getTimeArr() {
        return timeArr;
    }

    public void setTimeArr(List<String> timeArr) {
        this.timeArr = timeArr;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}