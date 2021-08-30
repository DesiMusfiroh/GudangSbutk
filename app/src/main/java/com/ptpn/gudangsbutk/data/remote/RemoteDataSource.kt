package com.ptpn.gudangsbutk.data.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.Item

class RemoteDataSource {
    private val db = Firebase.firestore

    companion object {
        fun getInstance(): RemoteDataSource {
            return RemoteDataSource()
        }
        private const val TAG = "Remote Data Source"
    }

    fun insertBarang(barang: Barang, imageUri: Uri): Boolean {
        val fileName = StringBuilder("${barang.kode}.jpg")
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        storageReference.putFile(imageUri).addOnSuccessListener { uploadImage ->
            storageReference.downloadUrl.addOnSuccessListener { firebaseImageUri ->
                val newBarang = hashMapOf(
                        "kode" to barang.kode,
                        "nama" to barang.nama,
                        "keterangan" to barang.keterangan,
                        "image" to firebaseImageUri.toString()
                )
                db.collection("barang").document("${barang.kode}")
                        .set(newBarang)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            }
            Log.d(TAG, "sukses upload image $uploadImage")
        }.addOnFailureListener {
            Log.d(TAG, "gagal upload image $it")
        }
        return true
    }

    fun updateBarang(barang: Barang, imageUri: Uri?) : Boolean {
        if (imageUri != null) {
            val fileName = StringBuilder("${barang.kode}.jpg")
            val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
            storageReference.delete()
            storageReference.putFile(imageUri).addOnSuccessListener { uploadImage ->
                storageReference.downloadUrl.addOnSuccessListener { firebaseImageUri ->
                    val updateBarang = hashMapOf(
                        "kode" to barang.kode,
                        "nama" to barang.nama,
                        "keterangan" to barang.keterangan,
                        "image" to firebaseImageUri.toString()
                    )
                    db.collection("barang").document("${barang.kode}")
                        .set(updateBarang)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }
                Log.d(TAG, "sukses upload image $uploadImage")
            }.addOnFailureListener {
                Log.d(TAG, "gagal upload image $it")
            }
        } else {
            val updateBarang = hashMapOf(
                "kode" to barang.kode,
                "nama" to barang.nama,
                "keterangan" to barang.keterangan,
                "image" to barang.image,
            )
            db.collection("barang").document("${barang.kode}")
                .set(updateBarang)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
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
    }

    fun deleteBarang(kode: String) : Boolean {
        val fileName = StringBuilder("${kode}.jpg")
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        storageReference.delete()
        db.collection("barang").document(kode)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        return true
    }

    fun getListBarang() : LiveData<List<String>> {
        val results = MutableLiveData<List<String>>()
        db.collection("barang").addSnapshotListener { value, _ ->
            val listBarang = ArrayList<String>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    listBarang.add(dc.document["nama"].toString())
                }
            }
            results.postValue(listBarang)
        }
        return results
    }

    fun insertItem(item: Item) : Boolean {
        val newItem = hashMapOf(
                "id" to item.id,
                "user" to item.user,
                "tanggal" to item.tanggal,
                "sales" to item.sales,
                "barang" to item.barang,
                "jumlah" to item.jumlah,
                "satuan" to item.satuan,
                "keterangan" to item.keterangan,
                "added_time" to item.addedTime,
        )
        db.collection("item").document("${item.id}")
                .set(newItem)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        return true
    }
}