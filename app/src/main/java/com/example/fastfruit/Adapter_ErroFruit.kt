package com.seuapp.utils.com.example.fastfruit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fastfruit.R

class Adapter_ErroFruit(private var arr: List<Int>): RecyclerView.Adapter<Adapter_ErroFruit.ViewHolder>() {
    private val frutas = arrayOf(
        R.drawable.apple , R.drawable.banana , R.drawable.cherry , R.drawable.coconut ,
        R.drawable.grape , R.drawable.kiwi , R.drawable.lemon , R.drawable.mango ,
        R.drawable.orange , R.drawable.papaya , R.drawable.pear , R.drawable.persimmon ,
        R.drawable.pineapple , R.drawable.strawberry , R.drawable.tomato , R.drawable.watermelon ,

        )
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var erroText: TextView = itemView.findViewById(R.id.fruit_counterroview)
        var fruitImage: ImageView = itemView.findViewById(R.id.fruiterro_view)

        init{

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup ,
        viewType: Int
    ): Adapter_ErroFruit.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Adapter_ErroFruit.ViewHolder , position: Int) {
        val aux = arr[position]
        holder.erroText.text = "$aux"
        holder.fruitImage.setImageResource(frutas[position])
    }

    override fun getItemCount(): Int {
        return frutas.size
    }

}