package com.gtrab.qrscanmaster.usecase

import android.content.Context
import com.gtrab.qrscanmaster.extension.unsafeLazy

class Settings(private val context: Context) {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
        private var INSTANCE: Settings? = null
        fun getInstance(context: Context): Settings {
            return INSTANCE ?: Settings(context.applicationContext).apply { INSTANCE = this }
        }

    }
    private enum class Key {
        INVERSE_BARCODE_COLORS, OPEN_LINKS_AUTOMATICALLY, COPY_TO_CLIPBOARD, SIMPLE_AUTO_FOCUS, FLASHLIGHT, VIBRATE, CONTINUOUS_SCANNING, CONFIRM_SCANS_MANUALLY, IS_BACK_CAMERA, SAVE_SCANNED_BARCODES_TO_HISTORY, SAVE_CREATED_BARCODES_TO_HISTORY, DO_NOT_SAVE_DUPLICATES, SEARCH_ENGINE,
    }
    private val sharedPreferences by unsafeLazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var copyToClipboard:Boolean get()=get(Key.COPY_TO_CLIPBOARD,false)
        set(value) = set(Key.COPY_TO_CLIPBOARD,value)

    var vibrate: Boolean get() = get(Key.VIBRATE, true)
        set(value) = set(Key.VIBRATE, value)

    var confirmScansManually:Boolean get() = get(Key.CONFIRM_SCANS_MANUALLY,false)
        set(value) = set(Key.CONFIRM_SCANS_MANUALLY,value)

    var saveCreatedBarcodesToHistory:Boolean get() = get(Key.SAVE_CREATED_BARCODES_TO_HISTORY,true)
        set(value)= set(Key.SAVE_CREATED_BARCODES_TO_HISTORY,value)

    var saveScanBarcodeToHistory:Boolean get()= get(Key.SAVE_SCANNED_BARCODES_TO_HISTORY,true)
        set(value) =set(Key.SAVE_SCANNED_BARCODES_TO_HISTORY,value)

    private fun get(key: Key, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.name, default)
    }
    private fun set(key: Key, value: Boolean) {
        sharedPreferences.edit().putBoolean(key.name, value).apply()
    }
}