package com.ptpn.gudangsbutk.ui.data

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.databinding.FragmentDataAllBinding
import com.ptpn.gudangsbutk.ui.home.DataAdapter
import com.ptpn.gudangsbutk.ui.home.ItemAdapter
import com.ptpn.gudangsbutk.utils.generateFile
import com.ptpn.gudangsbutk.utils.goToFileIntent
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DataAllFragment : Fragment() {
    private lateinit var binding: FragmentDataAllBinding
    private lateinit var viewModel: DataViewModel
    private lateinit var dataAdapter: DataAdapter
    private lateinit var dataResponse: List<Data>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDataAllBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[DataViewModel::class.java]

        populateData()
        binding.btnExportExcel.setOnClickListener { exportExcel() }
    }

    private fun populateData() {
        viewModel.getAllData().observe(viewLifecycleOwner, { data ->
            dataResponse = data
            if (data !== null) {
                dataAdapter = DataAdapter(data, requireContext())
                dataAdapter.notifyDataSetChanged()

                binding.apply {
                    rvItem.layoutManager = LinearLayoutManager(context)
                    rvItem.setHasFixedSize(true)
                    rvItem.adapter = dataAdapter
                }
                dataAdapter.setOnItemClickCallback(object : DataAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Data) {
                        showDialogData(data)
                    }
                })
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun showDialogData(data: Data) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_data, null)
        val tvSales = view.findViewById<TextView>(R.id.tv_sales)
        val tvTanggal = view.findViewById<TextView>(R.id.tv_tanggal)
        val tvKeterangan = view.findViewById<TextView>(R.id.tv_keterangan)
        val tvAddedTime = view.findViewById<TextView>(R.id.tv_added_time)
        val tvId = view.findViewById<TextView>(R.id.tv_id)
        val rvItem = view.findViewById<RecyclerView>(R.id.rv_item)

        val itemAdapter = data.item?.let { ItemAdapter(it, requireContext()) }
        itemAdapter?.notifyDataSetChanged()
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.setHasFixedSize(true)
        rvItem.adapter = itemAdapter

        val shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val longDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dataParse: Date =  shortDateFormat.parse("${data.tanggal}")
        val dataTanggal = longDateFormat.format(dataParse)

        tvSales.text = data.sales
        tvTanggal.text = dataTanggal
        tvKeterangan.text = data.keterangan
        tvAddedTime.text = data.addedTime
        tvId.text = StringBuilder("No Form : ${data.id?.substring(0, 13)}")

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.show()
    }

    private fun getCSVFileName() : String = "Pengambilan Barang All.csv"

    private fun exportExcel() {
        val csvFile = generateFile(requireContext(), getCSVFileName())
        if (csvFile != null) {
            csvWriter().open(csvFile, append = false) {
                writeRow(
                        listOf(
                                "No",
                                "Kode",
                                "Tanggal",
                                "Sales",
                                "Keterangan",
                                "Barang",
                                "Jumlah",
                                "Satuan",
                                "Catatan",
                                "Added Time"
                        )
                )
                dataResponse.forEachIndexed { index, data ->
                    data.item?.forEach{ item ->
                        writeRow(
                                listOf(
                                        index + 1,
                                        data.id?.substring(0, 13),
                                        data.tanggal,
                                        data.sales,
                                        data.keterangan,
                                        item.barang,
                                        item.jumlah,
                                        item.satuan,
                                        item.catatan,
                                        data.addedTime,
                                )
                        )
                    }
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