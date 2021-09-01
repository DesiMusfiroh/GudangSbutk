package com.ptpn.gudangsbutk.ui.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.data.ItemLama

class FormViewModel(private val repository: GudangRepository) : ViewModel() {
    fun getListBarang() : LiveData<List<String>> = repository.getListBarang()
    fun insertData(data: Data) : Boolean = repository.insertData(data)
}