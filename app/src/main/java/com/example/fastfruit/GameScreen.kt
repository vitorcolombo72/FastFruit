package com.example.fastfruit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.seuapp.utils.AuthManager
import com.seuapp.utils.AuthManager.getNumGames

class GameScreen : AppCompatActivity() {

    private lateinit var imageButtons: List<ImageView>
    private lateinit var frutasEmJogo: MutableList<Int>
    private var frutaAtual: Int = 0
    private var nivel: Int = 1
    private var acertos: Int = 0
    private var erros: Int = 0
    private var totalAcertos: Int = 0
    var sessaoCompleta: Boolean = false
    private lateinit var relatorio: StringBuilder
    private lateinit var hora_inicio: ZonedDateTime
    private lateinit var hora_fim: ZonedDateTime
    private lateinit var id: String
    private lateinit var jogosRef: DocumentReference
    private lateinit var docP: String
    private val frutas = arrayOf(
        R.drawable.apple , R.drawable.banana , R.drawable.cherry , R.drawable.coconut ,
        R.drawable.grape , R.drawable.kiwi , R.drawable.lemon , R.drawable.mango ,
        R.drawable.orange , R.drawable.papaya , R.drawable.pear , R.drawable.persimmon ,
        R.drawable.pineapple , R.drawable.strawberry , R.drawable.tomato , R.drawable.watermelon ,

        )
    private val frutasNomes = mapOf(
        R.drawable.apple to "Maçã" ,
        R.drawable.banana to "Banana" ,
        R.drawable.cherry to "Cereja" ,
        R.drawable.coconut to "Coco" ,
        R.drawable.grape to "Uva" ,
        R.drawable.kiwi to "Kiwi" ,
        R.drawable.lemon to "Limão" ,
        R.drawable.mango to "Manga" ,
        R.drawable.orange to "Laranja" ,
        R.drawable.papaya to "Mamão" ,
        R.drawable.pear to "Pêra" ,
        R.drawable.persimmon to "Caqui" ,
        R.drawable.pineapple to "Abacaxi" ,
        R.drawable.strawberry to "Morango" ,
        R.drawable.tomato to "Tomate" ,
        R.drawable.watermelon to "Melancia"
    )




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        id = intent.getStringExtra("ID_PACIENTE").toString()
        startGame()
    }

    override fun onDestroy() {
        val tempoDeJogo: Double = tempoDeJogo().toMillis().toDouble() / 1000
        println("$totalAcertos, $erros, $tempoDeJogo, $sessaoCompleta")
        addGameStatsToDB(totalAcertos,erros,tempoDeJogo,sessaoCompleta)
        super.onDestroy()
    }

    private fun startGame() {
        selecionarLayout()
        getImagesButton()
        frutasEmJogo = frutasSelecionadas().toMutableList()
        definirImagemDestaque()
        posicionarImagens()
        hora_inicio = horaAtual()
        addJogoToDB()
        jogar()

    }

    private fun horaAtual(): ZonedDateTime {
        val datahora = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        return datahora
    }

    private fun selecionarLayout() {
        when (getNivel()) {
            1 -> setContentView(R.layout.activity_game_screen_nivel_one)
            2 -> setContentView(R.layout.activity_game_screen_nivel_two)
            3 -> setContentView(R.layout.activity_game_screen_nivel_three)
        }
    }

    private fun getNivel(): Int {
        return nivel
    }

    private fun qtdFrutas(): Int {
        return when (getNivel()) {
            1 -> 4
            2 -> 9
            3 -> 16
            else -> 0
        }
    }

    private fun getImagesButton() {
        imageButtons = (1..qtdFrutas()).mapNotNull { i ->
            val id = resources.getIdentifier("fruit_$i" , "id" , packageName)
            findViewById(id)
        }
    }

    private fun frutasSelecionadas(): List<Int> {
        return ((frutas.indices).shuffled().take(qtdFrutas())).map { frutas[it] }
    }

    private fun definirImagemDestaque() {
        if (frutasEmJogo.isNotEmpty()) {
            frutaAtual = frutasEmJogo.random()
            trocarImagem(findViewById(R.id.fruta_destaque) , frutaAtual)
        }
    }

    private fun trocarImagem(imagemAtual: ImageView , idNovaImagem: Int) {
        imagemAtual.setImageResource(idNovaImagem)
    }

    private fun posicionarImagens() {
        getImagesButton()
        imageButtons.forEachIndexed { index , image ->
            val img = frutasEmJogo
            trocarImagem(image , img[index])
        }
    }

    private fun removerImagem(image: ImageView) {
        frutasEmJogo.remove(frutaAtual)
        image.visibility = View.INVISIBLE
        image.isEnabled = false
    }

    private fun verificarNivel() {
        if (acertos == 4) {
            nivel++
            if (nivel == 4) {
                sessaoCompleta = true
                finalizarJogo()
            }
            acertos = 0
            resetarJogo()
        }
    }

    private fun resetarJogo() {
        frutasEmJogo = frutasSelecionadas().toMutableList()
        acertos = 0
        selecionarLayout()
        imageButtons
        definirImagemDestaque()
        posicionarImagens()
        jogar()
    }

    private fun acerto() {
        totalAcertos++
        acertos++
    }

    private fun erro() {
        erros++
    }

    private fun tempoDeJogo(): Duration {
        return Duration.between(hora_inicio , hora_fim)
    }

    private fun finalizarJogo() {
        hora_fim = horaAtual()
        startActivity(Intent(this , ManagementScreen::class.java))
        this.finish()
    }

    override fun onStop() {
        super.onStop()
        finalizarJogo()
    }


    private fun piscarImagem(imageView: ImageView) {
        val animator = ObjectAnimator.ofFloat(imageView , "alpha" , 1f , 0f , 1f)
        animator.duration = 500 // Duração de cada ciclo (0.5s)
        animator.repeatCount = ValueAnimator.INFINITE // Piscar infinitamente
        animator.start()
    }

    private fun dicas(imageView: ImageView) {
        println("Entrou")
        piscarImagem(imageView)
    }

    private fun jogar() {
        var erro_round = 0
        var frutaserradas_round: MutableList<String> = mutableListOf()
        val aux = frutasEmJogo.toList()
        var tempoini = horaAtual()
        var tempofim: ZonedDateTime
        var hash: HashMap<String , Any>
            imageButtons.forEachIndexed { index , image ->
                image.setOnClickListener {
                    if (frutaAtual == aux[index]) {
                        if (erros > 2) dicas(image)
                        acerto()
                        verificarNivel()
                        removerImagem(image)
                        definirImagemDestaque()
                        tempofim = horaAtual()
                        var t = Duration.between(tempoini , tempofim)
                        hash = printRound(erro_round , aux[index] , frutaserradas_round , t)
                        addRoundToDB(hash)
                        tempoini = tempofim
                        erro_round = 0
                        frutaserradas_round.clear()
                    } else {
                        frutasNomes[aux[index]]?.let { it1 -> frutaserradas_round.add(it1) }
                        erro_round++
                        erro()
                    }
                }
            }

    }

    fun printRound(
        erro_round: Int ,
        fruta_round: Int ,
        frutaserradas_round: MutableList<String> ,
        t: Duration
    ):
            HashMap<String , Any> {
        val nomeFruta = frutasNomes[fruta_round] ?: "Fruta Desconhecida"
        val temp: Double = t.toMillis().toDouble() / 1000
        println("Erros no round: $erro_round")
        println("Fruta do round: $nomeFruta")
        println("Frutas erradas do round: $frutaserradas_round")
        println("tempo: $temp")
        val roundData = hashMapOf(
            "Erros" to erro_round ,
            "Fruta Destaque" to nomeFruta ,
            "Frutas erradas" to frutaserradas_round ,
            "Tempo" to temp
        )
        return roundData
    }
    fun addJogoToDB(){
        val db = FirebaseFirestore.getInstance()
        val userId = AuthManager.getCurrentUserId().toString()

        // Referência para a coleção "JOGOS" do paciente
        jogosRef = db.collection("users")
            .document(userId)
            .collection("patients")
            .document(id)
            .collection("JOGOS")
            .document()

    }
    fun addRoundToDB(hash: HashMap<String, Any>) {
            // Cria a nova coleção e adiciona os dados
            jogosRef.collection("ROUNDS")
                .document()
                .set(hash)
                .addOnSuccessListener {
                    println("Sucesso! Nova coleção criada: ")
                }
                .addOnFailureListener { e ->
                    println("Erro ao criar coleção: $e")
                }
        }
    fun addGameStatsToDB(totalAcertos:Int,totalErros:Int,tempoJogo:Double,sessao:Boolean){
        val dataGame = hashMapOf(
            "TotalErros" to totalErros ,
            "TotalAcertos" to totalAcertos ,
            "TempoPartida" to tempoJogo ,
            "SessaoCompleta" to sessao
        )
        jogosRef.set(dataGame)
    }
    }






