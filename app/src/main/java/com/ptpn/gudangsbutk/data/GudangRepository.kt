package com.ptpn.gudangsbutk.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.ptpn.gudangsbutk.data.local.LocalDataSource
import com.ptpn.gudangsbutk.data.remote.RemoteDataSource

class GudangRepository private constructor(
        private val remoteDataSource: RemoteDataSource,
        private val localDataSource: LocalDataSource,
) {
    companion object {
        @Volatile
        private var instance: GudangRepository? = null

        fun getInstance(remoteData: RemoteDataSource, localData: LocalDataSource): GudangRepository =
                instance ?: synchronized(this) {
                    instance ?: GudangRepository(remoteData, localData).apply {
                        instance = this
                    }
                }
    }

    fun insertBarang(barang: Barang, imageUri: Uri): Boolean = remoteDataSource.insertBarang(barang, imageUri)
    fun updateBarang(barang: Barang, imageUri: Uri?): Boolean = remoteDataSource.updateBarang(barang, imageUri)
    fun getBarang(): LiveData<ArrayList<Barang>> = remoteDataSource.getBarang()
    fun deleteBarang(kode: String) = remoteDataSource.deleteBarang(kode)
    fun getListBarang() : LiveData<List<String>> = remoteDataSource.getListBarang()

    fun insertData(data: Data): Boolean = remoteDataSource.insertData(data)
    fun getDailyData(tanggal: String): LiveData<ArrayList<Data>> = remoteDataSource.getDailyData(tanggal)
    fun getAllData(): LiveData<ArrayList<Data>> = remoteDataSource.getAllData()
    fun getUserData(user: String): LiveData<ArrayList<Data>> = remoteDataSource.getUserData(user)
    fun deleteData(dataId: String): Boolean = remoteDataSource.deleteData(dataId)
}