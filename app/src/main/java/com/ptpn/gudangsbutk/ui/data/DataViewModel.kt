package com.ptpn.gudangsbutk.ui.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.data.ItemLama

class DataViewModel(private val repository: GudangRepository) : ViewModel() {
    fun getAllItem(): LiveData<ArrayList<ItemLama>> = repository.getAllItem()
    fun getUserItem(user: String): LiveData<ArrayList<ItemLama>> = repository.getUserItem(user)
}