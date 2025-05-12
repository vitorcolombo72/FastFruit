package com.example.fastfruit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth
import com.seuapp.utils.AuthManager
import com.seuapp.utils.AuthManager.getPatientsNames
import com.seuapp.utils.com.example.fastfruit.RecyclerAdapter

class ManagementScreen : AppCompatActivity() {
    private var patientNames = mutableListOf<String>()
    private var patientAges = mutableListOf<Int>()
    private var patientImages = mutableListOf<Int>()
    private var patientIds = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_management_screen)
        onRegisterUser()
        displayUsers()
        findViewById<View>(R.id.signout_button).setOnClickListener{onSignOut()}
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(patientNames,patientAges,patientImages,patientIds)


    }
    private fun onSignOut(){
        AuthManager.getAuth().signOut()
        val goLoginScreen = Intent(this,LoginScreen::class.java)
        startActivity(goLoginScreen)
    }

    private fun onRegisterUser(){
        val registerButton = findViewById<View>(R.id.register_user_button)
        registerButton.setOnClickListener(){
            val goRegisterUserScreen = Intent(this,RegisterUserScreen::class.java)
            startActivity(goRegisterUserScreen)
        }
    }

    private fun displayUsers(){

        getPatientsNames { pNames,pAges,pIds ->
            attNames(pNames,pAges,pIds)
        }
        
    }
    private fun attNames(pNames: List<String>,pAges: List<Int>,pIds: List<String>){
        patientNames.addAll(pNames)
        patientAges.addAll(pAges)
        patientIds.addAll(pIds)
        patientImages.addAll(List(pNames.size){ R.mipmap.ic_launcher_round})
        Log.d("RecyclerView", "Pacientes: $patientNames")
        Log.d("RecyclerView", "Idades: $patientAges")
        Log.d("RecyclerView", "Ids: $patientIds")
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recyclerView)
        recyclerView.adapter?.notifyDataSetChanged()

    }
    private fun getPatientActive(i: Int): String{
        return patientNames[i]
    }
}