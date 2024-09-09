package com.gtrab.qrscanmaster.ui.create.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrEmailBinding
import com.gtrab.qrscanmaster.extension.isNotBlank
import com.gtrab.qrscanmaster.extension.textString
import com.gtrab.qrscanmaster.model.schema.Email
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.ui.create.CreateQrBase


class CreateQrEmail : CreateQrBase() {

    private lateinit var binding: FragmentCreateQrEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCreateQrEmailBinding.inflate(inflater,container,false)
        return binding.root
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
    }

    private fun handleTextChanged() {
        binding.etSubject.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etEmail.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.etMessage.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    override fun getBarcodeSchema(): Schema {
        return Email(email=binding.etEmail.textString, subject = binding.etSubject.textString, body = binding.etMessage.textString)
    }
    private fun toggleCreateBarcodeButton() {
        parentFragmen.isCreateBarcodeButtonEnabled = binding.etEmail.isNotBlank() || binding.etSubject.isNotBlank() || binding.etMessage.isNotBlank()
    }

}