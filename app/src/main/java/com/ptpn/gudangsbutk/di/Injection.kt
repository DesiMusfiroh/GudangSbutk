package com.ptpn.gudangsbutk.di

import android.content.Context
import com.ptpn.gudangsbutk.data.GudangRepository
import com.ptpn.gudangsbutk.data.local.GudangDatabase
import com.ptpn.gudangsbutk.data.local.LocalDataSource
import com.ptpn.gudangsbutk.data.remote.RemoteDataSource

object Injection {
    fun provideRepository(context: Context): GudangRepository {
        val database = GudangDatabase.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val localDataSource = LocalDataSource.getInstance(database.surveyDao())
        return GudangRepository.getInstance(remoteDataSource, localDataSource)
    }
}