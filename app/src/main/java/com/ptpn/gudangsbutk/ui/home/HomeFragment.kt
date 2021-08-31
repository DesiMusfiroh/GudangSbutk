package com.ptpn.gudangsbutk.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.FragmentHomeBinding
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
    private lateinit var itemAdapter: HomeItemAdapter
    private lateinit var tanggal: String
    private lateinit var itemResponse: List<Item>
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

        populateBarang()
        populateItem()
        binding.btnExportExcel.setOnClickListener { exportExcel() }
        binding.btnExportPdf.setOnClickListener { exportPdf() }
//        Dexter.withActivity(requireActivity())
//                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .withListener(object : PermissionListener {
//                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
//                        binding.btnExportPdf.setOnClickListener { exportPdf() }
//                    }
//                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
//                    }
//                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
//                    }
//                }).check()
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
                        showDialogItem(data)
                    }
                })
            }
        })
    }

    private fun showDialogItem(data: Item) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_item, null)
        val tvSales = view.findViewById<TextView>(R.id.tv_sales)
        val tvBarang = view.findViewById<TextView>(R.id.tv_barang)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
        val tvTanggal = view.findViewById<TextView>(R.id.tv_tanggal)
        val tvKeterangan = view.findViewById<TextView>(R.id.tv_keterangan)

        tvSales.text = data.sales
        tvBarang.text = data.barang
        tvJumlah.text = StringBuilder("${data.jumlah} ${data.satuan}")
        tvTanggal.text = data.tanggal
        tvKeterangan.text = data.keterangan

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
                writeRow(listOf("No", "Tanggal", "Sales", "Barang", "Jumlah", "Satuan", "Keterangan", "Added Time"))
                itemResponse.forEachIndexed { index, item ->
                    writeRow(
                        listOf(
                            index + 1,
                            item.tanggal,
                            item.sales,
                            item.barang,
                            item.jumlah,
                            item.satuan,
                            item.keterangan,
                            item.addedTime
                        )
                    )
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