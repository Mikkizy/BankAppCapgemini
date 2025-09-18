package com.mcu.bankapp.domain.repository

import com.mcu.bankapp.domain.models.PaymentRequest

interface PaymentRepository {
    suspend fun processPayment(request: PaymentRequest): Result<String>
}