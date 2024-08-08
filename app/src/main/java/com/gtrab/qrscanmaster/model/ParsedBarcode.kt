package com.gtrab.qrscanmaster.model

import com.gtrab.qrscanmaster.model.schema.App
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema
import com.gtrab.qrscanmaster.model.schema.Wifi

class ParsedBarcode (barcode:Barcode){
    var id= barcode.id
    var name=barcode.name
    val text= barcode.text
    val formattedText= barcode.formattedText
    val format= barcode.format
    val  schema= barcode.schema
    val date= barcode.date
    var isFavorite=barcode.isFavorite
    //wifi data
    var networkAuthType:String? =null
    var networkName:String? =null
    var networkPassword:String?=null
    var isHidden:Boolean?=null
    var anonymousIdentity:String? = null
    var identity:String?=null
    var eapMethod:String?=null
    var phase2Method:String?=null
    //url
    var url:String?=null
    //app
    var appMarketUrl:String? = null
    var appPackage:String? = null

    init {

        when(schema){
            BarcodeSchema.APP -> parseApp()
            BarcodeSchema.WIFI -> parseWifi()
            BarcodeSchema.URL -> parseUrl()

            else ->{}
        }

    }

    private fun parseApp(){
        appMarketUrl=text
        // sirve para abrir el app en caso de estar instalado
        appPackage= App.parse(text)?.appPackage
    }

    private fun parseWifi(){
        val wifi = Wifi.parse(text) ?: return
        networkAuthType = wifi.encryption
        networkName = wifi.name
        networkPassword = wifi.password
        isHidden = wifi.isHidden
        anonymousIdentity = wifi.anonymousIdentity
        identity = wifi.identity
        eapMethod = wifi.eapMethod
        phase2Method = wifi.phase2Method

    }

    private fun parseUrl(){
        url= text
    }
}