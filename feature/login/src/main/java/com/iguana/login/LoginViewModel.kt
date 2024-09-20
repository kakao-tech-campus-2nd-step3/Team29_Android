package com.iguana.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun checkLoginStatus() {
        if (loginRepository.isLoggedIn()) {
            _loginState.value = LoginState.LoggedIn
        } else {
            _loginState.value = LoginState.LoggedOut
        }
    }

    fun handleKakaoToken(token: String) {
        viewModelScope.launch {
            if (loginRepository.sendKakaoToken(token)) {
                _loginState.value = LoginState.LoggedIn
            } else {
                _loginState.value = LoginState.Error("로그인에 실패했습니다.")
            }
        }
    }
}

sealed class LoginState {
    object LoggedIn : LoginState()
    object LoggedOut : LoginState()
    data class Error(val message: String) : LoginState()
}