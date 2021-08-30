package com.ptpn.gudangsbutk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.di.Injection
import com.ptpn.gudangsbutk.ui.barang.BarangViewModel
import com.ptpn.gudangsbutk.ui.data.DataViewModel
import com.ptpn.gudangsbutk.ui.form.FormViewModel
import com.ptpn.gudangsbutk.ui.home.HomeViewModel
import com.ptpn.gudangsbutk.ui.settings.SettingsViewModel

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
            modelClass.isAssignableFrom(BarangViewModel::class.java) -> {
                BarangViewModel(mGudangRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(mGudangRepository) as T
            }
            modelClass.isAssignableFrom(FormViewModel::class.java) -> {
                FormViewModel(mGudangRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(mGudangRepository) as T
            }
            modelClass.isAssignableFrom(DataViewModel::class.java) -> {
                DataViewModel(mGudangRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}