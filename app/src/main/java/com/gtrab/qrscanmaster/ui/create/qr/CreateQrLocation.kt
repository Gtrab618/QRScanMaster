package com.gtrab.qrscanmaster.ui.create.qr

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrLocationBinding
import com.gtrab.qrscanmaster.extension.isNotBlank
import com.gtrab.qrscanmaster.extension.textString
import com.gtrab.qrscanmaster.model.schema.Geo
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.ui.create.CreateQrBase


class CreateQrLocation : CreateQrBase() {

    private lateinit var binding: FragmentCreateQrLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCreateQrLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        return Geo(latitude = binding.etLatitude.textString, longitude = binding.etLongitude.textString, altitude = null)
    }

    private fun handleTextChanged(){
        binding.etLatitude.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etLongitude.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentFragmen.isCreateBarcodeButtonEnabled=binding.etLatitude.isNotBlank() && binding.etLongitude.isNotBlank()

    }

}