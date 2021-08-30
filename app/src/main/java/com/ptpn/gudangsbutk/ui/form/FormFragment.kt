package com.ptpn.gudangsbutk.ui.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.FragmentFormBinding
import com.ptpn.gudangsbutk.utils.DatePickerFragment
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class FormFragment : Fragment(), DatePickerFragment.DialogDateListener, View.OnClickListener {
    private lateinit var binding: FragmentFormBinding
    private lateinit var viewModel: FormViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var tanggal: String
    private var barangAdapter: ArrayAdapter<String>? = null
    private var satuanAdapter: ArrayAdapter<String>? = null
    private lateinit var barangSelected: String
    private lateinit var satuanSelected: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[FormViewModel::class.java]

        mAuth = FirebaseAuth.getInstance()
        val sales = mAuth.currentUser?.displayName
        binding.etSales.setText(sales)

        val date = Calendar.getInstance().time
        val datetimeFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tanggal = datetimeFormat.format(date)
        binding.tvTanggal.text = tanggal

        getListBarang()
        getListSatuan()
        binding.btnSubmit.setOnClickListener(this)
        binding.btnTanggal.setOnClickListener(this)
    }

    private fun getListSatuan() {
        val arraySatuan = ArrayList<String>(3)
        arraySatuan.add("Dus")
        arraySatuan.add("Pcs")
        arraySatuan.add("Paket")
        satuanAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, arraySatuan) }
        binding.actvSatuan.setAdapter(satuanAdapter)
        binding.apply {
            actvSatuan.setAdapter(satuanAdapter)
            actvSatuan.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ -> satuanSelected = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    private fun getListBarang() {
        viewModel.getListBarang().observe(viewLifecycleOwner, { listBarang ->
            barangAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, listBarang) }
            binding.actvBarang.setAdapter(barangAdapter)
        })

        binding.apply {
            actvBarang.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id -> barangSelected = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tanggal = dateFormat.format(calendar.time)
        binding.tvTanggal.text = tanggal
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_submit -> {
                saveData()
            }
            R.id.btn_tanggal -> {
                Toast.makeText(context, "date picker", Toast.LENGTH_SHORT).show()
                val dialogFragment = DatePickerFragment()
                dialogFragment.show(parentFragmentManager, "datePicker")
            }
        }
    }

    private fun saveData() {
        val id = UUID.randomUUID().toString()
        val user = mAuth.currentUser?.email
        val date = Calendar.getInstance().time
        val datetimeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
        val addedTime = datetimeFormat.format(date)

        binding.apply {
            val sales: String = etSales.text.toString()
            val keterangan: String = etKeterangan.text.toString()
            val tanggal: String = tvTanggal.text.toString()
            val jumlah: String = etJumlah.text.toString()

            if (sales.isEmpty()) {
                etSales.error = "Mohon diisi terlebih dahulu!"
                etSales.requestFocus()
                return
            }

            if (!::barangSelected.isInitialized) {
                actvBarang.error = "Mohon pilih jenis barang terlebih dahulu!"
                actvBarang.requestFocus()
                return
            }

            if (jumlah.isEmpty()) {
                etJumlah.error = "Mohon diisi terlebih dahulu!"
                etJumlah.requestFocus()
                return
            }

            if (!::satuanSelected.isInitialized) {
                actvSatuan.error = "Mohon pilih satuan barang terlebih dahulu!"
                actvSatuan.requestFocus()
                return
            }

            val barang = barangSelected
            val satuan = satuanSelected

            val item = Item(id, user, tanggal, sales, barang, jumlah, satuan, keterangan, addedTime)
            viewModel.insert(item).apply {

            }
        }
    }
}