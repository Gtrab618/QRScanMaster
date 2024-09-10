package com.gtrab.qrscanmaster.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateOptionsBinding
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrCodeApp
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrEmail
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrLocation
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrSms
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrUrl
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrVcard
import com.gtrab.qrscanmaster.ui.create.qr.CreateQrWifi
import com.hbb20.CountryCodePicker


class CreateOptions : Fragment() {

    private lateinit var  binding:FragmentCreateOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCreateOptionsBinding.inflate(inflater,container,false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action()
    }

    private fun action(){

        binding.btnAppCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrCodeApp())
                .commit()
        }

        binding.btnEmailCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrEmail())
                .commit()
        }

        binding.btnSmsCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrSms())
                .commit()
        }

        binding.btnUrlCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrUrl())
                .commit()
        }
        binding.btnWifiCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrWifi())
                .commit()
        }
        binding.btnVcardCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrVcard())
                .commit()
        }

        binding.btnGeoCrea.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateQrLocation())
                .commit()
        }
    }
}