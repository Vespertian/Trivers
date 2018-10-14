package com.art3mis.trivers.Adaptador

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.art3mis.trivers.Interfaces.ICargarMas
import com.art3mis.trivers.Modelos.Item_Preguntas
import com.art3mis.trivers.R
import kotlinx.android.synthetic.main.item_pregunta.view.*

internal class ItemViewHolderPreguntas(item: View) : RecyclerView.ViewHolder(item){
    var Pregunta: TextView =item.Pregunta
    var respuesta1:TextView=item.R1
    var respuesta2:TextView=item.R2
    var respuesta3:TextView=item.R3
    var respuesta4:TextView=item.R4
    var respuesta:TextView=item.respuesta
}

class AdaptadorPreguntas(reciclerView: RecyclerView, internal var activity: Activity, internal var itemPreguntas: MutableList<Item_Preguntas>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM=0
    private val VIEW_TYPE_LOADING=1


    override fun getItemViewType(position: Int): Int {
        return if(itemPreguntas[position]==null)VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==VIEW_TYPE_ITEM){
            val view = LayoutInflater.from(activity).inflate(R.layout.item_pregunta,parent,false)
            return ItemViewHolderPreguntas(view)
        }
        else if(viewType==VIEW_TYPE_LOADING){
            val view = LayoutInflater.from(activity).inflate(R.layout.item_cargando,parent,false)
            return LoadingViewHolder(view)
        }
        return  nullViewHolder(view = LayoutInflater.from(activity).inflate(R.layout.item_cargando,parent,false))
    }

    override fun getItemCount(): Int {
        return itemPreguntas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ItemViewHolderPreguntas){
            holder.Pregunta.text=itemPreguntas[position]!!.pregunta
            holder.respuesta.text=itemPreguntas[position]!!.respuesta
            holder.respuesta1.text=itemPreguntas[position]!!.respuesta1
            holder.respuesta2.text=itemPreguntas[position]!!.respuesta2
            holder.respuesta3.text=itemPreguntas[position]!!.respuesta3
            holder.respuesta4.text=itemPreguntas[position]!!.respuesta4
        }
        else if(holder is LoadingViewHolder){
            holder.progressBar.isIndeterminate=true
        }
    }

}