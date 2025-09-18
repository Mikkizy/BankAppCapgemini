package com.mcu.bankapp.presentation.home

import androidx.lifecycle.ViewModel
import com.mcu.bankapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userAccount = userRepository.getUserAccount()
}