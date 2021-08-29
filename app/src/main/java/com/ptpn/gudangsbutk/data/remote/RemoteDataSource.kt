package com.ptpn.gudangsbutk.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ptpn.gudangsbutk.data.Barang
import kotlin.properties.Delegates

class RemoteDataSource {

    private val db = Firebase.firestore

    companion object {
        fun getInstance(): RemoteDataSource {
            return RemoteDataSource()
        }
        private const val TAG = "Remote Data Source"
    }

    fun insertBarang(barang: Barang): Boolean {
        val newBarang = hashMapOf(
            "kode" to barang.kode,
            "nama" to barang.nama,
            "keterangan" to barang.keterangan
        )
        db.collection("barang").document("${barang.kode}")
            .set(newBarang)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        return true
    }

}