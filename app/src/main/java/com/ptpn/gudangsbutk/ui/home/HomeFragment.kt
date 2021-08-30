package com.ptpn.gudangsbutk.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.data.Barang
import com.ptpn.gudangsbutk.databinding.FragmentHomeBinding
import com.ptpn.gudangsbutk.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var barangAdapter: HomeBarangAdapter

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

        binding.tvUser.text = getString(R.string.hai_user, currentUser?.displayName)
        binding.tvDate.text = currentDate
        Glide.with(requireContext()).load(currentUser?.photoUrl).apply(RequestOptions.circleCropTransform()).into(binding.btnUser)

        populateBarang()
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

}