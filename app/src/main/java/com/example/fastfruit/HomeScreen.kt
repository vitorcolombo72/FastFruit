package com.example.fastfruit

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.seuapp.utils.AuthManager

class HomeScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)


        startGame()
        login()
    }

    private fun startGame(){
        val startButton = findViewById<View>(R.id.startButton)

        startButton.setOnClickListener(){
            val goToGameScreen = Intent(this, GameScreen::class.java)
            startActivity(goToGameScreen)
        }
    }

    private fun login(){
        val loginButton = findViewById<View>(R.id.loginButton)
        val db = Firebase.firestore

        loginButton.setOnClickListener(){

            val goToLoginScreen = Intent(this,LoginScreen::class.java)
            startActivity(goToLoginScreen)

        }
    }



}