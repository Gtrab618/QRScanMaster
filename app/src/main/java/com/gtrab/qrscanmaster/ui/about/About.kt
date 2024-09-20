package com.gtrab.qrscanmaster.ui.about

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.gtrab.qrscanmaster.BuildConfig
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.databinding.FragmentAboutBinding
import java.util.Calendar


class About : Fragment() {

    private lateinit var binding:FragmentAboutBinding


     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAboutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdview()
        initData()
    }

    private fun initAdview(){
        val adRequest = AdRequest.Builder().build()
        binding.adViewAbout.loadAd(adRequest)
        initData()

    }

    private fun initData(){
        binding.txtVersion.text=getString(R.string.about_version,BuildConfig.VERSION_NAME)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        binding.txtCopyright.text= getString(R.string.about_copyright,currentYear.toString())
    }
}