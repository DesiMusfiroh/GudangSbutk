package com.ptpn.gudangsbutk.ui.barang

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.GudangRepository

class BarangViewModel(private val repository: GudangRepository) : ViewModel() {
    fun insert(barang: Barang, imageUri: Uri): Boolean = repository.insertBarang(barang, imageUri)
    fun update(barang: Barang, imageUri: Uri?): Boolean = repository.updateBarang(barang, imageUri)
    fun delete(kode: String): Boolean = repository.deleteBarang(kode)
}