package com.mcu.bankapp.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.usecases.PaymentResult
import java.util.Locale

// Data class to hold formatting result with cursor position
private data class SwiftCodeFormatResult(
    val formattedText: String,
    val cursorPosition: Int
)

// Helper function to format SWIFT code with proper cursor positioning
private fun formatSwiftCodeWithCursor(input: TextFieldValue): SwiftCodeFormatResult {
    val cleanInput = input.text.replace("-", "").uppercase()
    val limitedInput = cleanInput.take(12)
    val cursorPos = minOf(input.selection.end, input.text.length)

    // Count characters before cursor (excluding dashes)
    val charsBeforeCursor = input.text.take(cursorPos).replace("-", "").length
    val effectiveCharsBefore = minOf(charsBeforeCursor, limitedInput.length)

    // Format the text
    val formattedText = when {
        limitedInput.length <= 4 -> limitedInput
        limitedInput.length <= 6 -> "${limitedInput.substring(0, 4)}-${limitedInput.substring(4)}"
        limitedInput.length <= 8 -> "${limitedInput.substring(0, 4)}-${
            limitedInput.substring(
                4,
                6
            )
        }-${limitedInput.substring(6)}"

        else -> "${limitedInput.substring(0, 4)}-${
            limitedInput.substring(
                4,
                6
            )
        }-${limitedInput.substring(6, 8)}-${limitedInput.substring(8)}"
    }

    // Calculate new cursor position
    val newCursorPos = when {
        effectiveCharsBefore <= 4 -> effectiveCharsBefore
        effectiveCharsBefore <= 6 -> effectiveCharsBefore + 1 // +1 for first dash
        effectiveCharsBefore <= 8 -> effectiveCharsBefore + 2 // +2 for first two dashes
        else -> effectiveCharsBefore + 3 // +3 for all three dashes
    }

    return SwiftCodeFormatResult(formattedText, minOf(newCursorPos, formattedText.length))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    transferType: TransferType,
    //viewModel: PaymentViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    uiState: PaymentState,
    updateTransferType: (transferType: TransferType) -> Unit,
    clearPaymentResult: () -> Unit,
    resetForm: () -> Unit,
    updatePaymentData: (paymentData: PaymentData) -> Unit,
    processPayment: () -> Unit
) {
    //val uiState by viewModel.uiState.collectAsState()

    // Local state for SWIFT code TextFieldValue to handle cursor positioning
    var swiftCodeFieldValue by remember { mutableStateOf(TextFieldValue(uiState.paymentData.swiftCode)) }

    // Sync SWIFT code field value with ViewModel state
    LaunchedEffect(uiState.paymentData.swiftCode) {
        if (swiftCodeFieldValue.text != uiState.paymentData.swiftCode) {
            swiftCodeFieldValue = TextFieldValue(
                text = uiState.paymentData.swiftCode,
                selection = TextRange(uiState.paymentData.swiftCode.length)
            )
        }
    }

    LaunchedEffect(transferType) {
        //viewModel.updateTransferType(transferType)
        updateTransferType(transferType)
    }

    // Handle payment result
    uiState.paymentResult?.let { result ->
        PaymentResultDialog(
            result = result,
            onDismiss = { clearPaymentResult() },
            onNewPayment = {
                resetForm()
                navigateToHome()
            }
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

        // Account Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Available Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "£${String.format(Locale.getDefault(), "%.2f", uiState.userAccount.balance)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("BalanceText")
                )
            }
        }

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
                        updatePaymentData(
                            uiState.paymentData.copy(recipientName = newValue)
                        )
                    },
                    testTag = "RecipientNameField"
                )

                PaymentTextField(
                    label = "Account Number",
                    value = uiState.paymentData.accountNumber,
                    onValueChange = { newValue ->
                        updatePaymentData(
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
                        updatePaymentData(
                            uiState.paymentData.copy(amount = newValue)
                        )
                    },
                    keyboardType = KeyboardType.Decimal,
                    prefix = "£",
                    testTag = "AmountField"
                )

                // International-specific fields
                if (transferType == TransferType.INTERNATIONAL) {
                    PaymentTextField(
                        label = "IBAN (max 34 characters)",
                        value = uiState.paymentData.iban,
                        onValueChange = { newValue ->
                            if (newValue.length <= 34) {
                                updatePaymentData(
                                    uiState.paymentData.copy(iban = newValue.uppercase())
                                )
                            }
                        },
                        testTag = "IbanField"
                    )

                    // Special SWIFT code field with cursor positioning
                    SwiftCodeTextField(
                        label = "SWIFT Code (AAAA-BB-CC-12)",
                        value = swiftCodeFieldValue,
                        onValueChange = { newFieldValue ->
                            val formatResult = formatSwiftCodeWithCursor(newFieldValue)
                            swiftCodeFieldValue = TextFieldValue(
                                text = formatResult.formattedText,
                                selection = TextRange(formatResult.cursorPosition)
                            )
                            updatePaymentData(
                                uiState.paymentData.copy(swiftCode = formatResult.formattedText)
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
            onClick = { processPayment() },
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
private fun SwiftCodeTextField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        singleLine = true
    )
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
                    is PaymentResult.Success -> "Transaction ID: ${result.transactionId}\nYour account balance has been updated."
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