package com.gtrab.qrscanmaster.extension

fun StringBuilder.appendIfNotNullOrBlank(prefix: String="", value:String?, suffix:String=""):StringBuilder {
    if(value.isNullOrBlank().not()){
        append(prefix)
        append(value)
        append(suffix)
    }
    return this
}