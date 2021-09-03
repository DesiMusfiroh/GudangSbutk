package com.ptpn.gudangsbutk.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rekap (
    val tanggal: String? = null,
    val barang: String? = null,
    val dus: Int? = null,
    val pcs: Int? = null,
    val paket: Int? = null,
) : Parcelable