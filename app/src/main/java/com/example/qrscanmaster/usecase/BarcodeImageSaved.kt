package com.example.qrscanmaster.usecase

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.provider.MediaStore.Images
import com.example.qrscanmaster.model.Barcode
import io.reactivex.Completable
import io.reactivex.rxjava3.core.CompletableSource
import java.io.OutputStream

object BarcodeImageSaved {
    fun savePngImageToPublicDirectory(context: Context,image:Bitmap,barcode:Barcode): CompletableSource {
        return CompletableSource { emitter ->
            try {
                saveToPublicDirectory(context,barcode,"image/png"){ outputStream ->
                    image.compress(Bitmap.CompressFormat.PNG,100,outputStream)
                }
                emitter.onComplete()
            }catch (ex:Exception){
                emitter.onError(ex)
            }
        }
    }


    fun saveSvgImageToPublicDirectory(context: Context,image: String,barcode: Barcode):CompletableSource{
        return CompletableSource { emitter ->
            try {
                saveToPublicDirectory(context,barcode,"image/svg+xml"){outputStream ->
                    outputStream.write(image.toByteArray())
                }
                emitter.onComplete()

            }catch (ex:Exception){
                emitter.onError(ex)
            }

        }
    }

    private fun saveToPublicDirectory(context: Context, barcode: Barcode, mimeType:String, action:(OutputStream)-> Unit){
        val contentResolver= context.contentResolver ?: return
        val imageTitle = "${barcode.format}_${barcode.schema}_${barcode.date}"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, imageTitle)
            put(MediaStore.Images.Media.DISPLAY_NAME, imageTitle)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis()/1000)
        }
        val uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI,values) ?: return
        contentResolver.openOutputStream(uri)?.apply {
            action(this)
            flush()
            close()
        }
    }
}