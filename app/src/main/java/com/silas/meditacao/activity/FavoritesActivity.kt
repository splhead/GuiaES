package com.silas.meditacao.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.silas.guiaes.activity.R
import com.silas.meditacao.adapters.FavoritesListAdapter
import com.silas.meditacao.adapters.MeditacaoDBAdapter
import com.silas.meditacao.io.Util
import com.silas.meditacao.models.Meditacao
import kotlinx.android.synthetic.main.favorites_activity.*
import java.lang.ref.WeakReference
import java.util.*


class FavoritesActivity : ThemedActivity() {
    companion object {
        const val INTERSTIAL_AD_COUNTER_KEY = "interstial_counter"
        private const val TAG = "FavoritesActivity"
    }

    /*
    private lateinit var adLoader: AdLoader
    private val mNativeAds: ArrayList<UnifiedNativeAd> = ArrayList()*/
    private lateinit var favAdapter: FavoritesListAdapter
    private var mFavoritesItems: ArrayList<Meditacao> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var counterToShow = 0
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var adView: AdView
//    private var devotionals = ArrayList<Meditacao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites_activity)

        setupRecyclerView()
        FetchFavoriteTask(this).execute()

        adView = findViewById(R.id.ad_view)

        interstitialAd = InterstitialAd(this).apply {
            adUnitId = getString(R.string.interstitial_ad_id)
            adListener = (object : AdListener() {
                override fun onAdClosed() {
                    finish()
                }
            })
        }

        counterToShow = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(INTERSTIAL_AD_COUNTER_KEY, 0)

        counterToShow++

        if (counterToShow == 3) {
            counterToShow = 0
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putInt(INTERSTIAL_AD_COUNTER_KEY, counterToShow).apply()
        Log.i(TAG, counterToShow.toString())
    }

    fun setupAd() {

    }

    fun sendBack(d: Meditacao) {
        val intent = Intent()
        intent.putExtra("devotional", d)
        setResult(Activity.RESULT_OK, intent)
        if (counterToShow == 0
                && Util.notShabbat(Calendar.getInstance())) {

            showInterstial()
        } else {
            finish()
        }
    }

    private fun requestAd() {
        if (!interstitialAd.isLoading && !interstitialAd.isLoaded) {
            val adRequest = AdRequest.Builder()
//                    .addTestDevice("FC5AAA3D1C3842A79510C4C83BC27DD9")
                    .build()
            interstitialAd.loadAd(adRequest)
        }

        if (Util.notShabbat(Calendar.getInstance())) {
            adView.let {
                val adRequest = AdRequest.Builder().build()
                it.loadAd(adRequest)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        requestAd()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        adView.resume()
        super.onResume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun showInterstial() {
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.")
            finish()
        }
    }

    /*fun insertAdsInFavoritesItems() {
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
    }*/

    fun setDevotional(collection: ArrayList<Meditacao>) {
        mFavoritesItems.clear()
        mFavoritesItems = collection
//        loadNativeAds()
        favAdapter.updateFavItems(mFavoritesItems)
    }

    private fun setupRecyclerView() {
        recyclerView = favorites_recycler_view

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        favAdapter = FavoritesListAdapter(mFavoritesItems, this)
        recyclerView.adapter = favAdapter
    }


    private class FetchFavoriteTask(favoritesActivity: FavoritesActivity) : AsyncTask<Void, Void, ArrayList<Meditacao>>() {

        private val wr: WeakReference<FavoritesActivity> = WeakReference(favoritesActivity)

        override fun doInBackground(vararg params: Void?): ArrayList<Meditacao> {
            val meditacaoDBAdapter = MeditacaoDBAdapter(wr.get())
            return meditacaoDBAdapter.fetchFavorites()
        }

        override fun onPostExecute(meditacoes: ArrayList<Meditacao>) {
            wr.get()?.setDevotional(meditacoes)
            super.onPostExecute(meditacoes)
        }
    }
}