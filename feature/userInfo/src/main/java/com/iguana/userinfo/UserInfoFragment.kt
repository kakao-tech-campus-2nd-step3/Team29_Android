package com.iguana.userinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iguana.userinfo.databinding.FragmentUserinfoBinding
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserinfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserinfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserInfo()
        setupLogoutButton()
        observeLogoutEvent()
    }

    private fun setupUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                binding.mail.text = "사용자 정보를 불러올 수 없습니다."
            } else if (user != null) {
                val userName = user.kakaoAccount?.profile?.nickname ?: "이름 정보 없음"
                binding.mail.text = userName
            }
        }
    }

    private fun setupLogoutButton() {
        binding.logout?.setOnClickListener {

        }
    }

    private fun observeLogoutEvent() {
        viewLifecycleOwner.lifecycleScope.launch {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}