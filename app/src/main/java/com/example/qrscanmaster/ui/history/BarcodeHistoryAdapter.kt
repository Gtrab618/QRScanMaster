package com.example.qrscanmaster.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscanmaster.databinding.ItemBarcodeHistoryBinding
import com.example.qrscanmaster.model.Barcode

class BarcodeHistoryAdapter (private val onBarcodeClicked: (Barcode)->Unit): PagingDataAdapter<Barcode,BarcodeHistoryAdapter.ViewHolder>(DiffUtilCallback){
    private lateinit var binding : ItemBarcodeHistoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemBarcodeHistoryBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //ver que hace esto
        getItem(position)?.also {
            holder.show(it,position== itemCount.dec())
        }
    }

    inner class ViewHolder(private val binding:ItemBarcodeHistoryBinding):RecyclerView.ViewHolder(binding.root){

        fun show(barcode: Barcode, isLastItem:Boolean){
            showText(barcode)
            itemView.setOnClickListener {
                onBarcodeClicked(barcode)
            }
        }


        private fun showText(barcode: Barcode){
            binding.txtQrText.text=barcode.text
        }


    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<Barcode>(){
        override fun areItemsTheSame(oldItem: Barcode, newItem: Barcode): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Barcode, newItem: Barcode): Boolean {
            return oldItem == newItem
        }

    }
}