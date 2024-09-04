package com.gtrab.qrscanmaster.ui.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrMainBinding


class FragmentCreateQrMain : Fragment() {

    private lateinit var binding:FragmentCreateQrMainBinding

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
            Toast.makeText(requireContext(), "confirm", Toast.LENGTH_SHORT).show()
            println("acction")
        }
    }
}