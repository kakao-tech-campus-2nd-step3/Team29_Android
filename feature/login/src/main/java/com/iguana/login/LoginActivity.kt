package com.iguana.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iguana.login.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login = this

        viewModel.setContext(this)

        setupObservers()

        if (viewModel.isUserLoggedIn()) {
            navigateToMainScreen()
        }
    }

    fun onLoginButtonClicked() {
        viewModel.loginWithKakao()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginViewModel.LoginState.LoggedIn -> navigateToMainScreen()
                    is LoginViewModel.LoginState.Error -> {
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        binding.btnKakaoLogin?.isEnabled = true
                    }
                    is LoginViewModel.LoginState.Loading -> {
                        binding.btnKakaoLogin?.isEnabled = false
                    }
                    is LoginViewModel.LoginState.Idle -> {
                        binding.btnKakaoLogin?.isEnabled = true
                    }
                }
            }
        }
    }

    private fun navigateToMainScreen() {
        Log.d(TAG, "메인 화면으로 이동")
        val intent = Intent(this, com.iguana.ui.BaseActivity::class.java)
        startActivity(intent)
        finish()
    }
}
