package com.gtrab.qrscanmaster.ui.create.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrUrlBinding
import com.gtrab.qrscanmaster.extension.isNotBlank
import com.gtrab.qrscanmaster.extension.textString
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.model.schema.Url
import com.gtrab.qrscanmaster.ui.create.CreateQrBase


class CreateQrUrl : CreateQrBase() {

    private lateinit var binding:FragmentCreateQrUrlBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCreateQrUrlBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
    }

    private fun  handleTextChanged(){
        binding.etUrl.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentFragmen.isCreateBarcodeButtonEnabled = binding.etUrl.isNotBlank()
    }

    override fun getBarcodeSchema(): Schema {
        return Url(url=binding.etUrl.textString)
    }
}