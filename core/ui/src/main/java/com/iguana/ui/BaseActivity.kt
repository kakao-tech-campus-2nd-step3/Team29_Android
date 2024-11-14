package com.iguana.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.iguana.dashBoard.DashBoardFragment
import com.iguana.documents.DocumentsFragment
import com.iguana.favorites.FavoritesFragment
import com.iguana.navigation.LoginNavigator
import com.iguana.settings.SettingsFragment
import com.iguana.ui.databinding.ActivityBaseBinding
import com.iguana.userinfo.UserInfoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseActivity : AppCompatActivity(), LoginNavigator {
    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SideTabLayoutFragment())
                .replace(R.id.content_frame, DashBoardFragment())
                .commit()
        }

        StatusBarManager.setTransparentStatusBar(this)
    }

    fun showDashBoard() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, DashBoardFragment())
            .commit()
    }

    fun showDocuments() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, DocumentsFragment())
            .commit()
    }

    fun showSettings() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, SettingsFragment())
            .commit()
    }

    fun showUserInfo() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, UserInfoFragment())
            .commit()
    }

    fun showFavorites() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, FavoritesFragment())
            .commit()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, Class.forName("com.iguana.login.LoginActivity"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}