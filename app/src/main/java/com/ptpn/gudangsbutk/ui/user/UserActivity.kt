package com.ptpn.gudangsbutk.ui.user

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.ptpn.gudangsbutk.R
import com.ptpn.gudangsbutk.databinding.ActivityUserBinding
import com.ptpn.gudangsbutk.ui.signin.SignInActivity


class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Halaman Pengguna"

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        Glide.with(this).load(currentUser?.photoUrl).into(binding.imgUser)
        binding.tvName.text = currentUser?.displayName
        binding.tvEmail.text = currentUser?.email

        binding.cvLogout.setOnClickListener {
            mAuth.signOut()
            val logoutIntent = Intent(this, SignInActivity::class.java)
            startActivity(logoutIntent)
            finishAffinity()
        }

        binding.cvHelp.setOnClickListener { onClickWhatsApp() }
        binding.cvInformation.setOnClickListener { showDialogInformation()}
    }

    private fun showDialogInformation() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_information)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun onClickWhatsApp() {
        val phoneDeveloper = +6287833226468
        val message = "Hi, I am ${mAuth.currentUser?.displayName}. Sorry to interrupt, I need some help with this app [Gudang SBUTK] ! ....."
        try {
            val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + phoneDeveloper.toString() + "&text=" + message))
            startActivity(Intent.createChooser(waIntent, "Share with"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
        }
    }
}