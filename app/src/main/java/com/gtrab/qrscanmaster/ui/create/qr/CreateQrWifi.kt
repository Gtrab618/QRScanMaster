package com.gtrab.qrscanmaster.ui.create.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrUrlBinding
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrWifiBinding
import com.gtrab.qrscanmaster.extension.isNotBlank
import com.gtrab.qrscanmaster.extension.textString
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.model.schema.Wifi
import com.gtrab.qrscanmaster.ui.create.CreateQrBase


class CreateQrWifi : CreateQrBase() {
    private lateinit var binding: FragmentCreateQrWifiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateQrWifiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEncryptionTypesSpinner()
        handleTextChanged()
    }

    private fun initEncryptionTypesSpinner() {
        binding.spnEncryp.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.fragment_create_qr_code_wifi_encryption_types,
            R.layout.item_spn_wifi
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
        binding.spnEncryp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.etNetworkPass.isVisible = position != 2
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun handleTextChanged() {
        binding.etNetworkName.addTextChangedListener {toggleCreateBarcodeButton() }
        binding.etNetworkPass.addTextChangedListener { toggleCreateBarcodeButton()}
    }

    private fun toggleCreateBarcodeButton() {
        parentFragmen.isCreateBarcodeButtonEnabled = binding.etNetworkName.isNotBlank()
    }

    override fun getBarcodeSchema(): Schema {
        val encryption = when (binding.spnEncryp.selectedItemPosition) {
            0 -> "WPA"
            1 -> "WEP"
            2 -> "nopass"
            else -> "nopass"
        }
        return Wifi(
            encryption = encryption, name = binding.etNetworkName.textString,
            password = binding.etNetworkPass.textString, isHidden = binding.chcBoxHidden.isChecked
        )
    }
}