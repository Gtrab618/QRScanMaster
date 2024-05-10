package com.example.qrscanmaster.dependencies

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.qrscanmaster.usecase.SettingsGeneral

val Fragment.settingGen get() = SettingsGeneral.getInstance(requireContext())
