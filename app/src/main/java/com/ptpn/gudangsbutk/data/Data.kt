package com.ptpn.gudangsbutk.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
        val id: String? = null,
        val user: String? = null,
        val tanggal: String? = null,
        val sales: String? = null,
        val keterangan: String? = null,
        val addedTime: String? = null,
        val item: ArrayList<Item>?
) : Parcelable