package com.ptpn.gudangsbutk.ui.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.ActivityBarangBinding
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory

class AddBarangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarangBinding
    private lateinit var viewModel: BarangViewModel

    companion object {
        const val RESULT_CODE_ADD_BARANG = 110
        const val EXTRA_RESULT_ADD = "result add"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Jenis Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[BarangViewModel::class.java]

        binding.btnSubmit.setOnClickListener{
            save()
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

            val barang = Barang(kode, nama, keterangan)
            viewModel.insert(barang).apply {
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