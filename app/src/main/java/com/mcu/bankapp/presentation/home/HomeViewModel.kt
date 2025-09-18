package com.mcu.bankapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcu.bankapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()


    val userAccount = userRepository.getUserAccount()

    init {
        viewModelScope.launch {
            userAccount.collectLatest { account ->
                _homeState.value = _homeState.value.copy(
                    balance = account.balance,
                    name = account.name,
                    accountNo = account.accountNumber,
                    profileUrl = account.profileImageUrl.toString()
                )
            }
        }
    }
}