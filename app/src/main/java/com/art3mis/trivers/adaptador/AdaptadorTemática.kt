package com.art3mis.trivers.adaptador

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.art3mis.trivers.interfaces.ICargarMas
import com.art3mis.trivers.modelos.Item_Tematica
import com.art3mis.trivers.R
import kotlinx.android.synthetic.main.item_cargando.view.*
import kotlinx.android.synthetic.main.item_tematicas.view.*

internal class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {//No se usa pero se deja por si algo
    var progressBar:ProgressBar=view.progressBar
}

internal class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){//Solo tematicas
    var tematicaTrivias:TextView=itemView.tematicaTrivias
    var numTrivias:TextView=itemView.numTrivias
}

internal class nullViewHolder(view:View):RecyclerView.ViewHolder(view){

}



class AdaptadorTemática(reciclerView: RecyclerView,internal var activity: Activity,internal var itemTematicas: MutableList<Item_Tematica>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM=0
    private val VIEW_TYPE_LOADING=1
    internal var cargar: ICargarMas?=null
    var isCargando:Boolean = false
    internal var visibleThreshold=5
    internal var lastVisibleItem:Int=0
    internal var totalItemCount:Int =0

    init {
        val linearLayoutManager=reciclerView.layoutManager as LinearLayoutManager
        reciclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount=linearLayoutManager.itemCount
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition()
                if(!isCargando&& totalItemCount<=lastVisibleItem+visibleThreshold) {
                    if (cargar != null)
                        cargar!!.onCargarMas()
                    isCargando = true
                }
            }
        })
    }


    override fun getItemViewType(position: Int): Int {
        return if(itemTematicas[position]==null)VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {//ViewGroup? y RecyclerView.ViewHolder? <<< Tenían el signo de interrogación, al final retornaba null
        if(viewType==VIEW_TYPE_ITEM){
            val view =LayoutInflater.from(activity).inflate(R.layout.item_tematicas,parent,false)
            return ItemViewHolder(view)
        }
        else if(viewType==VIEW_TYPE_LOADING){
            val view =LayoutInflater.from(activity).inflate(R.layout.item_cargando,parent,false)
            return LoadingViewHolder(view)
        }
        return  nullViewHolder(view = LayoutInflater.from(activity).inflate(R.layout.item_cargando,parent,false))
    }

    override fun getItemCount(): Int {
        return itemTematicas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {//RecyclerView.ViewHolder? <<< tenía el signo de interrogación
        if(holder is ItemViewHolder){
            holder.tematicaTrivias.text=itemTematicas[position]!!.tematicaTrivias
            holder.numTrivias.text="Hay "+itemTematicas[position]!!.numTrivias +" Trivias"
        }
        else if(holder is LoadingViewHolder){
            holder.progressBar.isIndeterminate=true
        }
    }

    fun setCargado(){
        isCargando=false
    }

    fun setCargarMas(iCargarMas: ICargarMas){
        this.cargar=iCargarMas
    }
}