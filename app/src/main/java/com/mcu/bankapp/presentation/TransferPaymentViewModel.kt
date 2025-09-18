package com.mcu.bankapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcu.bankapp.domain.models.PaymentRequest
import com.mcu.bankapp.domain.models.TransferType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferPaymentViewModel @Inject constructor(
    private val paymentUseCase: PaymentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentScreenState())
    val state: StateFlow<PaymentScreenState> = _state.asStateFlow()

    fun updateRecipientName(name: String) {
        _state.value = _state.value.copy(recipientName = name, validationErrors = emptyList())
    }

    fun updateAccountNumber(accountNumber: String) {
        _state.value = _state.value.copy(accountNumber = accountNumber, validationErrors = emptyList())
    }

    fun updateAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount, validationErrors = emptyList())
    }

    fun updateIban(iban: String) {
        _state.value = _state.value.copy(iban = iban, validationErrors = emptyList())
    }

    fun updateSwiftCode(swiftCode: String) {
        _state.value = _state.value.copy(swiftCode = swiftCode, validationErrors = emptyList())
    }

    fun setTransferType(transferType: TransferType) {
        _state.value = _state.value.copy(
            transferType = transferType,
            validationErrors = emptyList(),
            iban = if (transferType == TransferType.DOMESTIC) "" else _state.value.iban,
            swiftCode = if (transferType == TransferType.DOMESTIC) "" else _state.value.swiftCode
        )
    }

    fun processPayment() {
        viewModelScope.launch {
            val currentState = _state.value
            val request = PaymentRequest(
                recipientName = currentState.recipientName,
                accountNumber = currentState.accountNumber,
                amount = currentState.amount.toDoubleOrNull() ?: 0.0,
                transferType = currentState.transferType,
                iban = if (currentState.transferType == TransferType.INTERNATIONAL) currentState.iban else null,
                swiftCode = if (currentState.transferType == TransferType.INTERNATIONAL) currentState.swiftCode else null
            )

            // Validate first
            val validationResult = paymentUseCase.validatePayment(request)
            if (!validationResult.isValid) {
                _state.value = currentState.copy(
                    validationErrors = validationResult.errors
                )
                return@launch
            }

            // Process payment
            _state.value = currentState.copy(
                isLoading = true,
                validationErrors = emptyList()
            )

            val result = paymentUseCase.processPayment(request)
            _state.value = _state.value.copy(
                isLoading = false,
                paymentResult = result.getOrNull(),
                isPaymentSuccessful = result.isSuccess
            )
        }
    }

    fun clearPaymentResult() {
        _state.value = _state.value.copy(
            paymentResult = null,
            isPaymentSuccessful = false
        )
    }
}