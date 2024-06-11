package com.example.qrscanmaster.extension

fun List<String?>.joinToStringNotNullOrBlankWithLineSeparator():String{
    return joinToStringNotNullOrBlank("\n")
}
fun List<String?>.joinToStringNotNullOrBlank(separator:String):String{
    return filter { it.isNullOrBlank().not() }.joinToString(separator)
}