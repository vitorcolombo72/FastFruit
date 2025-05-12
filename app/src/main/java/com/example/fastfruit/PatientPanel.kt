package com.example.fastfruit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seuapp.utils.AuthManager.getAvgTime
import com.seuapp.utils.AuthManager.getAvgTimePerRound
import com.seuapp.utils.AuthManager.getNumEachFruitWrong
import com.seuapp.utils.AuthManager.getNumGames
import com.seuapp.utils.AuthManager.getTotalTimePlayed
import com.seuapp.utils.AuthManager.getPSessaoCompleta
import com.seuapp.utils.AuthManager.getPAcertos
import com.seuapp.utils.AuthManager.getPMissEachFruit
import com.seuapp.utils.com.example.fastfruit.Adapter_ErroFruit
import com.seuapp.utils.com.example.fastfruit.Adapter_FruitAcerto
import com.seuapp.utils.com.example.fastfruit.RecyclerAdapter

class PatientPanel : AppCompatActivity() {
    private lateinit var nome: String
    private lateinit var id: String
    private var tam: Int = 0
    var arr = mutableListOf<Double>()
    private val frutasVetor: Array<String> = arrayOf(
        "Maçã", "Banana", "Cereja", "Coco", "Uva", "Kiwi", "Limão", "Manga",
        "Laranja", "Mamão", "Pêra", "Caqui", "Abacaxi", "Morango", "Tomate", "Melancia"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_panel)
        nome = intent.getStringExtra("NOME_PACIENTE").toString()
        id = intent.getStringExtra("ID_PACIENTE").toString()
        findViewById<TextView>(R.id.nome_textview).text = nome
        findViewById<View>(R.id.botao_comecarjogo).setOnClickListener{comecarJogo()}
        displayTam()
        displayTempo()
        displaySucessRate()
        displayPAcertos()
        displayAvgTime()
        displayPMissEachFruit()
        displayGetAvgTimePerRound()
        displayGetNumEachFruitWrong()

        }
    fun comecarJogo(){
        val intent = Intent(this, GameScreen::class.java)
        intent.putExtra("ID_PACIENTE",id)
        startActivity(intent)
    }
    fun displayTam(){
        val i: Int
        getNumGames(id) { i->
            //println("dentro de DisplayTam")
            //println(i)
            findViewById<TextView>(R.id.atributo1).text = "Número total de partidas: $i"
        }
    }
    fun displayTempo() {
        val i: Double
        getTotalTimePlayed(id) { i ->
            //println("dentro de displayTempo")
            //println(i)
            val formattedValue = "%.3f".format(i)
            findViewById<TextView>(R.id.atributo2).text = "Total de tempo jogado: $formattedValue segundos"
        }
    }
    fun displaySucessRate() {
        val i: Double
        getPSessaoCompleta(id) { i ->
            println("dentro de displaySucessRate")
            println(i)
            val percentage = "%.2f".format(i * 100)
            val n = 1 - i
            val percentage2 = "%.2f".format(n * 100)
            findViewById<TextView>(R.id.atributo3).text = "Porcentagem de sessões completas: $percentage%"
            findViewById<TextView>(R.id.atributo4).text = "Porcentagem de sessões incompletas: $percentage2%"
        }
    }
    fun displayPAcertos(){
        val i: Double
        getPAcertos(id) { i->
            println("dentro de displayPAcertos")
            println(i)
            val percentage = "%.2f".format(i * 100)
            val n = 1 - i
            val percentage2 = "%.2f".format(n * 100)
            findViewById<TextView>(R.id.atributo5).text = "Porcentagem de acertos: $percentage%"
            findViewById<TextView>(R.id.atributo6).text = "Porcentagem de erros: $percentage2%"
        }
    }
    fun displayAvgTime(){
        val i: Double
        getAvgTime(id) { i->
            println("dentro de displayAvgTime")
            println(i)
            val formattedValue = "%.3f".format(i)
            findViewById<TextView>(R.id.atributo7).text = "Tempo médio por partida: $formattedValue segundos"
        }
    }
    fun displayPMissEachFruit() {
        val arr = mutableListOf<Double>() // Lista para armazenar os dados
        val totalFruits = frutasVetor.size // Número total de frutas
        var completedCalls = 0 // Contador de chamadas concluídas

        // Lista de pares (fruta, valor)
        val fruitValuePairs = mutableListOf<Pair<String, Double>>()

        for (fruit in frutasVetor) {
            getPMissEachFruit(id, fruit) { i ->
                println("dentro de displayPMissEachFruit")
                println("$fruit: $i")
                fruitValuePairs.add(fruit to i) // Adiciona o par (fruta, valor)

                completedCalls++ // Incrementa o contador
                if (completedCalls == totalFruits) {
                    // Todas as chamadas foram concluídas
                    // Ordena os valores com base na ordem original das frutas
                    for (fruit in frutasVetor) {
                        val value = fruitValuePairs.find { it.first == fruit }?.second ?: 0.0
                        arr.add(value)
                    }

                    // Configura o RecyclerView
                    val recyclerView = findViewById<RecyclerView>(R.id.rv_pacertoview)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = Adapter_FruitAcerto(arr)
                }
            }
        }
    }
    fun displayGetAvgTimePerRound(){
        val i: Double
        getAvgTimePerRound(id) { i->
            println("dentro de displayGetAvgTimePerRound")
            println(i)
            val formattedValue = "%.3f".format(i)
            findViewById<TextView>(R.id.atributo8).text = "Tempo médio por rodada: $formattedValue segundos"
        }
    }
    fun displayGetNumEachFruitWrong(){
        val arr = mutableListOf<Int>() // Lista para armazenar os dados
        val totalFruits = frutasVetor.size // Número total de frutas
        var completedCalls = 0 // Contador de chamadas concluídas

        // Lista de pares (fruta, valor)
        val fruitValuePairs = mutableListOf<Pair<String, Int>>()

        for (fruit in frutasVetor) {
            getNumEachFruitWrong(id, fruit) { i ->
                println("dentro de displayPMissEachFruit")
                println("$fruit: $i")
                fruitValuePairs.add(fruit to i) // Adiciona o par (fruta, valor)

                completedCalls++ // Incrementa o contador
                if (completedCalls == totalFruits) {
                    // Todas as chamadas foram concluídas
                    // Ordena os valores com base na ordem original das frutas
                    for (fruit in frutasVetor) {
                        val value = fruitValuePairs.find { it.first == fruit }?.second ?: 0
                        arr.add(value)
                    }

                    // Configura o RecyclerView
                    val recyclerView = findViewById<RecyclerView>(R.id.rv_errofrutaview)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = Adapter_ErroFruit(arr)
                }
            }
        }
    }
    }
