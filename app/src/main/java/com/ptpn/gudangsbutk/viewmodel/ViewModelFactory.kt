package com.ptpn.gudangsbutk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.di.Injection
import com.ptpn.gudangsbutk.ui.barang.AddBarangViewModel

class ViewModelFactory private constructor(private val mGudangRepository: GudangRepository)
    : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
                instance ?: synchronized(this) {
                    ViewModelFactory(Injection.provideRepository(context)).apply { instance = this }
                }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddBarangViewModel::class.java) -> {
                AddBarangViewModel(mGudangRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}