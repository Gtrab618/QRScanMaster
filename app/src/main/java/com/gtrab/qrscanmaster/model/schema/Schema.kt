package com.gtrab.qrscanmaster.model.schema
enum class BarcodeSchema {
    APP,
    EMAIL,
    GEO,
    SMS,
    URL,
    VCARD,
    WIFI,
    BOARDINGPASS,
    OTHER;
}

interface Schema{
    val schema: BarcodeSchema
    fun toFormattedText(): String
    fun toBarcodeText(): String

}