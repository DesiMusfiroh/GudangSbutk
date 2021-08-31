package com.ptpn.gudangsbutk.ui.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.FragmentDataAllBinding
import com.ptpn.gudangsbutk.ui.home.HomeItemAdapter
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.lang.StringBuilder

class DataAllFragment : Fragment() {
    private lateinit var binding: FragmentDataAllBinding
    private lateinit var viewModel: DataViewModel
    private lateinit var itemAdapter: HomeItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDataAllBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[DataViewModel::class.java]

        populateItem()
    }

    private fun populateItem() {
        viewModel.getAllItem().observe(viewLifecycleOwner, { listItem ->
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
}