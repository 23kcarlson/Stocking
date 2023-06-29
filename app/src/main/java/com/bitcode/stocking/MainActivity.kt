package com.bitcode.stocking

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as ProductApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.list_product)
        val adapter = ProductAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //add FAB
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val intent = Intent(this@MainActivity, EditItem::class.java)
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.extras?.getParcelable("product",Product::class.java)?.let { product ->
                        productViewModel.insert(product)
                    }
                  }
                else{
                    @Suppress("DEPRECATION")
                    data?.extras?.getParcelable<Product>("product")?.let { product ->
                        productViewModel.insert(product)
                    }
                }
            }
            else{
                Toast.makeText(
                    applicationContext,
                    "No product added",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        fab.setOnClickListener {
            resultLauncher.launch(intent)
        }
        //Batch add UPCs
        fab.setOnLongClickListener{
            val batchIntent = Intent(this@MainActivity, BatchAdd::class.java)
            val batchResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data?.extras?.getParcelable("product",Product::class.java)?.let { product ->
                            productViewModel.insert(product)
                        }
                    }
                    else{
                        @Suppress("DEPRECATION")
                        data?.extras?.getParcelable<Product>("product")?.let { product ->
                            productViewModel.insert(product)
                        }
                    }
                }
                else{
                    Toast.makeText(
                        applicationContext,
                        "No product added",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            batchResultLauncher.launch(batchIntent)
            true
        }
        productViewModel.allProducts.observe(this) { products ->
            // Update the cached copy of the words in the adapter.
            products.let { adapter.submitList(it) }
        }
    }

    }

