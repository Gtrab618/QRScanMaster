package com.gtrab.qrscanmaster.dependencies

import androidx.fragment.app.Fragment
import com.gtrab.qrscanmaster.usecase.BarcodeDatabase
import com.gtrab.qrscanmaster.usecase.BarcodeImageGenerator
import com.gtrab.qrscanmaster.usecase.BarcodeImageSaved
import com.gtrab.qrscanmaster.usecase.BarcodeParse
import com.gtrab.qrscanmaster.usecase.ScannerCameraHelper
import com.gtrab.qrscanmaster.usecase.Settings
import com.gtrab.qrscanmaster.usecase.SettingsGeneral
import com.gtrab.qrscanmaster.usecase.WifiConnector

val Fragment.settingGen get() = SettingsGeneral.getInstance(requireContext())
val scannerCameraHelper get() = ScannerCameraHelper
val barcodeParser get()= BarcodeParse
val Fragment.barcodeDatabase get()= BarcodeDatabase.getInstance(requireContext())
val barcodeImageGenerator get() = BarcodeImageGenerator
val barcodeImageSaved get()=BarcodeImageSaved
val Fragment.settings get()= Settings.getInstance(requireContext())
val wifiConnector get() = WifiConnector