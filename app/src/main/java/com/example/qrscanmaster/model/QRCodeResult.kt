package com.example.qrscanmaster.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class QRCodeResult(val textInfo: String?, val qrImage:Bitmap?):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU){
            parcel.readParcelable(Bitmap::class.java.classLoader,Bitmap::class.java)
        }else{
            @Suppress("DEPRECATION")
            parcel.readParcelable(Bitmap::class.java.classLoader)
        }

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(textInfo)
        parcel.writeParcelable(qrImage, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QRCodeResult> {
        override fun createFromParcel(parcel: Parcel): QRCodeResult {
            return QRCodeResult(parcel)
        }

        override fun newArray(size: Int): Array<QRCodeResult?> {
            return arrayOfNulls(size)
        }
    }

}