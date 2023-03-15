package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    val editText = findViewById<EditText>(R.id.inputEditText)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backImage = findViewById<ImageView>(R.id.backButton)
        backImage.setOnClickListener {
            finish()
        }
        val clearButton = findViewById<ImageView>(R.id.clear_text)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        clearButton.setOnClickListener {
            editText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("myKey", editText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val myValue = savedInstanceState.getString("myKey")

        editText.setText(myValue)
    }
}
//    companion object {
//        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
//    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        val editText = findViewById<EditText>(R.id.inputEditText)
//        outState.putString(PRODUCT_AMOUNT,editText.text.toString())
//        super.onSaveInstanceState(outState)
//    }
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        val editText = findViewById<EditText>(R.id.inputEditText)
//        editText.setText(savedInstanceState.getString(PRODUCT_AMOUNT,""))
//        super.onRestoreInstanceState(savedInstanceState)
//
//    }
