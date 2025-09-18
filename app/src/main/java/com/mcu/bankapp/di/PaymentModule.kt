package com.mcu.bankapp.di

import com.mcu.bankapp.data.repository.PaymentRepositoryImpl
import com.mcu.bankapp.domain.repository.PaymentRepository
import com.mcu.bankapp.presentation.PaymentUseCase
import com.mcu.bankapp.presentation.PaymentUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {

    @Binds
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    abstract fun bindPaymentUseCase(
        paymentUseCaseImpl: PaymentUseCaseImpl
    ): PaymentUseCase
}