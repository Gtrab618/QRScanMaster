package com.example.qrscanmaster.comunication

import com.google.zxing.Result

interface Communicator {
    fun passInfoQr(data : Result)
}