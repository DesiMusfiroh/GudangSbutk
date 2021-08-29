package com.ptpn.gudangsbutk.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.ItemBarangBinding

class BarangAdapter(private val listBarang: ArrayList<Barang>, val context: Context) : RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Barang)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val binding = ItemBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class BarangViewHolder(private val binding: ItemBarangBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(barang: Barang) {
            with(binding) {
                tvNama.text = barang.nama
                tvKode.text = barang.kode
            }
        }
    }
}