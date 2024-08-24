package com.gtrab.qrscanmaster.extension.plusVibrator

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun Vibrator.vibrateOnce(pattern:LongArray){
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        vibrate(VibrationEffect.createWaveform(pattern,-1))
    }else{
        @Suppress("Deprecation")
        vibrate(pattern,-1)
    }
}