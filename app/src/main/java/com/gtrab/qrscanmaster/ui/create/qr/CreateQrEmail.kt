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
        binding.txtSubject.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.txtEmail.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.txtMessage.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    override fun getBarcodeSchema(): Schema {
        return Email(email=binding.txtEmail.textString, subject = binding.txtSubject.textString, body = binding.txtMessage.textString)
    }
    private fun toggleCreateBarcodeButton() {
        parentFragmen.isCreateBarcodeButtonEnabled = binding.txtEmail.isNotBlank() || binding.txtSubject.isNotBlank() || binding.txtMessage.isNotBlank()
    }

}