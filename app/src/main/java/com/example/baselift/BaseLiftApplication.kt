package com.example.baselift

import android.app.Application

class BaseLiftApplication : Application() {
    /**
     * Instancia de AppContainer que contém as dependências da aplicação
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
