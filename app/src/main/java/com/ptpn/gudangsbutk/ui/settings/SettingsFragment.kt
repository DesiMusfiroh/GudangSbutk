package com.ptpn.gudangsbutk.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.FragmentSettingsBinding
import com.ptpn.gudangsbutk.ui.barang.AddBarangActivity
import com.ptpn.gudangsbutk.ui.barang.AddBarangActivity.Companion.EXTRA_RESULT_ADD
import com.ptpn.gudangsbutk.ui.barang.AddBarangActivity.Companion.RESULT_CODE_ADD_BARANG
import com.ptpn.gudangsbutk.ui.barang.EditBarangActivity
import com.ptpn.gudangsbutk.ui.barang.EditBarangActivity.Companion.EXTRA_BARANG
import com.ptpn.gudangsbutk.ui.barang.EditBarangActivity.Companion.EXTRA_RESULT_EDIT
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var listBarang: ArrayList<Barang>

    companion object {
        private const val REQUEST_CODE_ADD_BARANG = 100
        private const val REQUEST_CODE_EDIT_BARANG = 200
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        populateBarang()

        binding.btnAddBarang.setOnClickListener {
            val addIntent = Intent(context, AddBarangActivity::class.java)
            startActivityForResult(addIntent, REQUEST_CODE_ADD_BARANG)
        }
    }

    fun populateBarang() {
        viewModel.getBarang().observe(viewLifecycleOwner, {
            listBarang = it
            if (it !== null) {
                barangAdapter = BarangAdapter(listBarang, requireContext())
                barangAdapter.notifyDataSetChanged()

                binding.apply {
                    rvBarang.layoutManager = LinearLayoutManager(context)
                    rvBarang.setHasFixedSize(true)
                    rvBarang.adapter = barangAdapter
                }
                barangAdapter.setOnItemClickCallback(object : BarangAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Barang) {
                        val editIntent = Intent(context, EditBarangActivity::class.java)
                        editIntent.putExtra(EXTRA_BARANG, data)
                        startActivityForResult(editIntent, REQUEST_CODE_EDIT_BARANG)
                    }
                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        populateBarang()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_BARANG && resultCode == RESULT_CODE_ADD_BARANG) {
            val result = data?.getBooleanExtra(EXTRA_RESULT_ADD, false)
            if (result == true) {
                Toast.makeText(context, "Berhasil menambah jenis barang!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal menambah jenis barang!", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_BARANG && resultCode == REQUEST_CODE_EDIT_BARANG) {
            val result = data?.getBooleanExtra(EXTRA_RESULT_EDIT, false)
            if (result == true) {
                Toast.makeText(context, "Berhasil mengedit jenis barang!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal mengedit jenis barang!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}