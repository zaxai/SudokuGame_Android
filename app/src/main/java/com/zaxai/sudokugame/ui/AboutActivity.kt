package com.zaxai.sudokugame.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.zaxai.sudokugame.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val backBtn: ImageButton=findViewById(R.id.backBtn)
        val introduceText:TextView=findViewById(R.id.introduceText)
        val updateText:TextView=findViewById(R.id.updateText)
        backBtn.setOnClickListener {
            finish()
        }
        introduceText.setOnClickListener {
            val intent=Intent(this,IntroduceActivity::class.java)
            startActivity(intent)
        }
        updateText.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://www.zaxai.com/resource.php?resid=373")
            startActivity(intent)
        }
    }
}