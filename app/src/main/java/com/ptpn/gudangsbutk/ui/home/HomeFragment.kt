package com.ptpn.gudangsbutk.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.databinding.FragmentHomeBinding
import com.ptpn.gudangsbutk.ui.user.UserActivity
import com.ptpn.gudangsbutk.utils.generateFile
import com.ptpn.gudangsbutk.utils.goToFileIntent
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var barangAdapter: HomeBarangAdapter
    private lateinit var dataAdapter: DataAdapter
    private lateinit var title: String
    private lateinit var dataResponse: List<Data>
    private lateinit var dateLong: String
    private lateinit var dateShort: String
    private lateinit var longDateFormat: SimpleDateFormat
    private lateinit var shortDateFormat: SimpleDateFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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

        shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        longDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateLong = longDateFormat.format(date)
        dateShort = shortDateFormat.format(date)

        val calender = Calendar.getInstance()
        val datePicker = OnDateSetListener{ _, year, month, day ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, day)
            updateTanggal(calender.time)
        }

        binding.tvUser.text = getString(R.string.hai_user, currentUser?.displayName)
        binding.tvDate.text = currentDate
        Glide.with(requireContext()).load(currentUser?.photoUrl).apply(RequestOptions.circleCropTransform()).into(
            binding.btnUser
        )
        binding.shimmerRvBarang.startShimmer()
        populateBarang()
        populateData(dateShort)

        binding.btnExportExcel.setOnClickListener { exportExcel() }
        binding.btnUser.setOnClickListener {
            val userIntent = Intent(requireContext(), UserActivity::class.java)
            startActivity(userIntent)
        }
        binding.btnTanggal.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker,
                    calender.get(Calendar.YEAR),
                    calender.get(Calendar.MONTH),
                    calender.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun populateData(tanggal: String) {
        viewModel.getDailyData(tanggal).observe(viewLifecycleOwner, { data ->
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

    private fun populateBarang() {
        viewModel.getBarang().observe(viewLifecycleOwner, { listBarang ->
            if (listBarang !== null) {
                barangAdapter = HomeBarangAdapter(listBarang, requireContext())
                barangAdapter.notifyDataSetChanged()

                binding.apply {
                    rvBarang.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    rvBarang.setHasFixedSize(true)
                    rvBarang.adapter = barangAdapter
                    shimmerRvBarang.stopShimmer()
                    shimmerRvBarang.visibility = GONE
                    rvBarang.visibility = VISIBLE
                }
                barangAdapter.setOnItemClickCallback(object :
                    HomeBarangAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Barang) {
                        showDialogBarang(data)
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

    @SuppressLint("InflateParams")
    private fun showDialogBarang(data: Barang) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_barang, null)
        val tvKode = view.findViewById<TextView>(R.id.tv_kode)
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvKeterangan = view.findViewById<TextView>(R.id.tv_keterangan)
        val image = view.findViewById<ImageView>(R.id.image)

        tvKode.text = data.kode
        tvNama.text = data.nama
        tvKeterangan.text = data.keterangan
        Glide.with(requireContext()).load(data.image).apply(
            RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
        ).into(image)

        dialog.setContentView(view)
        dialog.show()
    }

    private fun getCSVFileName() : String = "Pengambilan Barang $dateLong.csv"

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

    private fun updateTanggal(calender: Date) {
        dateLong = longDateFormat.format(calender)
        dateShort = shortDateFormat.format(calender)

        Toast.makeText(requireContext(), "Memuat data tanggal = $dateLong", Toast.LENGTH_SHORT).show()
        populateData(dateShort)
        title = "Pengambilan barang $dateLong"
        binding.tvTitle.text = title
    }
}