package com.gtrab.qrscanmaster.model.schema

import com.gtrab.qrscanmaster.extension.startsWithIgnoreCase
import com.gtrab.qrscanmaster.extension.unsafeLazy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BoardingPass(
    val name: String? = null,
    val flightNumber: String? = null,
    val origin: String? = null,
    val destination: String? = null,
    val seat: String? = null,
    val flightDate: String? = null,
    val additionalInfo: String? = null
) : Schema {
    companion object {
        private const val PREFIX = "M1:"
        private val DATE_FORMATTER by unsafeLazy { SimpleDateFormat("d MMMM", Locale.ENGLISH) }

        //04 debido a gran variedad de companias este se mejorar en futuro
        fun parse(text: String): BoardingPass? {
            try {
                if (text.length < 60) {
                    return null
                }
                if (text.startsWithIgnoreCase(PREFIX).not()){
                    return null
                }
                if(text[22] != 'E'){
                    return null
                }

                val name = text.slice(2..21).trim()
                //referencia reserva
                val pnr = text.slice(23..29).trim()
                val from = text.slice(30..32)
                val to = text.slice(33..35)
                val carrier = text.slice(36..38).trim()
                val flight = text.slice(39..43).trim()
                val dateJ = text.slice(44..46).toInt()
                val today = Calendar.getInstance()
                today.set(Calendar.DAY_OF_YEAR, dateJ)
                val date: String = DATE_FORMATTER.format(today.time)

                return BoardingPass(name,pnr,from, to, carrier, flight, date)
            } catch (e: Exception) {
                return null
            }
        }
    }

    override val schema= BarcodeSchema.BOARDINGPASS

    override fun toFormattedText(): String {
        TODO("Not yet implemented")
    }

    override fun toBarcodeText():String{
        return ""
    }
}