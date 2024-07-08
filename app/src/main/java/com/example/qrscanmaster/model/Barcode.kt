package com.example.qrscanmaster.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.qrscanmaster.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import java.io.Serializable

@Entity(tableName = "codes")
data class Barcode(
    @PrimaryKey(autoGenerate = true) val id:Long=0,
    val name: String?=null,
    val text: String,
    val formattedText:String,
    val format:BarcodeFormat,
    val schema: BarcodeSchema,
    val date: Long
):Serializable