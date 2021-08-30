package com.ptpn.gudangsbutk.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.ItemBarangHomeBinding

class HomeBarangAdapter(private val listBarang: ArrayList<Barang>, val context: Context) : RecyclerView.Adapter<HomeBarangAdapter.BarangViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Barang)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val binding = ItemBarangHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = listBarang[position]
        holder.bind(barang)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listBarang[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listBarang.size

    class BarangViewHolder(private val binding: ItemBarangHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(barang: Barang) {
            with(binding) {
                tvItemKode.text = barang.kode
                Glide.with(itemView.context)
                    .load(barang.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                    .into(imgItemImage)
            }
        }
    }
}