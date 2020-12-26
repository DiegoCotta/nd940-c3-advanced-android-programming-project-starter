package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    companion object {
        val STATUS = "download_status"
        val NAME = "download_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        if (intent.hasExtra(STATUS))
            if (intent.getBooleanExtra(STATUS, false)) {
                status.text = getString(R.string.success_text)
                status.setTextColor(getColor(R.color.successCollor))
            }else {
                status.text = getString(R.string.error_text)
                status.setTextColor(getColor(R.color.errorColor))
            }
        if (intent.hasExtra(NAME)) {
            name.text = intent.getStringExtra(NAME)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }

}
