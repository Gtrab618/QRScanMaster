package com.gtrab.qrscanmaster.model.schema

import com.gtrab.qrscanmaster.model.schema.BarcodeSchema
import com.gtrab.qrscanmaster.model.schema.Schema

class Other(val text:String): Schema {
    override val schema= BarcodeSchema.OTHER
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text

}