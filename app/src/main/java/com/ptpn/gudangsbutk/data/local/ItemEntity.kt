package com.ptpn.gudangsbutk.data.local

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item")
data class ItemEntity (
    @PrimaryKey
    @NonNull
    val id: String,

    @ColumnInfo(name = "added_time")
    val addedTime: String? = null,
) : Parcelable