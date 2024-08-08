package com.gtrab.qrscanmaster.model.schema

import com.gtrab.qrscanmaster.extension.removePrefixIgnoreCase
import com.gtrab.qrscanmaster.extension.startsWithAnyIgnoreCase
import com.gtrab.qrscanmaster.extension.unsafeLazy

class App(val url:String): Schema {

    companion object{

        private val PREFIXES = listOf("market://details?id=", "market://search", "http://play.google.com/", "https://play.google.com/")

        fun parse (text:String): App?{
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()){
                return null
            }
            return App(text)
        }

        //02 falta completar para crear barcode
    }

    override val schema =BarcodeSchema.APP

    override fun toFormattedText(): String= url

    override fun toBarcodeText(): String= url
    val appPackage by unsafeLazy {
        url.removePrefixIgnoreCase(PREFIXES[0])
    }
}