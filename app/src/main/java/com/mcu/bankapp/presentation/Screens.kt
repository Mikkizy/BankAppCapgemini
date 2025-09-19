package com.mcu.bankapp.presentation

import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
data class PaymentScreen(val transferType: String)