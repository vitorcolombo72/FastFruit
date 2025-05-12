package com.example.fastfruit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterUserScreen : AppCompatActivity() {
    private lateinit var name_field: TextView
    private lateinit var age_field: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_user_screen)

        name_field = findViewById<TextView>(R.id.name_field)
        age_field = findViewById<TextView>(R.id.age_field)
        onFinishRegister()
    }

    private fun onFinishRegister(){
        val finishButton = findViewById<View>(R.id.finish_register_button)
        finishButton.setOnClickListener(){

            val name = name_field.text.toString()
            val ageT = age_field.text.toString()
            val age = ageT.toIntOrNull()

            val db = FirebaseFirestore.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val userData = hashMapOf(
                "nome" to name,
                "age" to age
            )
            db.collection("users").document(userId).collection("patients").document().set(userData)
                .addOnSuccessListener {
                    println("sucesso!")
                }.addOnFailureListener{
                    println("erro!")
                }
            val goToManagementScreen = Intent(this,ManagementScreen::class.java)
            startActivity(goToManagementScreen)
        }
    }
}