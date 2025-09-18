package com.mcu.bankapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mcu.bankapp.domain.models.TransferType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPaymentScreen(
    modifier: Modifier = Modifier,
    viewModel: TransferPaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Show result dialog
    state.paymentResult?.let { result ->
        PaymentResultDialog(
            result = result,
            isSuccess = state.isPaymentSuccessful,
            onDismiss = { viewModel.clearPaymentResult() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Send Payment",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Transfer Type Selection
        TransferTypeSelector(
            selectedType = state.transferType,
            onTypeSelected = viewModel::setTransferType
        )

        // Common Fields
        PaymentTextField(
            value = state.recipientName,
            onValueChange = viewModel::updateRecipientName,
            label = "Recipient Name",
            placeholder = "Enter recipient's name"
        )

        PaymentTextField(
            value = state.accountNumber,
            onValueChange = viewModel::updateAccountNumber,
            label = "Account Number",
            placeholder = "Enter account number",
            keyboardType = KeyboardType.Number
        )

        PaymentTextField(
            value = state.amount,
            onValueChange = viewModel::updateAmount,
            label = "Amount",
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            prefix = "£"
        )

        // International Transfer Fields
        if (state.transferType == TransferType.INTERNATIONAL) {
            PaymentTextField(
                value = state.iban,
                onValueChange = viewModel::updateIban,
                label = "IBAN",
                placeholder = "34-character IBAN",
                supportingText = "Must be exactly 34 characters"
            )

            PaymentTextField(
                value = state.swiftCode,
                onValueChange = viewModel::updateSwiftCode,
                label = "SWIFT Code",
                placeholder = "AAAA-BB-CC-12",
                supportingText = "Format: AAAA-BB-CC-12"
            )
        }

        // Validation Errors
        if (state.validationErrors.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Please fix the following errors:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    state.validationErrors.forEach { error ->
                        Text(
                            text = "• $error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }
        }

        // Send Payment Button
        Button(
            onClick = viewModel::processPayment,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Send Payment")
            }
        }
    }
}

@Composable
private fun TransferTypeSelector(
    selectedType: TransferType,
    onTypeSelected: (TransferType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Transfer Type",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onTypeSelected(TransferType.DOMESTIC) },
                    label = { Text("Domestic") },
                    selected = selectedType == TransferType.DOMESTIC,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    onClick = { onTypeSelected(TransferType.INTERNATIONAL) },
                    label = { Text("International") },
                    selected = selectedType == TransferType.INTERNATIONAL,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String = "",
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        prefix = if (prefix.isNotEmpty()) { { Text(prefix) } } else null,
        supportingText = supportingText?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun PaymentResultDialog(
    result: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isSuccess) "Payment Successful" else "Payment Failed",
                color = if (isSuccess) Color.Green else MaterialTheme.colorScheme.error
            )
        },
        text = { Text(result) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}