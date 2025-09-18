package com.mcu.bankapp.presentation

import com.mcu.bankapp.domain.models.TransferType
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
data class PaymentScreen(val transferType: String)