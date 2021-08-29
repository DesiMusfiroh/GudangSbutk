package com.ptpn.gudangsbutk.ui.barang

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.ActivityBarangBinding
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory

class EditBarangActivity : AppCompatActivity(){
    private lateinit var binding: ActivityBarangBinding
    private lateinit var viewModel: BarangViewModel
    private lateinit var barang: Barang

    companion object {
        const val RESULT_CODE_EDIT_BARANG = 210
        const val EXTRA_RESULT_EDIT = "result edit"
        const val EXTRA_BARANG = "extra_barang"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Edit Jenis Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[BarangViewModel::class.java]

        barang = intent.getParcelableExtra<Barang>(EXTRA_BARANG) as Barang
        populateBarang(barang)

        binding.btnSubmit.setOnClickListener{ save() }
    }

    private fun populateBarang(barang: Barang) {
        binding.apply {
            etKode.setText(barang.kode)
            etNama.setText(barang.nama)
            etKeterangan.setText(barang.keterangan)
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
            viewModel.update(barang).apply {
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_RESULT_EDIT, this)
                setResult(RESULT_CODE_EDIT_BARANG, resultIntent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                AlertDialog.Builder(this).apply {
                    setMessage(getString(R.string.delete_barang))
                    setNegativeButton(getString(R.string.no), null)
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.delete(barang.kode.toString())
                        finish()
                    }
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}