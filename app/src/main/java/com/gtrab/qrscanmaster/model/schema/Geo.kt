package com.gtrab.qrscanmaster.model.schema

import com.gtrab.qrscanmaster.extension.removePrefixIgnoreCase
import com.gtrab.qrscanmaster.extension.startsWithIgnoreCase

class Geo :Schema{
    val uri:String
    private constructor(uri :String){
        this.uri=uri
    }

    companion object{
        private const val PREFIX="geo:"
        private const val SEPARATOR=","

        fun parse(text:String):Geo?{
            if (text.startsWithIgnoreCase(PREFIX).not()){
                return null
            }

            val uri = "$text?q=${text.removePrefixIgnoreCase(PREFIX)}"
            return Geo(uri)
        }
    }

    //para crear
    constructor(latitude:String, longitude:String, altitude:String?=null){
        uri = if (altitude.isNullOrEmpty()) {
            "$PREFIX$latitude$SEPARATOR$longitude?q=$latitude$longitude"
        } else {
            "$PREFIX$latitude$SEPARATOR$longitude$SEPARATOR$altitude"
        }
    }

    override val schema = BarcodeSchema.GEO

    override fun toFormattedText(): String= uri

    override fun toBarcodeText(): String {
        return uri.removePrefixIgnoreCase(PREFIX).replace(SEPARATOR, "\n")
    }
}