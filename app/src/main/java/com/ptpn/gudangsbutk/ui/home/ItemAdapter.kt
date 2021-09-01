package com.ptpn.gudangsbutk.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.ItemDialogDataBinding
import java.lang.StringBuilder

class ItemAdapter(private val listItem: ArrayList<Item>, val context: Context) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private var no = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDialogDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = listItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = listItem.size

    inner class ItemViewHolder(private val binding: ItemDialogDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            with(binding) {
                no += 1
                tvNo.text = StringBuilder("$no.")
                tvBarang.text = item.barang
                tvJumlah.text = StringBuilder("${item.jumlah} ${item.satuan}")
            }
        }
    }
}