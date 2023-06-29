package com.bitcode.stocking

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ProductApplication: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { ProductRepository(database.productDao()) }
}