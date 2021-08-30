package com.ptpn.gudangsbutk.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.FragmentHomeBinding
import com.ptpn.gudangsbutk.utils.generateFile
import com.ptpn.gudangsbutk.utils.goToFileIntent
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var barangAdapter: HomeBarangAdapter
    private lateinit var itemAdapter: HomeItemAdapter
    private lateinit var tanggal: String
    private lateinit var itemResponse: List<Item>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val date = Calendar.getInstance().time
        val datetimeFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val currentDate = datetimeFormat.format(date)

        val tanggalFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tanggal = tanggalFormat.format(date)

        binding.tvUser.text = getString(R.string.hai_user, currentUser?.displayName)
        binding.tvDate.text = currentDate
        Glide.with(requireContext()).load(currentUser?.photoUrl).apply(RequestOptions.circleCropTransform()).into(binding.btnUser)

        populateBarang()
        populateItem()
        binding.btnExportExcel.setOnClickListener { exportExcel() }
    }

    private fun populateItem() {
        viewModel.getDailyItem(tanggal).observe(viewLifecycleOwner, { listItem ->
            itemResponse = listItem
            if (listItem !== null) {
                itemAdapter = HomeItemAdapter(listItem, requireContext())
                itemAdapter.notifyDataSetChanged()

                binding.apply {
                    rvItem.layoutManager = LinearLayoutManager(context)
                    rvItem.setHasFixedSize(true)
                    rvItem.adapter = itemAdapter
                }
                itemAdapter.setOnItemClickCallback(object : HomeItemAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Item) {

                    }
                })
            }
        })
    }

    private fun populateBarang() {
        viewModel.getBarang().observe(viewLifecycleOwner, { listBarang ->
            if (listBarang !== null) {
                barangAdapter = HomeBarangAdapter(listBarang, requireContext())
                barangAdapter.notifyDataSetChanged()

                binding.apply {
                    rvBarang.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    rvBarang.setHasFixedSize(true)
                    rvBarang.adapter = barangAdapter
                }
                barangAdapter.setOnItemClickCallback(object : HomeBarangAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Barang) {

                    }
                })
            }
        })
    }

    private fun getCSVFileName() : String = "Pengambilan Barang $tanggal.csv"

    private fun exportExcel() {
        val csvFile = generateFile(requireContext(), getCSVFileName())
        if (csvFile != null) {
            csvWriter().open(csvFile, append = false) {
                writeRow(listOf(
                        "No", "Tanggal", "Sales", "Barang", "Jumlah", "Satuan", "Keterangan", "Added Time"))
                itemResponse.forEachIndexed { index, item ->
                    writeRow(listOf(
                            index + 1,
                            item.tanggal,
                            item.sales,
                            item.barang,
                            item.jumlah,
                            item.satuan,
                            item.keterangan,
                            item.addedTime
                    ))
                }
            }
            Toast.makeText(requireContext(), getString(R.string.csv_file_generated_text), Toast.LENGTH_LONG).show()
            val intent = goToFileIntent(requireContext(), csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.csv_file_not_generated_text), Toast.LENGTH_LONG).show()
        }
    }
}