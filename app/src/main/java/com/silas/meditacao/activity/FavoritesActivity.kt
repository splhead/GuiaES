package com.silas.meditacao.activity

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.silas.guiaes.activity.R
import com.silas.meditacao.adapters.FavoritesListAdapter
import com.silas.meditacao.adapters.MeditacaoDBAdapter
import com.silas.meditacao.models.Meditacao
import kotlinx.android.synthetic.main.favorites_activity.*
import java.lang.ref.WeakReference


class FavoritesActivity : ThemedActivity() {
    companion object {
        const val NUMBER_OF_ADS = 2
    }

    private lateinit var adLoader: AdLoader
    private val mNativeAds: ArrayList<UnifiedNativeAd> = ArrayList()
    private lateinit var favAdapter: FavoritesListAdapter
    private var mFavoritesItems: ArrayList<Any> = ArrayList()
    private lateinit var recyclerView: RecyclerView
//    private var devotionals = ArrayList<Meditacao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites_activity)

        setupRecyclerView()
        FetchFavoriteTask(this).execute()

    }

    fun insertAdsInFavoritesItems() {
        if (mNativeAds.size <= 0) {
            return
        }

        val offset = (mFavoritesItems.size / mNativeAds.size) + 1
        var index = 0
        mNativeAds.forEach { ad ->
            mFavoritesItems.add(index, ad)
            index += offset
        }
        favAdapter.updateFavItems(mFavoritesItems)
    }

    fun loadNativeAds() {
        val builder = AdLoader.Builder(this, getString(R.string.ad_unit_id))
        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd ->
            // A native ad loaded successfully, check if the ad loader has finished loading
            // and if so, insert the ads into the list.
            mNativeAds.add(unifiedNativeAd)
            if (!adLoader.isLoading) {
                insertAdsInFavoritesItems()
            }
        }
                .withAdListener(
                        object : AdListener() {
                            override fun onAdFailedToLoad(errorCode: Int) {
                                // A native ad failed to load, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.
                                Log.e("FavoritesActivity", "The previous native ad failed to load. Attempting to" + " load another.")
                                if (!adLoader.isLoading) {
                                    insertAdsInFavoritesItems()
                                }
                            }
                        }).build()

        // Load the Native Express ad.
        adLoader.loadAds(AdRequest.Builder().build(), NUMBER_OF_ADS)
    }

    fun setDevotional(collection: ArrayList<Any>) {
        mFavoritesItems.clear()
        mFavoritesItems = collection
        loadNativeAds()
        favAdapter.updateFavItems(mFavoritesItems)
    }

    private fun setupRecyclerView() {
        recyclerView = favorites_recycler_view

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        favAdapter = FavoritesListAdapter(mFavoritesItems)
        recyclerView.adapter = favAdapter
    }


    private class FetchFavoriteTask internal constructor(favoritesActivity: FavoritesActivity) : AsyncTask<Void, Void, ArrayList<Meditacao>>() {

        private val wr: WeakReference<FavoritesActivity> = WeakReference(favoritesActivity)

        override fun doInBackground(vararg params: Void?): ArrayList<Meditacao> {
            val meditacaoDBAdapter = MeditacaoDBAdapter(wr.get())
            return meditacaoDBAdapter.fetchFavorites()
        }

        override fun onPostExecute(meditacoes: ArrayList<Meditacao>) {
            wr.get()?.setDevotional(meditacoes as ArrayList<Any>)
            super.onPostExecute(meditacoes)
        }
    }
}