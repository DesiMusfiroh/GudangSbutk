package com.ptpn.gudangsbutk.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.databinding.FragmentHomeBinding
import com.ptpn.gudangsbutk.ui.user.UserActivity
import com.ptpn.gudangsbutk.utils.generateFile
import com.ptpn.gudangsbutk.utils.goToFileIntent
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var barangAdapter: HomeBarangAdapter
    private lateinit var dataAdapter: DataAdapter
    private lateinit var tanggal: String
    private lateinit var dataResponse: List<Data>
    companion object {
        private const val PERMISSION_CODE = 1001
    }

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

        val tanggalFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tanggal = tanggalFormat.format(date)

        binding.tvUser.text = getString(R.string.hai_user, currentUser?.displayName)
        binding.tvDate.text = currentDate
        Glide.with(requireContext()).load(currentUser?.photoUrl).apply(RequestOptions.circleCropTransform()).into(
            binding.btnUser
        )
        binding.shimmerRvBarang.startShimmer()
        populateBarang()
        populateData()
        binding.btnExportExcel.setOnClickListener { exportExcel() }
        binding.btnExportPdf.setOnClickListener { exportPdf() }
        binding.btnUser.setOnClickListener {
            val userIntent = Intent(requireContext(), UserActivity::class.java)
            startActivity(userIntent)
        }
    }

    private fun populateData() {
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

        tvSales.text = data.sales
        tvTanggal.text = data.tanggal
        tvKeterangan.text = data.keterangan
        tvAddedTime.text = data.addedTime
        tvId.text = StringBuilder("No Form : ${data.id?.substring(0, 13)}")

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.show()
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
        Glide.with(requireContext())
                .load(data.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(image)

        dialog.setContentView(view)
        dialog.show()
    }

    private fun getCSVFileName() : String = "Pengambilan Barang $tanggal.csv"

    private fun exportExcel() {
        val csvFile = generateFile(requireContext(), getCSVFileName())
        if (csvFile != null) {
            csvWriter().open(csvFile, append = false) {
                writeRow(listOf("No","Kode", "Tanggal", "Sales", "Keterangan", "Barang", "Jumlah", "Satuan", "Catatan", "Added Time"))
                dataResponse.forEachIndexed { index, data ->
                    data.item?.forEach{ item ->
                        writeRow(
                                listOf(
                                        index + 1,
                                        data.id?.substring(0,13),
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

    private fun exportPdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
                Log.d("pdf", "permission denied")
            } else { savePdf()
                Log.d("pdf", "start save pdf")
            }
        } else { savePdf()
            Log.d("pdf", "start save pdf build version < m")
        }
    }

    private fun savePdf() {
        val mDoc = Document()
        val mFileName = "Pengambilan Barang $tanggal"
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName +".pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()
            mDoc.addAuthor(mAuth.currentUser?.displayName)
            mDoc.add(Paragraph("Pengambilan Barang "))
            mDoc.close()
            Log.d("pdf", "success ${mFileName} path $mFilePath")
            Toast.makeText(
                requireContext(),
                "$mFileName.pdf\n berhasil disimpan ke dalam folder \n$mFilePath",
                Toast.LENGTH_SHORT
            ).show()
        }
        catch (e: Exception){
            Log.d("pdf", "exception ${e.message}")
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePdf()
            }
        }
    }
}