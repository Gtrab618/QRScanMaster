package com.example.qrscanmaster.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.qrscanmaster.R
import com.example.qrscanmaster.comunication.Communicator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Home : Fragment() {

    private lateinit var comm: Communicator
    private lateinit var btn:Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewHome= inflater.inflate(R.layout.fragment_home, container, false)
        comm = requireActivity() as Communicator
        btn=viewHome.findViewById(R.id.button2)
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


}