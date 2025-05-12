package com.seuapp.utils.com.example.fastfruit

import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.fastfruit.GameScreen
import com.example.fastfruit.ManagementScreen
import com.example.fastfruit.PatientPanel
import com.example.fastfruit.R
import org.w3c.dom.Text

class RecyclerAdapter (private var nomes: List<String>, private var idades: List<Int>,private var images: List<Int>,
    private var ids: List<String>) :
RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.name_view)
        val itemAge: TextView = itemView.findViewById(R.id.age_view)
        val itemImage: ImageView = itemView.findViewById(R.id.image_view)
        init{
            itemView.setOnClickListener{v: View ->
                val position: Int = adapterPosition
                Toast.makeText(itemView.context,"clicou # ${position+1}${nomes[position]}",Toast.LENGTH_SHORT).show()
                val intent = Intent(itemView.context, PatientPanel::class.java)
                intent.putExtra("NOME_PACIENTE",nomes[position])
                intent.putExtra("ID_PACIENTE",ids[position])
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = nomes[position]
        holder.itemAge.text = idades[position].toString()
        holder.itemImage.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return nomes.size
    }
}


