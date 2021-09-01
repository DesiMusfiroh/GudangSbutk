package com.ptpn.gudangsbutk.ui.form

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Data
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.FragmentFormBinding
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class FormFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentFormBinding
    private lateinit var viewModel: FormViewModel
    private lateinit var mAuth: FirebaseAuth
    private var barangAdapter: ArrayAdapter<String>? = null
    private var satuanAdapter: ArrayAdapter<String>? = null
    private lateinit var dateLong: String
    private lateinit var dateShort: String
    private lateinit var longDateFormat: SimpleDateFormat
    private lateinit var shortDateFormat: SimpleDateFormat
    private lateinit var listItem: ArrayList<Item>
    private lateinit var itemAdapter: FormAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[FormViewModel::class.java]

        mAuth = FirebaseAuth.getInstance()

        val date = Calendar.getInstance().time
        shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        longDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateLong = longDateFormat.format(date)
        dateShort = shortDateFormat.format(date)
        binding.tvTanggal.text = dateLong

        listItem = ArrayList(0)

        itemAdapter = FormAdapter(listItem, requireContext())
        binding.apply {
            rvItem.layoutManager = LinearLayoutManager(context)
            rvItem.setHasFixedSize(true)
            rvItem.adapter = itemAdapter
        }
        itemAdapter.setOnItemClickCallback(object : FormAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Item) { }
            override fun onItemDelete(data: Item) {
                listItem.remove(data)
                itemAdapter.notifyDataSetChanged()
            }
        })

        binding.btnSubmit.setOnClickListener(this)
        binding.btnTanggal.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)

        val calender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, day)
            updateTanggal(calender.time)
        }
        binding.btnTanggal.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker,
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_add -> { showDialogAddItem() }
            R.id.btn_submit -> { saveData() }
        }
    }

    private fun showDialogAddItem() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_form)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnClose = dialog.findViewById(R.id.iv_close) as ImageView
        val actvSatuan = dialog.findViewById(R.id.actv_satuan) as AutoCompleteTextView
        val actvBarang = dialog.findViewById(R.id.actv_barang) as AutoCompleteTextView
        val btnAddItem = dialog.findViewById(R.id.btn_add_item) as Button
        val etJumlah = dialog.findViewById(R.id.et_jumlah) as TextInputEditText
        val etCatatan = dialog.findViewById(R.id.et_catatan) as TextInputEditText

        var barangSelected: String? = null
        var satuanSelected:  String? = null

        val arraySatuan = ArrayList<String>(3)
        arraySatuan.add("Dus")
        arraySatuan.add("Pcs")
        arraySatuan.add("Paket")
        satuanAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, arraySatuan) }
        actvSatuan.setAdapter(satuanAdapter)
        actvSatuan.onItemClickListener = AdapterView.OnItemClickListener {
            parent, _, position, _ -> satuanSelected = parent?.getItemAtPosition(position).toString()
        }

        viewModel.getListBarang().observe(viewLifecycleOwner, { listBarang ->
            barangAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, listBarang) }
            actvBarang.setAdapter(barangAdapter)
        })
        actvBarang.onItemClickListener = AdapterView.OnItemClickListener {
            parent, _, position, _ -> barangSelected = parent?.getItemAtPosition(position).toString()
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        btnAddItem.setOnClickListener{
            val jumlah = etJumlah.text.toString()
            val catatan = etCatatan.text.toString()

            if (barangSelected == null) {
                actvBarang.error = "Mohon pilih jenis barang!"
                actvBarang.requestFocus()
                return@setOnClickListener
            }

            if (jumlah.isEmpty()) {
                etJumlah.error = "Mohon isi jumlah barang!"
                etJumlah.requestFocus()
                return@setOnClickListener
            }

            if (satuanSelected == null) {
                actvSatuan.error = "Mohon pilih satuan barang!"
                actvSatuan.requestFocus()
                return@setOnClickListener
            }

            val item = Item(barangSelected!!, jumlah, satuanSelected!!, catatan)
            listItem.add(item)
            dialog.dismiss()
            Toast.makeText(requireContext(), "Berhasil menambahkan item", Toast.LENGTH_LONG).show()

            if (listItem.isNotEmpty()) {
                itemAdapter.notifyDataSetChanged()
            }
        }
        dialog.show()
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

            if (sales.isEmpty()) {
                etSales.error = "Mohon diisi terlebih dahulu!"
                etSales.requestFocus()
                return
            }
            if (listItem.isEmpty()) {
                btnAdd.requestFocus()
                Toast.makeText(requireContext(), "Mohon tambahkan item terlebih dahulu", Toast.LENGTH_LONG).show()
                return
            }

            val data = Data(id, user, dateShort, sales, keterangan, addedTime, listItem)
            viewModel.insertData(data).apply {
                showAlert(this)
            }
        }
    }

    private fun showAlert(check: Boolean) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvMessage = dialog.findViewById(R.id.tv_message) as TextView

        val btnClose = dialog.findViewById(R.id.iv_close) as ImageView
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        val lottie = dialog.findViewById(R.id.lottie_anim) as LottieAnimationView
        if (check) {
            listItem.clear()
            binding.etSales.setText("")
            binding.etKeterangan.setText("")
            itemAdapter.notifyDataSetChanged()
            lottie.setAnimation("lottie_success.json")
            tvMessage.text = StringBuilder("Sukses !... \nData pengambilan barang \n berhasil disimpan !")
        } else {
            lottie.setAnimation("lottie_failed.json")
            tvMessage.text = StringBuilder("Gagal !!. \nData pengambilan barang  \nbelum berhasil disimpan!")
        }
        dialog.show()
    }

    private fun updateTanggal(calender: Date) {
        dateLong = longDateFormat.format(calender)
        dateShort = shortDateFormat.format(calender)
        binding.tvTanggal.text = dateLong
        Toast.makeText(requireContext(), "Tanggal form diubah = $dateLong", Toast.LENGTH_SHORT).show()
    }
}