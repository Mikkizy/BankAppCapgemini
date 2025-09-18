package com.mcu.bankapp.di

import com.mcu.bankapp.domain.usecases.ProcessPaymentUseCase
import com.mcu.bankapp.domain.usecases.ValidatePaymentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideValidatePaymentUseCase(): ValidatePaymentUseCase {
        return ValidatePaymentUseCase()
    }

    @Provides
    @Singleton
    fun provideProcessPaymentUseCase(): ProcessPaymentUseCase {
        return ProcessPaymentUseCase()
    }
}