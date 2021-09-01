package com.ptpn.gudangsbutk.ui.form

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.ItemFormBinding
import java.lang.StringBuilder

class FormAdapter(private val listItem: ArrayList<Item>, val context: Context) : RecyclerView.Adapter<FormAdapter.ItemViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var onItemClickDelete: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Item)
        fun onItemDelete(data: Item)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
        this.onItemClickDelete = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ItemViewHolder(private val binding: ItemFormBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            with(binding) {
                tvBarang.text = item.barang
                tvJumlah.text = StringBuilder("Jumlah : ${item.jumlah} ${item.satuan}")
                btnDelete.setOnClickListener { onItemClickCallback.onItemDelete(item) }
            }
        }
    }
}