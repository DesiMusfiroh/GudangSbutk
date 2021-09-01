package com.ptpn.gudangsbutk.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
        val barang: String? = null,
        val jumlah: String? = null,
        val satuan: String? = null,
        val catatan: String? = null,
) : Parcelable