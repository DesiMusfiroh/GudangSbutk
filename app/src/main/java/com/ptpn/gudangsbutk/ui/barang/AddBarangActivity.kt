package com.ptpn.gudangsbutk.ui.barang

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.ActivityBarangBinding
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class AddBarangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarangBinding
    private lateinit var viewModel: BarangViewModel
    private lateinit var imageUri: Uri

    companion object {
        const val RESULT_CODE_ADD_BARANG = 110
        const val EXTRA_RESULT_ADD = "result add"
        const val REQUEST_CODE_CHOOSE_IMAGE = 200
        const val PERMISSION_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Jenis Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[BarangViewModel::class.java]

        binding.btnSubmit.setOnClickListener{ save() }
        binding.btnChooseImage.setOnClickListener { chooseImage() }
    }

    private fun chooseImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else{
                selectImage()
            }
        } else {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
            imageUri = data?.data!!
            binding.ivImage.visibility = View.VISIBLE
            binding.warningImage.visibility = View.GONE
            binding.ivImage.setImageURI(imageUri)
        }
    }

    private fun save() {
        binding.apply {
            val nama: String = etNama.text.toString()
            val kode: String = etKode.text.toString()
            val keterangan: String = etKeterangan.text.toString()

            if (kode.isEmpty()) {
                etKode.error = "Mohon diisi terlebih dahulu!"
                etKode.requestFocus()
                return
            }
            if (nama.isEmpty()) {
                etNama.error = "Mohon diisi terlebih dahulu!"
                etNama.requestFocus()
                return
            }

            if (!::imageUri.isInitialized) {
                warningImage.visibility = View.VISIBLE
                warningImage.requestFocus()
                return
            }

            val barang = Barang(kode, nama, keterangan, "")
            viewModel.insert(barang, imageUri).apply {
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_RESULT_ADD, this)
                setResult(RESULT_CODE_ADD_BARANG, resultIntent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}