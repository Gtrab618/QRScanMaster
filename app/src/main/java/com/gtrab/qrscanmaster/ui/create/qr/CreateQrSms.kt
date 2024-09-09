package com.gtrab.qrscanmaster.ui.create.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrSmsBinding
import com.gtrab.qrscanmaster.extension.isNotBlank
import com.gtrab.qrscanmaster.extension.textString
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.model.schema.Sms
import com.gtrab.qrscanmaster.ui.create.CreateQrBase


class CreateQrSms : CreateQrBase() {

    private lateinit var binding:FragmentCreateQrSmsBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCreateQrSmsBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
    }

    private  fun handleTextChanged(){
        binding.etPhone.addTextChangedListener {toggleCreateBarcodeButton()  }
        binding.etSubject.addTextChangedListener { toggleCreateBarcodeButton() }

    }

    private fun toggleCreateBarcodeButton() {
        parentFragmen.isCreateBarcodeButtonEnabled = binding.etPhone.isNotBlank() || binding.etSubject.isNotBlank()
    }

    override fun getBarcodeSchema(): Schema {
        return Sms(phone=binding.etPhone.textString,binding.etSubject.textString)
    }
}