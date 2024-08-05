package com.gtrab.qrscanmaster.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import java.io.Serializable


@Entity(tableName = "codes")
//investigar 02
//@TypeConverters(BarcodeDatabaseTypeConverter::class)
data class Barcode(
    //regresar a tipo val despues de las pruebas de carga 03
    @PrimaryKey(autoGenerate = true) val id:Long=0,
    var name: String?=null,
    var text: String,
    val formattedText:String,
    val format:BarcodeFormat,
    val schema: BarcodeSchema,
    val date: Long,
    val isFavorite: Boolean= false,
    val errorCorrectionLevel:String? =null

):Serializable