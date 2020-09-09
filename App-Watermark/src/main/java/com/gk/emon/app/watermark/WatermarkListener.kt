package com.gk.emon.app.watermark

 interface WatermarkListener {
    fun onSuccess()
    fun showLog(log: String?, throwable: Throwable?)
    fun onFailure(message: String?, throwable: Throwable?)
}