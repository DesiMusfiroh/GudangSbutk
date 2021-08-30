package com.ptpn.gudangsbutk.ui.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ptpn.gudangsbutk.data.Item
import com.ptpn.gudangsbutk.databinding.FragmentDataAllBinding
import com.ptpn.gudangsbutk.ui.home.HomeItemAdapter
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory

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

                    }
                })
            }
        })
    }
}