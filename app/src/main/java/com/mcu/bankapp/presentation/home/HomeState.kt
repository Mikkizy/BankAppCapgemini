package com.mcu.bankapp.presentation.home

data class HomeState(
    val balance: Double = 5000.0,
    val name: String = "",
    val accountNo: String = "",
    val profileUrl: String = ""
)
