package com.iguana.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iguana.ui.databinding.ActivityBaseBinding
import com.iguana.dashBoard.DashBoardFragment
import com.iguana.ui.SideTabLayoutFragment

class BaseActivity : AppCompatActivity() {
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
    }

    fun showDashBoard() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, DashBoardFragment())
            .commit()
    }
}