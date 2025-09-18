package com.mcu.bankapp.data.repository

import com.mcu.bankapp.domain.models.PaymentRequest
import com.mcu.bankapp.domain.repository.PaymentRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {
    override suspend fun processPayment(request: PaymentRequest): Result<String> {
        return try {
            // Simulate API call
            delay(1000)
            Result.success("Payment processed successfully. Transaction ID: TXN${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}