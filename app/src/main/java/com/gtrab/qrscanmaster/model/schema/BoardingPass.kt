package com.gtrab.qrscanmaster.model.schema

import android.widget.Toast
import com.gtrab.qrscanmaster.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.gtrab.qrscanmaster.extension.startsWithIgnoreCase
import com.gtrab.qrscanmaster.extension.unsafeLazy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BoardingPass(
    val name: String? = null,
    private val pnr: String? = null,
    private val from:String? = null,
    private val to :String? = null,
    private val carrier: String? = null,
    val flight: String?= null,
    private val date: String?=null,
    private val cabin: String? = null,
    private val seat: String? = null,
    private val seq: String? = null
) : Schema {
    companion object {
        private const val PREFIX = "M1"
        private val DATE_FORMATTER by unsafeLazy { SimpleDateFormat("d MMMM", Locale.ENGLISH) }

        //04 debido a gran variedad de companias este se mejorar en futuro
        fun parse(text: String): BoardingPass? {
            try {
                if (text.length < 60) {

                    return null
                }
                if (text.startsWithIgnoreCase(PREFIX).not()){
                    println("comprobacion 2")
                    return null
                }
                if(text[22] != 'E'){
                    println("comprobacion 3")
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
                // y= economy ,f Primero class, j Negocios
                val cabin = text.slice(47..47)
                //# asiento
                val seat = text.slice(48..51).trim()
                //persona numero X en registrarse
                val seq = text.slice(52..56)



                return BoardingPass(name,pnr,from, to, carrier, flight, date, cabin, seat, seq)
            } catch (e: Exception) {
                return null
            }
        }
    }

    override val schema= BarcodeSchema.BOARDINGPASS

    override fun toFormattedText(): String = listOf(name,pnr,"$from->$to","$carrier$flight",date, cabin, seat, seq).joinToStringNotNullOrBlankWithLineSeparator()

    override fun toBarcodeText():String{
        return ""
    }
}