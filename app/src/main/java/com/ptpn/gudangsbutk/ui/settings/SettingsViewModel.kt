package com.ptpn.gudangsbutk.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.GudangRepository

class SettingsViewModel(private val repository: GudangRepository) : ViewModel() {
    fun getBarang() : LiveData<ArrayList<Barang>> = repository.getBarang()
}