package com.gtrab.qrscanmaster.util

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ParmissionRequestFragment(
    private val fragment: Fragment,
    private val permission: String,
    private val onRational: () -> Unit = {},
    private val onDenied: () -> Unit = {}
) {
    private var onGranted: () -> Unit = {}

    private val permissionReq =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                fragment.shouldShowRequestPermissionRationale(permission) ->
                    onRational()
                else -> onDenied()
            }
        }

    fun runWithPermission(body: () -> Unit) {
        onGranted = body
        permissionReq.launch(permission)
    }
}