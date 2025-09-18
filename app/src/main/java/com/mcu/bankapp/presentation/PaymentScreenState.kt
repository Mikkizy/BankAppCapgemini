package com.mcu.bankapp.presentation

import com.mcu.bankapp.domain.models.TransferType

data class PaymentScreenState(
    val recipientName: String = "",
    val accountNumber: String = "",
    val amount: String = "",
    val iban: String = "",
    val swiftCode: String = "",
    val transferType: TransferType = TransferType.DOMESTIC,
    val isLoading: Boolean = false,
    val validationErrors: List<String> = emptyList(),
    val paymentResult: String? = null,
    val isPaymentSuccessful: Boolean = false
)
