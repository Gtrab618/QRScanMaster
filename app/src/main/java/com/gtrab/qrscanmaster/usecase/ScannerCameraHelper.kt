package com.gtrab.qrscanmaster.usecase
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.google.firebase.crashlytics.FirebaseCrashlytics


object  ScannerCameraHelper {
    // Método para obtener las propiedades de la cámara trasera
    fun getBackCameraProperties(isBackCamera: Boolean,context: Context): CameraCharacteristics? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraFacing= getCameraFacing(isBackCamera)
        try {
            // Obtener la lista de cámaras disponibles
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                // Verificar si es la cámara trasera
                if (facing == cameraFacing) {
                    // Es la cámara trasera, retornar sus propiedades
                    return characteristics
                }
            }
        } catch (e: CameraAccessException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("scannerCameraHelper 27: ${e.message}")
        }
        return null
    }

    private fun getCameraFacing(isBackCamera: Boolean): Int {
        return if (isBackCamera) {
            CameraCharacteristics.LENS_FACING_BACK
        } else {
            CameraCharacteristics.LENS_FACING_FRONT
        }
    }
}