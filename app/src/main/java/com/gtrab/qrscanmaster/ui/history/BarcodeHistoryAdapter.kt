package com.gtrab.qrscanmaster.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gtrab.qrscanmaster.databinding.ItemBarcodeHistoryBinding
import com.gtrab.qrscanmaster.extension.toImageId
import com.gtrab.qrscanmaster.model.Barcode
import java.text.SimpleDateFormat
import java.util.Locale

class BarcodeHistoryAdapter (private val onBarcodeClicked: (Barcode)->Unit): PagingDataAdapter<Barcode,BarcodeHistoryAdapter.ViewHolder>(DiffUtilCallback){
    private lateinit var binding : ItemBarcodeHistoryBinding
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

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
        //02 isLastItem ver si le doy una usabilidad cuando no halla mas items por ahora no se usa
        fun show(barcode: Barcode, isLastItem:Boolean){
            setClickListener(barcode)
            showText(barcode)
            showDate(barcode)
            showName(barcode)
            showIsFavorite(barcode)
            showImage(barcode)
            showFormat(barcode)
        }

        private fun setClickListener(barcode: Barcode){
            itemView.setOnClickListener {
                onBarcodeClicked(barcode)
            }
        }

        private fun showText(barcode: Barcode){

            binding.txtQrText.text=barcode.name ?: barcode.formattedText
        }

        private fun showDate(barcode: Barcode){
            binding.txtDateHisto.text=dateFormatter.format(barcode.date)
        }

        private fun showName(barcode: Barcode){
            binding.txtName.isVisible=true
            binding.txtName.text=barcode.schema.name
        }

        private fun showIsFavorite(barcode: Barcode){
            binding.imgFavorite.isVisible=barcode.isFavorite
        }

        private fun showImage(barcode: Barcode){
            val imageId= barcode.schema.toImageId()
            val image = AppCompatResources.getDrawable(itemView.context,imageId)
            binding.imageView2.setImageDrawable(image)
        }

        private fun showFormat(barcode: Barcode){
            binding.txtTypeQr.text=barcode.format.toString()
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