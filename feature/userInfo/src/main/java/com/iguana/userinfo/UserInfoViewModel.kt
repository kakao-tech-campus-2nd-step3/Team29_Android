package com.iguana.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iguana.domain.repository.LoginRepository
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    // 에러 처리
                    return@logout
                }
                viewModelScope.launch {
                    loginRepository.clearLoginState()
                    _logoutEvent.emit(Unit)
                }
            }
        }
    }
} 