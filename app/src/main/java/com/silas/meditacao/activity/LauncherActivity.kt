package com.silas.meditacao.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.silas.guiaes.activity.R
import com.silas.meditacao.adapters.MeditacaoDBAdapter
import com.silas.meditacao.models.Meditacao
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
        window.decorView.systemUiVisibility += View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility += View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility += View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*/

        MobileAds.initialize(this, getString(R.string.app_ad_id))
        LoadDevotionalTask(this).execute()
    }

    fun start(intent: Intent) {
        startActivity(intent)
        finish()
    }

    companion object {

        class LoadDevotionalTask(activity: LauncherActivity)
            : AsyncTask<Void, Int, ArrayList<Meditacao>>() {

            private val wr: WeakReference<LauncherActivity> = WeakReference(activity)
            private val devotionals = ArrayList<Meditacao>()

            override fun doInBackground(vararg tipos: Void?): ArrayList<Meditacao> {
                val mdba = MeditacaoDBAdapter(wr.get())
                val day = Calendar.getInstance()
                var counter = 0

                MainActivity.TYPES.forEach { tipo ->
                    counter++
                    val devotional = mdba.buscaMeditacao(day, tipo!!)
                    devotional?.let { devotionals.add(devotional) }
                }

                return devotionals
            }

            override fun onPostExecute(result: ArrayList<Meditacao>?) {

                val activity = wr.get()
                if (activity == null || activity.isFinishing) return

                val intent = Intent(activity, MainActivity::class.java)
                intent.putParcelableArrayListExtra(Meditacao.DEVOTIONALS_ARRAY_KEY, result)

                Handler().postDelayed(
                        {
                            activity.start(intent)
                        },
                        1000L
                )


                super.onPostExecute(result)
            }
        }
    }

}