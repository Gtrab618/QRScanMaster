package com.example.qrscanmaster.usecase

import android.content.Context

class SettingsGeneral private constructor(context: Context) {
    // Almacena una referencia al contexto de la aplicación en lugar de un contexto de actividad o fragmento
    private val applicationContext: Context = context.applicationContext
    companion object {
        @Volatile
        private var INSTANCE: SettingsGeneral? = null

        fun getInstance(context: Context): SettingsGeneral {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsGeneral(context).also { INSTANCE = it }
            }
        }
    }
    var isBackCamera: Boolean = true


}