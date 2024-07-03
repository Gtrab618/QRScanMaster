package com.example.qrscanmaster.extension

private val escapedRegex = """\\([\\;,":])""".toRegex()
fun String.unescape():String{
    return replace(escapedRegex){ escaped ->
        escaped.groupValues[1]
    }
}
fun List<String?>.joinToStringNotNullOrBlankWithLineSeparator():String{
    return joinToStringNotNullOrBlank("\n")
}
fun List<String?>.joinToStringNotNullOrBlank(separator:String):String{
    return filter { it.isNullOrBlank().not() }.joinToString(separator)
}
fun String.startsWithIgnoreCase(prefix:String):Boolean{
    return startsWith(prefix,true)
}