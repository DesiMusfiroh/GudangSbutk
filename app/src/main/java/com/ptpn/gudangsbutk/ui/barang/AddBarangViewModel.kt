package com.ptpn.gudangsbutk.ui.barang

import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.GudangRepository

class AddBarangViewModel(private val repository: GudangRepository) : ViewModel() {
    fun insert(barang: Barang): Boolean = repository.insertBarang(barang)
}