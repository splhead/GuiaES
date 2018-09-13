package com.silas.meditacao.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silas.guiaes.activity.R
import com.silas.meditacao.models.Meditacao
import kotlinx.android.synthetic.main.favorites_item.view.*

class FavoritesListAdapter(private val devotionals: List<Meditacao>,
                           private val context: Context) : RecyclerView.Adapter<FavoritesListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.favorites_item
                , parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devotionals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val devotional = devotionals[position]
        holder.title.text = devotional.titulo
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.tvTitulo
    }
}