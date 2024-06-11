package com.example.qrscanmaster.model

import com.example.qrscanmaster.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.qrscanmaster.model.schema.BarcodeSchema
import com.example.qrscanmaster.model.schema.Schema


class Wifi (val encryption: String? = null,
            val name: String? = null,
            val password: String? = null,
            val isHidden: Boolean? = null,
            val anonymousIdentity: String? = null,
            val identity: String? = null,
            val eapMethod: String? = null,
            val phase2Method: String? = null
):Schema {

    override val schema = BarcodeSchema.WIFI
    override fun toFormattedText(): String {
        return listOf(name,encryption,password).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        TODO("Not yet implemented")
    }

}