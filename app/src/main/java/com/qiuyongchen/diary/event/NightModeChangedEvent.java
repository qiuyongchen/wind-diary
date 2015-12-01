package com.qiuyongchen.diary.event;

/**
 * Created by DELL on 2015/12/1.
 */
public class NightModeChangedEvent {
    boolean nightMode;

    public NightModeChangedEvent(boolean n) {
        nightMode = n;
    }

    public boolean getNightMode() {
        return nightMode;
    }

    public void setNightMode(boolean n) {
        this.nightMode = n;
    }
}
