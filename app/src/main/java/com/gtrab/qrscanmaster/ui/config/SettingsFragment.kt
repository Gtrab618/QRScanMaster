package com.gtrab.qrscanmaster.ui.config

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.dependencies.settings

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val vibratePreference =findPreference<SwitchPreferenceCompat>("vibrate")
        vibratePreference?.setOnPreferenceChangeListener { _, newValue ->
            settings.vibrate = newValue as Boolean
            true
        }

        val copyToClipboardPreference = findPreference<SwitchPreferenceCompat>("copyClipBoard")
        copyToClipboardPreference?.setOnPreferenceChangeListener{_,newValue ->
            settings.copyToClipboard=newValue as Boolean
            true
        }

        val confirmScansManuallyPrefence= findPreference<SwitchPreferenceCompat>("confirmScanManual")
        confirmScansManuallyPrefence?.setOnPreferenceChangeListener { _, newValue ->
            settings.confirmScansManually= newValue as Boolean
            true
        }

        val saveCreatedBarcodesPreference = findPreference<SwitchPreferenceCompat>("saveCreateBarcode")
        saveCreatedBarcodesPreference?.setOnPreferenceChangeListener { _, newValue ->
            settings.saveCreatedBarcodesToHistory=newValue as Boolean
            true
        }

        val saveScanBarcodePreference= findPreference<SwitchPreferenceCompat>("saveScanBarcode")
        saveScanBarcodePreference?.setOnPreferenceChangeListener{_, newValue ->
            settings.saveScanBarcodeToHistory= newValue as Boolean
            true
        }
    }
}