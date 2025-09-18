package com.mcu.bankapp.domain.usecases

import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import kotlinx.coroutines.delay
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor() {

    suspend fun execute(paymentData: PaymentData, transferType: TransferType): PaymentResult {
        return try {
            // Simulate API call
            delay(2000)

            // Simulate random success/failure for demo
            val success = (0..10).random() > 2

            if (success) {
                PaymentResult.Success(generateTransactionId())
            } else {
                PaymentResult.Error("Payment processing failed. Please try again.")
            }
        } catch (e: Exception) {
            PaymentResult.Error("Network error: ${e.message}")
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