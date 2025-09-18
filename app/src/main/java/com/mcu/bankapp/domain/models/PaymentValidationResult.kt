package com.mcu.bankapp.domain.models

data class PaymentValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)
