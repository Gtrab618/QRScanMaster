package com.example.qrscanmaster.ui.infoqr


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.example.qrscanmaster.R
import com.example.qrscanmaster.model.QRCodeResult

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
    private var param1: QRCodeResult? = null
    private lateinit var btnEditName: ImageButton
    private lateinit var btnFavorito: ImageButton
    private lateinit var btnDelete: ImageButton
    private lateinit var ivQrImage:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_PARAM1, QRCodeResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(ARG_PARAM1)
            }

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
        fun newInstance(param1: QRCodeResult?) =
            InfoQr().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)

                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnEditName=view.findViewById(R.id.btnEditName)
        btnDelete=view.findViewById(R.id.btnDelete)
        btnFavorito=view.findViewById(R.id.btnFavorite)
        ivQrImage=view.findViewById(R.id.ivQrImage)
        ivQrImage.setImageBitmap(param1?.qrImage)
        initMenuBar()

    }
    private fun initMenuBar() {
        btnEditName.setOnClickListener {
            Toast.makeText(requireContext(), param1?.textInfo ?: "vacio", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
        }

        btnFavorito.setOnClickListener {
            Toast.makeText(requireContext(), "favovito", Toast.LENGTH_SHORT).show()
        }
    }
}