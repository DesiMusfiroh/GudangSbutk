package com.ptpn.gudangsbutk.ui.barang

import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.GudangRepository

class BarangViewModel(private val repository: GudangRepository) : ViewModel() {
    fun insert(barang: Barang): Boolean = repository.writeBarang(barang)
    fun update(barang: Barang): Boolean = repository.writeBarang(barang)
    fun delete(kode: String): Boolean = repository.deleteBarang(kode)
}