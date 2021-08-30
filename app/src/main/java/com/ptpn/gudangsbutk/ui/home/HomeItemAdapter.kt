package com.ptpn.gudangsbutk.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.ItemDataHomeBinding
import java.lang.StringBuilder

class HomeItemAdapter(private val listItem: ArrayList<Item>, val context: Context) : RecyclerView.Adapter<HomeItemAdapter.ItemViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Item)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDataHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = listItem[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listItem[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listItem.size

    class ItemViewHolder(private val binding: ItemDataHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            with(binding) {
                tvBarang.text = item.barang
                tvSales.text = item.sales
                tvAddedTime.text = item.addedTime
                tvJumlah.text = StringBuilder("${item.jumlah} ${item.satuan}")
            }
        }
    }
}