package com.seuapp.utils.com.example.fastfruit

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.fastfruit.PatientPanel
import com.example.fastfruit.R

class Adapter_FruitAcerto(private var arr: List<Double>): RecyclerView.Adapter<Adapter_FruitAcerto.ViewHolder>() {

    private val frutas = arrayOf(
        R.drawable.apple , R.drawable.banana , R.drawable.cherry , R.drawable.coconut ,
        R.drawable.grape , R.drawable.kiwi , R.drawable.lemon , R.drawable.mango ,
        R.drawable.orange , R.drawable.papaya , R.drawable.pear , R.drawable.persimmon ,
        R.drawable.pineapple , R.drawable.strawberry , R.drawable.tomato , R.drawable.watermelon ,

        )


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var porcentagemText: TextView = itemView.findViewById(R.id.porcentagem_view)
        var fruitImage: ImageView = itemView.findViewById(R.id.fruit_view)

        init{

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup ,
        viewType: Int
    ): Adapter_FruitAcerto.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_2, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Adapter_FruitAcerto.ViewHolder , position: Int) {
        val aux = arr[position]
        val aux2 = "%.2f".format((1-aux)*100)
        holder.porcentagemText.text = "$aux2%"
        holder.fruitImage.setImageResource(frutas[position])
    }

    override fun getItemCount(): Int {
        return frutas.size
    }

}