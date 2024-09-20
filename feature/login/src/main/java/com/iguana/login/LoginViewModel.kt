package com.iguana.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _kakaoLoginUrl = MutableStateFlow<String?>(null)
    val kakaoLoginUrl: StateFlow<String?> = _kakaoLoginUrl.asStateFlow()

    init {
        if (authRepository.isLoggedIn()) {
            _loginState.value = LoginState.LoggedIn
        }
    }

    fun getKakaoLoginUrl() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val url = authRepository.getKakaoLoginUrl()
            if (url != null) {
                _kakaoLoginUrl.value = url
                _loginState.value = LoginState.Idle
            } else {
                _loginState.value = LoginState.Error("카카오 로그인 URL을 가져오는데 실패했습니다.")
            }
        }
    }

    fun login(token: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val success = authRepository.sendKakaoToken(token)
            _loginState.value = if (success) {
                LoginState.LoggedIn
            } else {
                LoginState.Error("로그인에 실패했습니다.")
            }
        }
    }

    fun isUserLoggedIn() = authRepository.isLoggedIn()

    fun getAccessToken() = authRepository.getAccessToken()

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object LoggedIn : LoginState()
        data class Error(val message: String) : LoginState()
    }
}