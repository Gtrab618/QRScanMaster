package com.gtrab.qrscanmaster.ui.create.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrVcardBinding
import com.gtrab.qrscanmaster.extension.isNotBlank
import com.gtrab.qrscanmaster.extension.textString
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.model.schema.VCard
import com.gtrab.qrscanmaster.ui.create.CreateQrBase


class CreateQrVcard : CreateQrBase() {

    private lateinit var binding:FragmentCreateQrVcardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCreateQrVcardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindCodeNumber()
        parentFragmen.isCreateBarcodeButtonEnabled=true
    }

    private fun bindCodeNumber(){

        binding.ccp.registerCarrierNumberEditText(binding.etPhone)
    }
    override fun getBarcodeSchema(): Schema {
       val number= if(binding.etPhone.isNotBlank()) {
           binding.ccp.selectedCountryCodeWithPlus+binding.etPhone.textString
       }else{
           ""
       }


       return VCard(firstName = binding.txtFirstName.textString, lastName = binding.txtLastName.textString, email = binding.txtEmailVcard.textString, phone = number)
    }


}