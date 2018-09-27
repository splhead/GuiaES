package com.silas.meditacao.activity

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.silas.guiaes.activity.R
import com.silas.meditacao.adapters.FavoritesListAdapter
import com.silas.meditacao.adapters.MeditacaoDBAdapter
import com.silas.meditacao.models.Meditacao
import kotlinx.android.synthetic.main.favorites_activity.*
import java.lang.ref.WeakReference

class FavoritesActivity : ThemedActivity() {
    private lateinit var favAdapter: FavoritesListAdapter
    private var devotionals = ArrayList<Meditacao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites_activity)

        fetchFavoriteTask(this).execute()

    }

    fun setDevotional(devotionals: ArrayList<Meditacao>) {
        this.devotionals.clear()
        this.devotionals = devotionals
        val recyclerView = favorites_recycler_view
        favAdapter = FavoritesListAdapter(devotionals, this)
        recyclerView.adapter = favAdapter

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
    }


    private class fetchFavoriteTask internal constructor(favoritesActivity: FavoritesActivity) : AsyncTask<Void, Void, ArrayList<Meditacao>>() {

        private val wr: WeakReference<FavoritesActivity> = WeakReference(favoritesActivity)

        override fun doInBackground(vararg params: Void?): ArrayList<Meditacao> {
            val meditacaoDBAdapter = MeditacaoDBAdapter(wr.get())
            return meditacaoDBAdapter.fetchFavorites()
        }

        override fun onPostExecute(meditacoes: ArrayList<Meditacao>) {
            super.onPostExecute(meditacoes)
            wr.get()?.setDevotional(meditacoes)
        }
    }
}