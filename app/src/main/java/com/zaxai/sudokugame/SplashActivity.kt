package com.zaxai.sudokugame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.Exception
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        thread {
            try {
                Thread.sleep(1000)
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                runOnUiThread{
                    val intent= Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}