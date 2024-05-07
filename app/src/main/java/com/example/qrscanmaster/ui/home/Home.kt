package com.example.qrscanmaster.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.qrscanmaster.R
import com.example.qrscanmaster.comunication.Communicator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Home : Fragment() {

    //private lateinit var comm: Communicator
    //private lateinit var btn:Button
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView:CodeScannerView
    private lateinit var btnGalery:ImageButton
    private lateinit var btnCameraFront:ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewHome= inflater.inflate(R.layout.fragment_home, container, false)

        //comm = requireActivity() as Communicator
        //btn=viewHome.findViewById(R.id.button2)
       /* val txt= viewHome.findViewById<EditText>(R.id.editTextText)
        val enterBtn=viewHome.findViewById<Button>(R.id.button)
        enterBtn.setOnClickListener{
            comm.passInfoQr(txt.text.toString())
        }*/

     /*   btn.setOnClickListener{
            IntentIntegrator.forSupportFragment(this@Home).initiateScan()
        }*/
        return viewHome
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        initScanner()

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun initScanner(){
        //inicialziar el lector

        codeScanner = CodeScanner(requireActivity(), scannerView).apply {
            //escaneo mas suave en vez de cada rato
            autoFocusMode=AutoFocusMode.CONTINUOUS
            //escanear solo 1 qr
            scanMode=ScanMode.SINGLE
            //se auto ajuste la camara
            isAutoFocusEnabled = true
            //tocar para ajustar
            isTouchFocusEnabled = true

        }

        //que se hace con el codigo
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        //aberiguar que hace esto
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun btn(){
        btnGalery.setOnClickListener {
            Toast.makeText(requireContext(), "galery", Toast.LENGTH_SHORT).show()
        }
        
    }




}