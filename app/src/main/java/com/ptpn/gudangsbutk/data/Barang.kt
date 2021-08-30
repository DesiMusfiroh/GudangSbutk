package com.ptpn.gudangsbutk.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barang (
    val kode: String? = null,
    val nama: String? = null,
    val keterangan: String? = null,
    val image: String? = null,
) : Parcelable