package com.gk.emon.app.watermark;

interface WatermarkListener {

    void onSuccess();

    void showLog(String log, Throwable throwable);

    void onFailure(String message, Throwable throwable);
}
