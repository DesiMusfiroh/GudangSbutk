package com.ptpn.gudangsbutk.data

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

    fun insertBarang(barang: Barang): Boolean = remoteDataSource.insertBarang(barang)

}