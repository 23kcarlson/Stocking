package com.bitcode.stocking

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class EditItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        val productName = findViewById<EditText>(R.id.edit_product)
        val product : Product? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product",Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("product")
        }

        val upc = findViewById<EditText>(R.id.edit_upc)
        val quantityBack = findViewById<EditText>(R.id.edit_amount_back)
        val quantityFront = findViewById<EditText>(R.id.edit_amount_shelves)

        //if product is not null, set textboxes to product values
        if(product != null){
            productName.setText(product.name)
            upc.setText(product.upc)
            quantityBack.setText(product.quantity_back.toString())
            quantityFront.setText(product.quantity_front.toString())
        }

        val scanButton = findViewById<Button>(R.id.button_scan)
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E)
            .build()

        scanButton.setOnClickListener {
            val scanner = GmsBarcodeScanning.getClient(this,options)
            scanner.startScan().addOnSuccessListener {
                upc.setText(it.rawValue)
            }

        }
        val saveButton = findViewById<Button>(R.id.button_save)
        saveButton.setOnClickListener {
            val replyIntent = Intent()
            val savedProduct = Product(
                productName.text.toString(),
                upc.text.toString(),
                quantityFront.text.toString().toInt(),
                quantityBack.text.toString().toInt()
            )
            if (savedProduct.isEmpty()) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra("product", savedProduct)

                setResult(RESULT_OK, replyIntent)
            }
            finish()
        }
    }
}