package com.mcu.bankapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.usecases.ProcessPaymentUseCase
import com.mcu.bankapp.domain.usecases.ValidatePaymentUseCase
import com.mcu.bankapp.domain.usecases.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val validatePaymentUseCase: ValidatePaymentUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun updateTransferType(transferType: TransferType) {
        _uiState.value = _uiState.value.copy(
            transferType = transferType,
            validationErrors = emptyList()
        )
    }

    fun updatePaymentData(paymentData: PaymentData) {
        _uiState.value = _uiState.value.copy(
            paymentData = paymentData,
            validationErrors = emptyList()
        )
    }

    fun processPayment() {
        val currentState = _uiState.value

        // Validate first
        val validationResult = validatePaymentUseCase.execute(
            currentState.paymentData,
            currentState.transferType
        )

        when (validationResult) {
            is ValidationResult.Success -> {
                _uiState.value = currentState.copy(
                    isLoading = true,
                    validationErrors = emptyList(),
                    paymentResult = null
                )

                viewModelScope.launch {
                    val result = processPaymentUseCase.execute(
                        currentState.paymentData,
                        currentState.transferType
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        paymentResult = result
                    )
                }
            }
            is ValidationResult.Error -> {
                _uiState.value = currentState.copy(
                    validationErrors = validationResult.errors
                )
            }
        }
    }

    fun clearPaymentResult() {
        _uiState.value = _uiState.value.copy(paymentResult = null)
    }

    fun resetForm() {
        _uiState.value = PaymentUiState(transferType = _uiState.value.transferType)
    }
}