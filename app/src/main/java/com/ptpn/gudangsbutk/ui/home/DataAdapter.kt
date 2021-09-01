package com.ptpn.gudangsbutk.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.databinding.ItemDataBinding
import java.lang.StringBuilder

class DataAdapter(private val listItem: ArrayList<Data>, val context: Context) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Data)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = listItem[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listItem[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listItem.size

    class DataViewHolder(private val binding: ItemDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            with(binding) {
                tvSales.text = data.sales
                tvAddedTime.text = data.addedTime
                tvKeterangan.text = data.keterangan
                tvJumlah.text = StringBuilder("${data.item?.size} item")
            }
        }
    }
}