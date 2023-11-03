package com.example.lostnfound

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Handler().postDelayed({
            val intent = Intent(this@WelcomeActivity, LostFoundItemListActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)
    }
}
