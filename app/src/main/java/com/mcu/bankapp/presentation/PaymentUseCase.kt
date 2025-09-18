package com.mcu.bankapp.presentation

import com.mcu.bankapp.domain.models.PaymentRequest
import com.mcu.bankapp.domain.models.PaymentValidationResult
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.repository.PaymentRepository
import javax.inject.Inject
import javax.inject.Singleton

interface PaymentUseCase {
    suspend fun validatePayment(request: PaymentRequest): PaymentValidationResult
    suspend fun processPayment(request: PaymentRequest): Result<String>
}

@Singleton
class PaymentUseCaseImpl @Inject constructor(
    private val paymentRepository: PaymentRepository
) : PaymentUseCase {

    override suspend fun validatePayment(request: PaymentRequest): PaymentValidationResult {
        val errors = mutableListOf<String>()

        if (request.recipientName.isBlank()) {
            errors.add("Recipient name is required")
        }

        if (request.accountNumber.isBlank()) {
            errors.add("Account number is required")
        }

        if (request.amount <= 0) {
            errors.add("Amount must be greater than 0")
        }

        when (request.transferType) {
            TransferType.INTERNATIONAL -> {
                if (request.iban.isNullOrBlank()) {
                    errors.add("IBAN is required for international transfers")
                } else if (request.iban.length != 34) {
                    errors.add("IBAN must be exactly 34 characters")
                }

                if (request.swiftCode.isNullOrBlank()) {
                    errors.add("SWIFT code is required for international transfers")
                } else if (!isValidSwiftCode(request.swiftCode)) {
                    errors.add("Invalid SWIFT code format (e.g., AAAA-BB-CC-12)")
                }
            }
            TransferType.DOMESTIC -> {
                // Additional domestic validation if needed
            }
        }

        return PaymentValidationResult(errors.isEmpty(), errors)
    }

    override suspend fun processPayment(request: PaymentRequest): Result<String> {
        return paymentRepository.processPayment(request)
    }

    private fun isValidSwiftCode(swiftCode: String): Boolean {
        val swiftPattern = "^[A-Z]{4}-[A-Z]{2}-[A-Z]{2}-[0-9]{2}$".toRegex()
        return swiftPattern.matches(swiftCode)
    }
}