package com.mcu.bankapp.presentation.payment

import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.models.UserAccount
import com.mcu.bankapp.domain.usecases.PaymentResult

data class PaymentUiState(
    val transferType: TransferType = TransferType.DOMESTIC,
    val paymentData: PaymentData = PaymentData(),
    val userAccount: UserAccount = UserAccount(),
    val isLoading: Boolean = false,
    val validationErrors: List<String> = emptyList(),
    val paymentResult: PaymentResult? = null
)
