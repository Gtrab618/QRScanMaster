package com.gtrab.qrscanmaster.model

import com.gtrab.qrscanmaster.model.schema.App
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema
import com.gtrab.qrscanmaster.model.schema.BoardingPass
import com.gtrab.qrscanmaster.model.schema.Email
import com.gtrab.qrscanmaster.model.schema.Geo
import com.gtrab.qrscanmaster.model.schema.Sms
import com.gtrab.qrscanmaster.model.schema.VCard
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
    //SMS
    var phone:String? = null
    var smsBody:String?= null
    //Geo
    var geoUri:String? =null
    //flight
    var numberFlight:String? =null
    //vcard
    var firstName: String? = null
    var lastName: String? = null
    var email : String? = null
    var emailType: String? =null
    var phoneType: String? = null
    var secondaryPhone: String? = null
    var secondaryPhoneType: String? = null
    var tertiaryPhone: String? = null
    var tertiaryPhoneType: String? = null
    //email
    var emailSubject:String?= null
    var emailBody:String?=null

    init {

        when(schema){
            BarcodeSchema.APP -> parseApp()
            BarcodeSchema.GEO -> parseGeo()
            BarcodeSchema.WIFI -> parseWifi()
            BarcodeSchema.URL -> parseUrl()
            BarcodeSchema.BOARDINGPASS -> parseBoardingPass()
            BarcodeSchema.SMS -> parseSms()
            BarcodeSchema.VCARD -> parseVcard()
            BarcodeSchema.EMAIL ->parseEmail()
            else ->{}
        }

    }

    private fun parseApp(){
        appMarketUrl=text
        // sirve para abrir el app en caso de estar instalado
        appPackage= App.parse(text)?.appPackage
    }

    private fun parseGeo(){
        val geo = Geo.parse(text)
        println("parseao de text geo "+ text)
        geoUri=geo?.uri
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

    private fun parseSms(){
        val sms= Sms.parse(text) ?: return
        phone = sms.phone
        smsBody= sms.message
    }

    private fun parseBoardingPass(){
        val flight= BoardingPass.parse(text)
        numberFlight=flight?.carrier+flight?.flight;
    }

    private fun parseVcard(){
        val vCard= VCard.parse(text)
        firstName=vCard?.firstName
        lastName=vCard?.lastName
        email=vCard?.email
        emailType=vCard?.emailType
        phone=vCard?.phone
        phoneType=vCard?.phoneType
        secondaryPhone=vCard?.secondaryPhone
        secondaryPhoneType=vCard?.secondaryPhoneType
        tertiaryPhone=vCard?.tertiaryPhone
        tertiaryPhoneType=vCard?.tertiaryPhoneType
    }

    private fun parseEmail(){
        val email =Email.parse(text) ?:return
        this.email=email.email
        emailSubject = email.subject
        emailBody = email.body

    }

}