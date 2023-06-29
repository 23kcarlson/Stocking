package com.bitcode.stocking

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    val name: String,
    @PrimaryKey
    val upc: String,
    val quantity_front: Int,
    val quantity_back: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun isEmpty(): Boolean {
        return name == "" && upc == "" && quantity_front == 0 && quantity_back == 0
    }

    companion object : Parceler<Product> {

        override fun Product.write(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(upc)
            parcel.writeInt(quantity_front)
            parcel.writeInt(quantity_back)
        }

        override fun create(parcel: Parcel): Product {
            return Product(parcel)
        }
    }
}

@Dao
@Entity(tableName = "products")
interface ProductDao {

    @Query("SELECT * FROM products")
    fun getAll(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg products: Product)

    @Delete
    fun delete(vararg products: Product)


}
@Database(entities = [Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
           return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "product_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ProductDatabaseCallback(scope))
                    .build()
                instance
            }

        }
        private class ProductDatabaseCallback(
            private val scope: CoroutineScope
        ): Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                instance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.productDao())
                    }
                }
            }
            fun populateDatabase(productDao: ProductDao) {
                productDao.delete()

            }
        }
    }

}
