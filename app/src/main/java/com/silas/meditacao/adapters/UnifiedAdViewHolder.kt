//package com.silas.meditacao.adapters
//
//import android.view.View
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.ads.formats.UnifiedNativeAdView
//import kotlinx.android.synthetic.main.ad_unified.view.*
//
//class UnifiedAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    lateinit var adView: UnifiedNativeAdView
//
//    fun bindView() {
//        adView = itemView.ad_view
//        adView.mediaView = itemView.ad_media
//
//        adView.headlineView = itemView.ad_headline
//        adView.bodyView = itemView.ad_body
//        adView.callToActionView = itemView.ad_call_to_action
//        adView.iconView = itemView.ad_icon
//        adView.priceView = itemView.ad_price
//        adView.starRatingView = itemView.ad_stars
//        adView.storeView = itemView.ad_store
//        adView.advertiserView = itemView.ad_advertiser
//    }
//}