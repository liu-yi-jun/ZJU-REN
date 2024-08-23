package com.hui.app.model;

import androidx.lifecycle.ViewModel;

import com.hui.app.define.PulseDefine;

public class DoctorModel extends ViewModel {
    private PulseDefine firstMode;
    private PulseDefine secondMode;

    public DoctorModel() {
        firstMode = new PulseDefine();
        secondMode = new PulseDefine();
    }

    public PulseDefine getFirstMode() {
        return firstMode;
    }

    public void setFirstMode(PulseDefine firstMode) {
        this.firstMode = firstMode;
    }

    public PulseDefine getSecondMode() {
        return secondMode;
    }

    public void setSecondMode(PulseDefine secondMode) {
        this.secondMode = secondMode;
    }
}
