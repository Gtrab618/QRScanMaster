package com.gtrab.qrscanmaster.extension

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Vibrator

//01 revisar esta decaprited para versiones de android 12 y superior
val Context.vibrator : Vibrator? get()= getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
val Context.wifiManager: WifiManager? get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
