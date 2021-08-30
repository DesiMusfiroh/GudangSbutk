package com.ptpn.gudangsbutk.ui.barang

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.ActivityBarangBinding
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.util.*

@Suppress("DEPRECATION")
class EditBarangActivity : AppCompatActivity(){
    private lateinit var binding: ActivityBarangBinding
    private lateinit var viewModel: BarangViewModel
    private lateinit var barang: Barang
    private var imageUri: Uri? = null

    companion object {
        const val RESULT_CODE_EDIT_BARANG = 210
        const val EXTRA_RESULT_EDIT = "result edit"
        const val EXTRA_BARANG = "extra_barang"
        const val REQUEST_CODE_CHOOSE_IMAGE = 200
        const val PERMISSION_CODE = 1000
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
        binding.btnChooseImage.setOnClickListener { chooseImage() }
    }

    private fun populateBarang(barang: Barang) {
        binding.apply {
            etKode.setText(barang.kode)
            etNama.setText(barang.nama)
            etKeterangan.setText(barang.keterangan)

            if (barang.image != null) {
                ivImage.visibility = View.VISIBLE
                Glide.with(this@EditBarangActivity).load(barang.image).into(ivImage)
            }
        }
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

            val barangUpdate = Barang(kode, nama, keterangan, barang.image)
            viewModel.update(barangUpdate, imageUri).apply {
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