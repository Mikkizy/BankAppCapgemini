package com.mcu.bankapp.domain.models

data class PaymentRequest(
    val recipientName: String,
    val accountNumber: String,
    val amount: Double,
    val transferType: TransferType,
    val iban: String? = null,
    val swiftCode: String? = null
)
