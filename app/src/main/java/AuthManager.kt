package com.seuapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun getAuth(): FirebaseAuth {
        return auth
    }
    // Retorna o usuário atualmente autenticado, ou null se não houver login ativo
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    fun setCurrentUser(){
        println(auth.currentUser?.uid)
    }

    // Retorna o ID do usuário autenticado, ou null se não estiver logado
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    fun getPatientsNames(callback: (List<String>,List<Int>,List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        val patientNames = mutableListOf<String>()
        val patientAges = mutableListOf<Int>()
        val patientIds = mutableListOf<String>()
        if (userId != null) {
            val patientsRef = db.collection("users").document(userId).collection("patients")
            patientsRef.get().addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("nome")
                    val age = document.getLong("age")?.toInt()
                    val id = document.id
                    if (name != null) {
                        patientNames.add(name)
                        if (age != null) {
                            patientAges.add(age)
                        }
                        patientIds.add(id)
                    }
                }
                callback(patientNames,patientAges,patientIds)
            }.addOnFailureListener {
                println("Erro ao buscar nomes")
                callback(emptyList(), emptyList(), emptyList())
            }
        } else {
            println("Não há usuário logado")
            callback(emptyList(), emptyList(), emptyList())
        }
    }
    // n total de jogos
    fun getNumGames(id: String,callback: (Int) -> Unit){
        val db = FirebaseFirestore.getInstance() // Instância do banco
        val userId = getCurrentUserId() // Usuário logado
        if (userId != null) { // Se o usuário estiver logado
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener  { result -> //acesso a coleção jogos foi sucesso!
                callback(result.size()) // retorna quantos jogos foram encontrados
            }

        }else { // erro
            callback(-1)
        }
    }
    // total de tempo
    fun getTotalTimePlayed(id: String,callback: (Double) -> Unit){
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        var tempototal: Double = 0.0
        if (userId != null) {
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener { result ->
                for(document in result){
                    val aux = document.getDouble("TempoPartida")
                    print("val:")
                    println(aux)
                    if (aux != null) {
                        tempototal += aux
                    }
                }
                callback(tempototal)
            }

        }else {
            callback(-1.0)
        }

    }
    // Porcentagem de sessoes completas
    fun getPSessaoCompleta(id: String, callback: (Double) -> Unit){
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        var sucessRate: Double = 0.0
        var cont: Int = 0
        if (userId != null) {
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener { result ->
                for(document in result){
                    val aux = document.getBoolean("SessaoCompleta")
                    print("val:")
                    println(aux)
                    if (aux == true) {
                        cont++
                    }
                }
                if(result.size() > 0) {
                    sucessRate = (cont.toDouble() / result.size().toDouble())
                    callback(sucessRate)
                }
            }

        }else {
            callback(-1.0)
        }
    }
    fun getPAcertos(id:String,callback: (Double) -> Unit){
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        var acertoRate: Double = 0.0
        var total: Long = 0
        var totalAcerto: Long = 0
        if (userId != null) {
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener { result ->
                for(document in result){
                    val aux = document.getLong("TotalAcertos")
                    val aux2 = document.getLong("TotalErros")
                    print("val:")
                    println(aux)
                    if (aux != null) {
                        totalAcerto += aux
                        total += aux
                    }
                    if (aux2 != null) {
                        total += aux2
                    }
                }
                    acertoRate = totalAcerto.toDouble() / total.toDouble()
                    callback(acertoRate)
            }

        }else {
            callback(-1.0)
        }
    }
    fun getAvgTime(id:String,callback: (Double) -> Unit){
        var time: Double = 0.0
        var nn: Int = 1
        var avgTime: Double = 0.0
        getTotalTimePlayed(id){ i ->
            time = i
            getNumGames(id){n ->
                nn = n
                if(nn != 0){
                    avgTime = time / nn.toDouble()
                    callback(avgTime)
                }
            }
        }
    }
    fun getPMissEachFruit(id: String,nomeFruta:String, callback: (Double) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        var contAparicao: Long = 0
        var contErros: Long = 0
        var missRate: Double = 0.0

        if (userId != null) {
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener { result ->
                val totalDocuments = result.size()
                var processedDocuments = 0
                for (document in result) {
                    val roundsRef = jogosRef.document(document.id).collection("ROUNDS")
                    roundsRef.whereEqualTo("Fruta Destaque", nomeFruta).get().addOnSuccessListener { rounds ->
                        for (round in rounds) {
                            val aux = round.getLong("Erros")
                            if (aux != null) {
                                contErros += aux
                                contAparicao += aux
                            }
                            contAparicao++
                        }
                        processedDocuments++
                        if (processedDocuments == totalDocuments) {
                            missRate = if (contAparicao != 0L) {
                                contErros.toDouble() / contAparicao.toDouble()
                            } else {
                                0.0
                            }
                            callback(missRate)
                        }
                    }.addOnFailureListener { e ->
                        println("Erro ao consultar rounds: $e")
                        callback(-1.0)
                    }
                }
            }.addOnFailureListener { e ->
                println("Erro ao consultar jogos: $e")
                callback(-1.0)
            }
        } else {
            callback(-1.0)
        }
    }
    fun getAvgTimePerRound(id: String, callback: (Double) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        var contRounds: Double = 0.0
        var countTime: Double = 0.0
        var missRate: Double = 0.0

        if (userId != null) {
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener { result ->
                val totalDocuments = result.size()
                var processedDocuments = 0
                for (document in result) {
                    val roundsRef = jogosRef.document(document.id).collection("ROUNDS")
                    roundsRef.get().addOnSuccessListener { rounds ->
                        for (round in rounds) {
                            val aux = round.getDouble("Tempo")
                            if (aux != null) {
                                countTime += aux
                            }
                            contRounds++
                        }
                        processedDocuments++
                        if (processedDocuments == totalDocuments) {
                            missRate = if (contRounds != 0.0) {
                                println("FODASE")
                                println(countTime)
                                println(contRounds)
                                countTime / contRounds
                            } else {
                                0.0
                            }
                            callback(missRate)
                        }
                    }.addOnFailureListener { e ->
                        println("Erro ao consultar rounds: $e")
                        callback(-1.0)
                    }
                }
            }.addOnFailureListener { e ->
                println("Erro ao consultar jogos: $e")
                callback(-1.0)
            }
        } else {
            callback(-1.0)
        }
    }
    fun getNumEachFruitWrong(id: String,nomeFruta:String, callback: (Int) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = getCurrentUserId()
        var cont: Int = 0

        if (userId != null) {
            val jogosRef = db.collection("users").document(userId)
                .collection("patients").document(id).collection("JOGOS")
            jogosRef.get().addOnSuccessListener { result ->
                val totalDocuments = result.size()
                var processedDocuments = 0
                for (document in result) {
                    val roundsRef = jogosRef.document(document.id).collection("ROUNDS")
                    roundsRef.whereArrayContains("Frutas erradas", nomeFruta).get().addOnSuccessListener { rounds ->
                        for (round in rounds) {
                            val aux = round.get("Frutas erradas") as List<String>
                            cont += aux.count() {it == nomeFruta}
                        }
                        processedDocuments++
                        if (processedDocuments == totalDocuments) {
                            callback(cont)
                        }
                    }.addOnFailureListener { e ->
                        println("Erro ao consultar rounds: $e")
                        callback(-1)
                    }
                }
            }.addOnFailureListener { e ->
                println("Erro ao consultar jogos: $e")
                callback(-1)
            }
        } else {
            callback(-1)
        }
    }

    // Faz logout do usuário
    fun signOut() {
        auth.signOut()
    }
}

