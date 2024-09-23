package com.iguana.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.data.repository.LoginRepository
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private lateinit var context: Context
    private val TAG = "LoginViewModel"

    fun setContext(context: Context) {
        this.context = context
    }

    fun loginWithKakao() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                loginWithKakaoTalk()
            } else {
                loginWithKakaoAccount()
            }
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)

                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    _loginState.value = LoginState.Idle
                    return@loginWithKakaoTalk
                }

                loginWithKakaoAccount()
            } else if (token != null) {
                Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                login(token.accessToken)
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
                _loginState.value = LoginState.Error("로그인에 실패했습니다: ${error.message}")
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                login(token.accessToken)
            }
        }
    }

    private fun login(token: String) {
        viewModelScope.launch {
            val result = loginRepository.sendKakaoToken(token)
            if (result) {
                _loginState.value = LoginState.LoggedIn
            } else {
                _loginState.value = LoginState.Error("로그인에 실패했습니다.")
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return loginRepository.isLoggedIn()
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object LoggedIn : LoginState()
        data class Error(val message: String) : LoginState()
    }
}