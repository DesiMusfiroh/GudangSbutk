package com.ptpn.gudangsbutk.ui.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.data.ItemLama
import com.ptpn.gudangsbutk.databinding.FragmentDataUserBinding
import com.ptpn.gudangsbutk.ui.home.HomeItemAdapter
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory

class DataUserFragment : Fragment() {
    private lateinit var binding: FragmentDataUserBinding
    private lateinit var viewModel: DataViewModel
    private lateinit var itemAdapter: HomeItemAdapter
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDataUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[DataViewModel::class.java]
        mAuth = FirebaseAuth.getInstance()
        populateItem()
    }

    private fun populateItem() {
        mAuth.currentUser?.email?.let {
            viewModel.getUserItem(it).observe(viewLifecycleOwner, { listItem ->
                if (listItem !== null) {
                    itemAdapter = HomeItemAdapter(listItem, requireContext())
                    itemAdapter.notifyDataSetChanged()

                    binding.apply {
                        rvItem.layoutManager = LinearLayoutManager(context)
                        rvItem.setHasFixedSize(true)
                        rvItem.adapter = itemAdapter
                    }
                    itemAdapter.setOnItemClickCallback(object : HomeItemAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: ItemLama) {

                        }
                    })
                }
            })
        }
    }
}