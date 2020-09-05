package com.gk.emon.app.watermark;

import androidx.annotation.Nullable;

interface WatermarkContract {
    void onSuccess();

    void showLog(String log, Throwable throwable);

    void onFailure(String message, Throwable throwable);

}

public abstract class WatermarkListener implements WatermarkContract {

    @Override
    public void onSuccess() {

    }

    @Override
    public void showLog(String log,@Nullable Throwable throwable) {

    }

    @Override
    public void onFailure(String message, Throwable throwable) {

    }
}
