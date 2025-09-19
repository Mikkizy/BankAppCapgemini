package com.mcu.bankapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class MyCustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        // Instruct Hilt to use HiltTestApplication for your instrumented tests
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}