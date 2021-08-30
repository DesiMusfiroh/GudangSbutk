package com.ptpn.gudangsbutk.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.databinding.ActivityDashboardBinding
import com.ptpn.gudangsbutk.ui.data.DataFragment
import com.ptpn.gudangsbutk.ui.form.FormFragment
import com.ptpn.gudangsbutk.ui.home.HomeFragment
import com.ptpn.gudangsbutk.ui.settings.SettingsFragment
import com.ptpn.gudangsbutk.utils.DatePickerFragment

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.navView.setItemSelected(R.id.navigation_home, true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, HomeFragment())
            .commit()

        bottomMenu()
    }

    private fun bottomMenu() {
        binding.navView.setOnItemSelectedListener {
            val fragment: Fragment = when (it) {
                R.id.navigation_form ->  FormFragment()
                R.id.navigation_data ->  DataFragment()
                R.id.navigation_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()
        }
    }

}