package com.gtrab.qrscanmaster.ui.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrMainBinding
import com.gtrab.qrscanmaster.extension.unsafeLazy
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.model.schema.Schema


class FragmentCreateQrMain : Fragment() {

    private lateinit var binding:FragmentCreateQrMainBinding
    private val barcodeFormat by unsafeLazy {
        BarcodeFormat.QR_CODE
    }

    var  isCreateBarcodeButtonEnabled:Boolean
        get()=false
        set(enable){
            val iconId= if (enable){
                R.drawable.confirm_qr_on
            }else{
                R.drawable.confirm_qr_off
            }
            binding.btnConfirm.setImageResource(iconId)
            binding.btnConfirm.isEnabled=enable
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCreateQrMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null){
            childFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateOptions())
                .commit()
        }
        initHandleButton()
    }
    
    private fun initHandleButton(){
        binding.btnConfirm.setOnClickListener {
            createBarcode()
        }
    }

    private fun createBarcode(){
        val schema = getCurrentFragment().getBarcodeSchema()
        createBarcode(schema)

    }

    private fun createBarcode(schema: Schema,finish:Boolean=false){
        val barcode = Barcode(
            text = schema.toBarcodeText(),
            formattedText = schema.toFormattedText(),
            format = barcodeFormat,
            schema = schema.schema,
            date = System.currentTimeMillis(),

        )
        
    }


    private fun getCurrentFragment(): CreateQrBase {
        return childFragmentManager.findFragmentById(R.id.fragment_container) as CreateQrBase
    }

}