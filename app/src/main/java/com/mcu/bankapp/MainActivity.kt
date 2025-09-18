package com.mcu.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.presentation.HomeScreen
import com.mcu.bankapp.presentation.PaymentScreen
import com.mcu.bankapp.presentation.home.HomeScreen
import com.mcu.bankapp.presentation.home.HomeViewModel
import com.mcu.bankapp.presentation.payment.PaymentScreen
import com.mcu.bankapp.presentation.payment.PaymentViewModel
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
                    PaymentApp(
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

    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.homeState.collectAsState()

    val paymentViewModel: PaymentViewModel = hiltViewModel()
    val paymentState by paymentViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HomeScreen
    ) {
        composable<HomeScreen> {
            HomeScreen(
                modifier = modifier,
                onDomesticTransferClick = {
                    navController.navigate(PaymentScreen("domestic"))
                },
                onInternationalTransferClick = {
                    navController.navigate(PaymentScreen("international"))
                },
                homeState = homeState
            )
        }

        composable<PaymentScreen> { backStackEntry ->
            val transferType = when (backStackEntry.toRoute<PaymentScreen>().transferType) {
                "domestic" -> TransferType.DOMESTIC
                "international" -> TransferType.INTERNATIONAL
                else -> TransferType.DOMESTIC
            }

            PaymentScreen(
                transferType = transferType,
                navigateToHome = {
                    navController.navigateUp()
                },
                uiState = paymentState,
                updateTransferType = { type: TransferType ->
                    paymentViewModel.updateTransferType(type)
                },
                clearPaymentResult = paymentViewModel::clearPaymentResult,
                resetForm = paymentViewModel::resetForm,
                updatePaymentData = { paymentData: PaymentData ->
                    paymentViewModel.updatePaymentData(paymentData)
                },
                processPayment = paymentViewModel::processPayment
            )
        }
    }
}