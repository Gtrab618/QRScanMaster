package com.gtrab.qrscanmaster.util

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class MessageToast private constructor() {
    companion object {
        fun showToastPersonalize(context: Context, message: Int) {
            val toast =Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM , 0, 200) // El último parámetro es el desplazamiento vertical en píxeles
            toast.show()
        }
    }
}