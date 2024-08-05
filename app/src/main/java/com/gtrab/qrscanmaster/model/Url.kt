package com.gtrab.qrscanmaster.model

import com.gtrab.qrscanmaster.extension.startsWithAnyIgnoreCase
import com.gtrab.qrscanmaster.extension.startsWithIgnoreCase
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema
import com.gtrab.qrscanmaster.model.schema.Schema

class Url(val url:String):Schema {
    companion object{
        private const val HTTP_PREFIX="http://"
        private const val HTTPS_PREFIX="https://"
        private const val WWW_PREFIX="www."
        private val PREFIXES= listOf(HTTP_PREFIX, HTTPS_PREFIX, WWW_PREFIX)


        fun parse(text:String):Url?{
            if(text.startsWithAnyIgnoreCase(PREFIXES).not()){
                return null
            }

            val url= when{
                text.startsWithIgnoreCase(WWW_PREFIX) ->"$HTTP_PREFIX$text"
                else -> text
            }

            return Url(url)

        }
    }

    override val schema= BarcodeSchema.URL
    override fun toFormattedText(): String =url
    override fun toBarcodeText(): String = url

}