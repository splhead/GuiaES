package com.silas.meditacao.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.silas.guiaes.activity.R
import com.silas.meditacao.models.Meditacao
import kotlinx.android.synthetic.main.favorites_item.view.*

class FavoritesListAdapter(var favoriteViewItems: List<Any>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val DEVOTIONAL_ITEM_VIEW_TYPE = 0
        private const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1
    }

    override fun getItemViewType(position: Int): Int {
        val favoriteViewItem = favoriteViewItems[position]
        if (favoriteViewItem is UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE
        }
        return DEVOTIONAL_ITEM_VIEW_TYPE
    }

    fun updateFavItems(list: List<Any>) {
        favoriteViewItems = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.ad_unified, parent, false)
                return UnifiedAdViewHolder(unifiedNativeLayoutView)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.favorites_item
                        , parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteViewItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val nativeAd = favoriteViewItems[position] as UnifiedNativeAd
                (holder as UnifiedAdViewHolder).bindView()
                populateNativeAdView(nativeAd, holder.adView)

            }
            else -> {
                (holder as ViewHolder).bindView(favoriteViewItems[position] as? Meditacao)
            }
        }

    }

    private fun populateNativeAdView(nativeAd: UnifiedNativeAd, adview: UnifiedNativeAdView) {
        (adview.headlineView as TextView).text = nativeAd.headline
        (adview.bodyView as TextView).text = nativeAd.body
        (adview.callToActionView as TextView).text = nativeAd.callToAction

        val icon = nativeAd.icon

        if (icon == null) {
            adview.iconView.visibility = View.GONE
        } else {
            (adview.iconView as ImageView).setImageDrawable(icon.drawable)
            adview.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adview.priceView.visibility = View.GONE
        } else {
            adview.priceView.visibility = View.VISIBLE
            (adview.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adview.storeView.visibility = View.GONE
        } else {
            adview.storeView.visibility = View.VISIBLE
            (adview.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adview.starRatingView.visibility = View.GONE
        } else {
            (adview.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adview.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adview.advertiserView.visibility = View.GONE
        } else {
            (adview.advertiserView as TextView).text = nativeAd.advertiser
            adview.advertiserView.visibility = View.VISIBLE
        }

        adview.setNativeAd(nativeAd)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindView(devotional: Meditacao?) {
            val title = itemView.tvTitle
            val verse = itemView.tvVerse

            devotional?.let {
                title.text = it.titulo
                verse.text = it.textoBiblico
            }

        }
    }
}