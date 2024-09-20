package com.iguana.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iguana.login.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //화면 넘어감을 위한 테스트 액티비티

    }
}
