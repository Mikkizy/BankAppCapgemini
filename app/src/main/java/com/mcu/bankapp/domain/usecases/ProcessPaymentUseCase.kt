package com.mcu.bankapp.domain.usecases

import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import kotlinx.coroutines.delay
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor() {

    suspend fun execute(paymentData: PaymentData, transferType: TransferType): PaymentResult {
        return try {
            // Simulate API call
            delay(1000)

            PaymentResult.Success(generateTransactionId())

        } catch (e: Exception) {
            PaymentResult.Error("Payment processing failed. Please try again: ${e.message}")
        }
    }

    private fun generateTransactionId(): String {
        return "TXN${System.currentTimeMillis()}"
    }
}

sealed class PaymentResult {
    data class Success(val transactionId: String) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
}