package com.ptpn.gudangsbutk.data.local

class LocalDataSource(private val gudangDao: GudangDao) {
    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(gudangDao: GudangDao): LocalDataSource =
                INSTANCE ?: LocalDataSource(gudangDao)
    }
}