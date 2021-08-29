package com.ptpn.gudangsbutk.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ptpn.gudangsbutk.databinding.FragmentSettingsBinding
import com.ptpn.gudangsbutk.ui.barang.AddBarangActivity
import com.ptpn.gudangsbutk.ui.barang.AddBarangActivity.Companion.EXTRA_RESULT_ADD
import com.ptpn.gudangsbutk.ui.barang.AddBarangActivity.Companion.RESULT_CODE_ADD_BARANG

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    companion object {
        private const val REQUEST_CODE_ADD_BARANG = 100
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddBarang.setOnClickListener {
            val addIntent = Intent(context, AddBarangActivity::class.java)
            startActivityForResult(addIntent, REQUEST_CODE_ADD_BARANG)
        }
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
    }

}