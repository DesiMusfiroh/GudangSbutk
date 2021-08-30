package com.ptpn.gudangsbutk.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
        val id: String? = null,
        val user: String? = null,
        val tanggal: String? = null,
        val sales: String? = null,
        val barang: String? = null,
        val jumlah: String? = null,
        val satuan: String? = null,
        val keterangan: String? = null,
        val addedTime: String? = null,
) : Parcelable