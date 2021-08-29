package com.ptpn.gudangsbutk.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ptpn.gudangsbutk.data.Barang

class RemoteDataSource {
    private val db = Firebase.firestore

    companion object {
        fun getInstance(): RemoteDataSource {
            return RemoteDataSource()
        }
        private const val TAG = "Remote Data Source"
    }

    fun writeBarang(barang: Barang): Boolean {
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

    fun getBarang() : LiveData<ArrayList<Barang>> {
        val results = MutableLiveData<ArrayList<Barang>>()
        db.collection("barang").addSnapshotListener { value, error ->
            val listBarang = ArrayList<Barang>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    listBarang.add(dc.document.toObject(Barang::class.java))
                }
            }
            results.postValue(listBarang)
        }
        return results
    //        val query = db.collection("barang").orderBy("kode", Query.Direction.ASCENDING)
    //        val response = FirestoreRecyclerOptions.Builder<Barang>().setQuery(query, Barang::class.java).build()
    }

    fun deleteBarang(kode: String) : Boolean {
        db.collection("barang").document(kode)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        return true
    }
}