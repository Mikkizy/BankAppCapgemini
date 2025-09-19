package com.mcu.bankapp.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.models.UserAccount
import com.mcu.bankapp.domain.repository.UserRepository
import com.mcu.bankapp.domain.usecases.ProcessPaymentUseCase
import com.mcu.bankapp.domain.usecases.ValidatePaymentUseCase
import com.mcu.bankapp.domain.usecases.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val validatePaymentUseCase: ValidatePaymentUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentState())

    val uiState: StateFlow<PaymentState> = combine(
        _uiState,
        userRepository.getUserAccount()
    ) { uiState: PaymentState, userAccount: UserAccount ->
        uiState.copy(userAccount = userAccount)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaymentState()
    )

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
        val currentState = uiState.value

        // First validate the amount against balance
        val amount = currentState.paymentData.amount.toDoubleOrNull()
        if (amount != null) {
            if (!currentState.userAccount.canTransfer(amount)) {
                _uiState.value = _uiState.value.copy(
                    validationErrors = listOf(
                        "Insufficient balance. Available: £${
                            String.format(
                                Locale.getDefault(),
                                "%.2f",
                                currentState.userAccount.balance
                            )
                        }"
                    )
                )
                return
            }
        }

        // Validate payment data
        val validationResult = validatePaymentUseCase.execute(
            currentState.paymentData,
            currentState.transferType
        )

        when (validationResult) {
            is ValidationResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    validationErrors = emptyList(),
                    paymentResult = null
                )

                viewModelScope.launch {
                    val balanceResult = userRepository.deductBalance(amount!!)

                    if (balanceResult.isSuccess) {
                        val result = processPaymentUseCase.execute()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            paymentResult = result
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            validationErrors = listOf(
                                balanceResult.exceptionOrNull()?.message ?: "Payment failed"
                            )
                        )
                    }
                }
            }
            is ValidationResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    validationErrors = validationResult.errors
                )
            }
        }
    }

    fun clearPaymentResult() {
        _uiState.value = _uiState.value.copy(paymentResult = null)
    }

    fun resetForm() {
        _uiState.value = PaymentState(transferType = _uiState.value.transferType)
    }
}