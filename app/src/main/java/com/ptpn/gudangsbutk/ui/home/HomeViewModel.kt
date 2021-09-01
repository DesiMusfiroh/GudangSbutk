package com.ptpn.gudangsbutk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.data.ItemLama

class HomeViewModel(private val repository: GudangRepository) : ViewModel() {
    fun getBarang() : LiveData<ArrayList<Barang>> = repository.getBarang()
    fun getDailyItem(tanggal: String): LiveData<ArrayList<ItemLama>> = repository.getDailyItem(tanggal)
}