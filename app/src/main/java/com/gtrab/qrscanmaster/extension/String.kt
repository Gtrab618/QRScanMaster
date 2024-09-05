package com.gtrab.qrscanmaster.extension

import androidx.fragment.app.Fragment
import com.gtrab.qrscanmaster.model.schema.Schema
import java.util.Locale

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

fun String.startsWithAnyIgnoreCase(prefixes: List<String>):Boolean{
    var startsWith= false

    prefixes.forEach{prefix->
        if (startsWith(prefix,true)){
            startsWith=true
        }

    }
    return startsWith
}
fun String.removePrefixIgnoreCase(prefix: String): String {
    return substring(prefix.length)
}

fun String.toCaps(): String{
    return uppercase(Locale.ROOT)
}