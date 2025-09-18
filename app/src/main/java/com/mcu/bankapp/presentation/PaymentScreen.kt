package com.mcu.bankapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.usecases.PaymentResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    transferType: TransferType,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(transferType) {
        viewModel.updateTransferType(transferType)
    }

    // Handle payment result
    uiState.paymentResult?.let { result ->
        PaymentResultDialog(
            result = result,
            onDismiss = { viewModel.clearPaymentResult() },
            onNewPayment = { viewModel.resetForm() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("PaymentScreen")
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = when (transferType) {
                TransferType.DOMESTIC -> "Domestic Transfer"
                TransferType.INTERNATIONAL -> "International Transfer"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("ScreenTitle")
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Common fields
                PaymentTextField(
                    label = "Recipient Name",
                    value = uiState.paymentData.recipientName,
                    onValueChange = { newValue ->
                        viewModel.updatePaymentData(
                            uiState.paymentData.copy(recipientName = newValue)
                        )
                    },
                    testTag = "RecipientNameField"
                )

                PaymentTextField(
                    label = "Account Number",
                    value = uiState.paymentData.accountNumber,
                    onValueChange = { newValue ->
                        viewModel.updatePaymentData(
                            uiState.paymentData.copy(accountNumber = newValue)
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    testTag = "AccountNumberField"
                )

                PaymentTextField(
                    label = "Amount",
                    value = uiState.paymentData.amount,
                    onValueChange = { newValue ->
                        viewModel.updatePaymentData(
                            uiState.paymentData.copy(amount = newValue)
                        )
                    },
                    keyboardType = KeyboardType.Decimal,
                    prefix = "$",
                    testTag = "AmountField"
                )

                // International-specific fields
                if (transferType == TransferType.INTERNATIONAL) {
                    PaymentTextField(
                        label = "IBAN (max 34 characters)",
                        value = uiState.paymentData.iban,
                        onValueChange = { newValue ->
                            if (newValue.length <= 34) {
                                viewModel.updatePaymentData(
                                    uiState.paymentData.copy(iban = newValue.uppercase())
                                )
                            }
                        },
                        testTag = "IbanField"
                    )

                    PaymentTextField(
                        label = "SWIFT Code (AAAA-BB-CC-12)",
                        value = uiState.paymentData.swiftCode,
                        onValueChange = { newValue ->
                            viewModel.updatePaymentData(
                                uiState.paymentData.copy(swiftCode = newValue.uppercase())
                            )
                        },
                        placeholder = "ABCD-EF-GH-12",
                        testTag = "SwiftCodeField"
                    )
                }
            }
        }

        // Error messages
        if (uiState.validationErrors.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.testTag("ErrorCard")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    uiState.validationErrors.forEach { error ->
                        Text(
                            text = "• $error",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Send Payment Button
        Button(
            onClick = { viewModel.processPayment() },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("SendPaymentButton")
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Send Payment")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
    placeholder: String? = null,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        prefix = prefix?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        singleLine = true
    )
}

@Composable
private fun PaymentResultDialog(
    result: PaymentResult,
    onDismiss: () -> Unit,
    onNewPayment: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (result) {
                    is PaymentResult.Success -> "Payment Successful"
                    is PaymentResult.Error -> "Payment Failed"
                }
            )
        },
        text = {
            Text(
                text = when (result) {
                    is PaymentResult.Success -> "Transaction ID: ${result.transactionId}"
                    is PaymentResult.Error -> result.message
                },
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    if (result is PaymentResult.Success) {
                        onNewPayment()
                    }
                },
                modifier = Modifier.testTag("DialogConfirmButton")
            ) {
                Text("OK")
            }
        },
        modifier = Modifier.testTag("PaymentResultDialog")
    )
}