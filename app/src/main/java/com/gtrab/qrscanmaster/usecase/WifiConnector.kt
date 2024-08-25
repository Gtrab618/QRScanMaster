package com.gtrab.qrscanmaster.usecase

import android.content.Context
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Identity
import android.util.Log
import androidx.annotation.RequiresApi
import com.gtrab.qrscanmaster.extension.toCaps
import com.gtrab.qrscanmaster.extension.wifiManager
import freemarker.ext.beans.StringModel
import io.reactivex.rxjava3.core.Completable

object WifiConnector {

    private val hexRegex = """^[\da-f]+$""".toRegex(RegexOption.IGNORE_CASE)

    fun connect(
        context:Context,
        authType:String,
        name:String,
        password:String,
        isHidden:Boolean,
        anonymousIdentity:String,
        identity: String,
        eapMethod:String,
        phase2Method:String

    ):Completable{
        return Completable
            .create{ emitter ->
                try {
                    tryToConnect(
                        context,
                        authType,
                        name,
                        password,
                        isHidden,
                        anonymousIdentity,
                        identity,
                        eapMethod.toEapMethod(),
                        phase2Method.toPhase2Method()
                    )
                    emitter.onComplete()
                }catch (e:Exception){
                    emitter.onError(e)
                }

            }


    }

    private fun tryToConnect(
        context: Context,
        authType: String,
        name: String,
        password: String,
        isHidden: Boolean,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            tryToConnectNewApi(context,authType,name,password,anonymousIdentity,identity,eapMethod,phase2Method)

        }else{

            tryToConnectOldApi(context,authType,name,password,isHidden,anonymousIdentity,identity,eapMethod,phase2Method)

        }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun tryToConnectNewApi(
        context: Context,
        authType: String,
        name: String,
        password: String,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ){
        when(authType.toCaps()){
            "","NOPASS" -> connectToOpenNetworkNewApi(context,name)
            "WPA","WPA2" -> connectToWpa2NetworkNewApi(context,name,password)
            "WPA2-EAP" -> connectToWpa2EapNetworkNewApi(context, name, password, anonymousIdentity, identity, eapMethod, phase2Method)

        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToOpenNetworkNewApi(context: Context, name: String) {
        val builder = WifiNetworkSuggestion.Builder().setSsid(name)
        connect(context, builder)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWpa2NetworkNewApi(context: Context, name: String, password: String){

        val builder= WifiNetworkSuggestion.Builder()
            .setSsid(name)
            .setWpa2Passphrase(password)

        connect(context,builder)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWpa2EapNetworkNewApi(
        context: Context,
        name: String,
        password: String,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ){
        val config=WifiEnterpriseConfig().also { config ->
            config.anonymousIdentity=anonymousIdentity
            config.identity=identity
            config.password=password
            eapMethod?.apply {
                config.eapMethod=this
            }
            phase2Method?.apply {
                config.phase2Method=this
            }

        }

        val builder= WifiNetworkSuggestion.Builder()
            .setSsid(name)
            .setWpa2Passphrase(password)
            .setWpa2EnterpriseConfig(config)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connect(context: Context, builder: WifiNetworkSuggestion.Builder){
        val suggestion= listOf(builder.build())
        context.wifiManager?.apply {

            removeNetworkSuggestions(suggestion)
            val status=addNetworkSuggestions(suggestion)
            if(status==0){
                println("conexion permitida")
            }else if (status==2){
                println("permiso de wifi denegado")
            }

        }



    }

    private fun tryToConnectOldApi(
        context: Context,
        authType: String,
        name: String,
        password: String,
        isHidden: Boolean,
        anonymousIdentity: String,
        identity: String,
        eapMethod: Int?,
        phase2Method: Int?
    ){}

    private fun String.toEapMethod():Int?{
        return when(this){
            "AKA" -> WifiEnterpriseConfig.Eap.AKA
            "AKA_PRIME" -> requireApiLevel(Build.VERSION_CODES.M) { WifiEnterpriseConfig.Eap.AKA_PRIME }
            "NONE" ->  WifiEnterpriseConfig.Eap.NONE
            "PEAP" -> WifiEnterpriseConfig.Eap.PEAP
            "PWD" -> WifiEnterpriseConfig.Eap.PWD
            "SIM" -> WifiEnterpriseConfig.Eap.SIM
            "TLS" -> WifiEnterpriseConfig.Eap.TLS
            "TTLS" -> WifiEnterpriseConfig.Eap.TTLS
            "UNAUTH_TLS" -> requireApiLevel(Build.VERSION_CODES.N) { WifiEnterpriseConfig.Eap.UNAUTH_TLS }
            else -> null
        }
    }

    private fun String.toPhase2Method():Int?{
        return when (this) {
            "AKA" -> requireApiLevel(Build.VERSION_CODES.O) { WifiEnterpriseConfig.Phase2.AKA }
            "AKA_PRIME" -> requireApiLevel(Build.VERSION_CODES.O) { WifiEnterpriseConfig.Phase2.AKA_PRIME }
            "GTC" -> WifiEnterpriseConfig.Phase2.GTC
            "MSCHAP" -> WifiEnterpriseConfig.Phase2.MSCHAP
            "MSCHAPV2" -> WifiEnterpriseConfig.Phase2.MSCHAPV2
            "NONE" -> WifiEnterpriseConfig.Phase2.NONE
            "PAP" -> WifiEnterpriseConfig.Phase2.PAP
            "SIM" -> requireApiLevel(Build.VERSION_CODES.O) { WifiEnterpriseConfig.Phase2.SIM }
            else -> WifiEnterpriseConfig.Phase2.NONE
        }
    }

    private inline fun <T> requireApiLevel(version: Int, block: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= version) return block() else null
    }

}