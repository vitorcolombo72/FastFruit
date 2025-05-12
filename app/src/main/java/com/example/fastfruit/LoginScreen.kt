package com.example.fastfruit

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase
import com.seuapp.utils.AuthManager


class LoginScreen<FirebaseAuthInvalidCredentialsException> : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    //campos de nome e senha
    private lateinit var signin_email_field: EditText
    private lateinit var signin_password_field: EditText
    private lateinit var signup_email_field: EditText
    private lateinit var signup_password_field: EditText


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_screen)
        signin_email_field = findViewById<EditText>(R.id.signin_email_field)
        signin_password_field = findViewById<EditText>(R.id.signin_password_field)
        signup_email_field = findViewById<EditText>(R.id.signup_email_field)
        signup_password_field = findViewById<EditText>(R.id.signup_password_field)
        auth = AuthManager.getAuth()
        findViewById<View>(R.id.signin_button).setOnClickListener{onSignIn()}
        findViewById<View>(R.id.signup_button).setOnClickListener{onSignUp()}

    }
    private fun onSignUp() {
        val email = signup_email_field.text.toString()
        val password = signup_password_field.text.toString()
        if (validateFields(email , password , signup_email_field , signup_password_field)) {
            auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG , "createUserWithEmail:success")
                        val goToManagementScreen = Intent(this , ManagementScreen::class.java)
                        startActivity(goToManagementScreen)
                        //updateUI(user)
                    } else {
                        println(task.exception)
                        handleSignUpError(task.exception)
                    }

                }
        }
    }
    private fun onSignIn(){

        val email = signin_email_field.text.toString()
        val password = signin_password_field.text.toString()
        if (validateFields(email, password, signin_email_field, signin_password_field)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val goToManagementScreen = Intent(this, ManagementScreen::class.java)
                        startActivity(goToManagementScreen)
                    } else {
                        println(task.exception)
                        handleSignInError(task.exception)

                    }
                }
        }
    }

    private fun validateFields(email:String,password:String,emailField: EditText,passwordField:EditText): Boolean{
        var isValid = true;
        if(email.isEmpty()){
            emailField.error = "Campo de email não pode ficar vazio!"
            isValid = false
        }
        if (password.isEmpty()) {
            passwordField.error = "O campo de senha não pode estar vazio"
            isValid = false
        } else if (password.length < 6) {
            passwordField.error = "A senha deve ter pelo menos 6 caracteres"
            isValid = false
        }
        return isValid
    }
    private fun handleSignUpError(exception: Exception?) {
        Log.w("AUTH", "Erro no cadastro", exception)
        when (exception) {
            is FirebaseAuthWeakPasswordException -> signup_password_field.error = "Senha muito fraca. Escolha uma senha mais forte."
            is FirebaseAuthUserCollisionException -> signup_email_field.error = "Este e-mail já está cadastrado."
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> signup_email_field.error = "Email inválido, digite um email válido"
            //is FirebaseAuthInvalidCredentialsException -> signup_email_field.error = "E-mail inválido. Verifique e tente novamente."
            else -> {
                //showToast("Erro no cadastro: ${exception?.message}")
                Log.w("AUTH", "Erro no cadastro", exception)
            }
        }
    }

    private fun handleSignInError(exception: Exception?) {
        signin_password_field.error = "Usuário ou senha inválidos"
        Log.w("AUTH","erro",exception)
    }

    }


