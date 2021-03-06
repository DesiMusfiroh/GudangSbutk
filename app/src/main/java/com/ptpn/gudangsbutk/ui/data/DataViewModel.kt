package com.ptpn.gudangsbutk.ui.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.data.Rekap

class DataViewModel(private val repository: GudangRepository) : ViewModel() {
    fun getAllData(): LiveData<ArrayList<Data>> = repository.getAllData()
    fun getUserData(user: String): LiveData<ArrayList<Data>> = repository.getUserData(user)
    fun deleteData(dataId: String): Boolean = repository.deleteData(dataId)
    fun getRekap(tanggal: String): LiveData<ArrayList<Rekap>> = repository.getRekap(tanggal)
}