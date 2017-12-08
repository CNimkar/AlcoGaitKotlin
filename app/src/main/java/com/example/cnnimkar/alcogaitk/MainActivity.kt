package com.example.cnnimkar.alcogaitk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val SOBER = 0.00..0.01
    val TIPSY = 0.02..0.06
    val DRUNK = 0.07..0.125
    val WASTED = 0.13..0.25




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        bacInputButton.setOnClickListener({
            var alcoholInput = bacInputEditText
                                .text
                                .toString()
                                .toDouble()


            when {
                alcoholInput in SOBER ->  GlideApp
                        .with(this)
                        .load(R.drawable.sober)
                        .centerCrop()
                        .into(avatarImage)

                alcoholInput in TIPSY ->  GlideApp
                        .with(this)
                        .load(R.drawable.tipsy)
                        .centerCrop()
                        .into(avatarImage)

                alcoholInput in DRUNK ->  GlideApp
                        .with(this)
                        .load(R.drawable.drunk)
                        .centerCrop()
                        .into(avatarImage)

                alcoholInput in WASTED ->  GlideApp
                        .with(this)
                        .load(R.drawable.wasted)
                        .centerCrop()
                        .into(avatarImage)
                else -> GlideApp
                        .with(this)
                        .load(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(avatarImage)
            }

        })









    }
}
