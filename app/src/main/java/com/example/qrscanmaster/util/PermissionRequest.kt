package com.example.qrscanmaster.util

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionRequester(activity: ComponentActivity, private val permission: String, private val onRational:()->Unit={}, private val onDenied:()->Unit={}) {
    private var onGranted: () -> Unit = {}

    private val permisionReq =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) ->
                    onRational()

                else -> onDenied()
            }
        }

    fun runWithPermission(body: () -> Unit) {
        onGranted = body
        permisionReq.launch(permission)
    }

}