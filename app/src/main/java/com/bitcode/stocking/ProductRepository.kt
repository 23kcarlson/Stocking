package com.bitcode.stocking

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {

    val allProducts: Flow<List<Product>> = dao.getAll()

    @WorkerThread
    suspend fun insert(product: Product) {
        dao.insert(product)
    }

}