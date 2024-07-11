package com.example.qrscanmaster.ui.infoqr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.qrscanmaster.R
import com.example.qrscanmaster.model.Barcode
import com.example.qrscanmaster.model.ParsedBarcode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoQr.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoQr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Barcode? = null
    private lateinit var btnEditName: ImageButton
    private lateinit var btnFavorito: ImageButton
    private lateinit var btnDelete: ImageButton
    private lateinit var txtSchemaName:TextView
    private lateinit var txtContent:TextView
    private  var barcode:ParsedBarcode? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as Barcode?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_qr, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment InfoQr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Barcode) =
            InfoQr().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)

                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnEditName=view.findViewById(R.id.btnEditName)
        btnDelete=view.findViewById(R.id.btnDelete)
        btnFavorito=view.findViewById(R.id.btnFavorite)
        txtSchemaName=view.findViewById(R.id.txtSchemaType)
        txtContent=view.findViewById(R.id.txtContent)
        parseBarcodeInfo()
        initMenuBar()

    }


    private fun initMenuBar() {

        txtSchemaName.text= param1?.schema.toString()
        txtContent.text= barcode?.formattedText
        btnEditName.setOnClickListener {
            Toast.makeText(requireContext(), "test", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
        }

        btnFavorito.setOnClickListener {
            Toast.makeText(requireContext(), "favovito", Toast.LENGTH_SHORT).show()
        }
    }


    private fun parseBarcodeInfo(){
        barcode= param1?.let { ParsedBarcode(it) }
    }


}