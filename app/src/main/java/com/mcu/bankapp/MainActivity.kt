package com.mcu.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.presentation.PaymentScreen
import com.mcu.bankapp.presentation.TransferPaymentScreen
import com.mcu.bankapp.ui.theme.BankAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TransferPaymentScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentApp(
    modifier: Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                modifier = modifier,
                onDomesticTransferClick = {
                    navController.navigate("payment/domestic")
                },
                onInternationalTransferClick = {
                    navController.navigate("payment/international")
                }
            )
        }

        composable("payment/domestic") {
            PaymentScreen(transferType = TransferType.DOMESTIC)
        }

        composable("payment/international") {
            PaymentScreen(transferType = TransferType.INTERNATIONAL)
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    onDomesticTransferClick: () -> Unit,
    onInternationalTransferClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("HomeScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bank Payment App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onDomesticTransferClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("DomesticTransferButton")
        ) {
            Text("Domestic Transfer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onInternationalTransferClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("InternationalTransferButton")
        ) {
            Text("International Transfer")
        }
    }
}