package com.silas.meditacao.activity

import android.content.Intent
import android.os.AsyncTask
import com.daimajia.androidanimations.library.Techniques
import com.silas.guiaes.activity.R
import com.silas.meditacao.adapters.MeditacaoDBAdapter
import com.silas.meditacao.models.Meditacao
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.model.ConfigSplash
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class LauncherActivity : AwesomeSplash() {
    override fun initSplash(configSplash: ConfigSplash) {

        configSplash.backgroundColor = R.color.textColorPrimary
        configSplash.animCircularRevealDuration = 500

        //Customize Logo
        configSplash.logoSplash = R.mipmap.ic_launcher //or any other drawable
        configSplash.animLogoSplashDuration = 100 //int ms
        configSplash.animLogoSplashTechnique = Techniques.Bounce //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Title
        configSplash.titleSplash = getString(R.string.app_name)
        configSplash.titleTextColor = R.color.colorPrimaryInverse
        configSplash.animTitleDuration = 100
        configSplash.titleFont = "fonts/GreatVibes-Regular.ttf"
//        configSplash.animTitleTechnique = Techniques.FlipInX
    }

    override fun animationsFinished() {
        LoadDevotionalTask(this).execute()
        /*startActivity(Intent(this, MainActivity::class.java))
        finish()*/
    }

    fun start(intent: Intent) {
        startActivity(intent)
        finish()
    }

    companion object {

        const val DEVOTIONALS = "devotionals"

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

//                if ( result!!.size > 0 ) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putParcelableArrayListExtra(DEVOTIONALS, result)
                activity.start(intent)
//                }
                super.onPostExecute(result)
            }
        }
    }

}