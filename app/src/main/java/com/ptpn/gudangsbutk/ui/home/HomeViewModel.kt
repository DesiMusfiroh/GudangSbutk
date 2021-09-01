package com.ptpn.gudangsbutk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.data.GudangRepository

class HomeViewModel(private val repository: GudangRepository) : ViewModel() {
    fun getBarang() : LiveData<ArrayList<Barang>> = repository.getBarang()
    fun getDailyData(tanggal: String): LiveData<ArrayList<Data>> = repository.getDailyData(tanggal)
}