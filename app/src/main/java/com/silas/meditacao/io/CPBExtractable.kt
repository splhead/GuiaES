package com.silas.meditacao.io

import android.content.Context
import android.preference.PreferenceManager
import com.silas.meditacao.interfaces.Extractable
import com.silas.meditacao.models.Meditacao
import com.silas.meditacao.models.Meditacao.getNomeTipo
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*
import kotlin.collections.ArrayList

class CPBExtractable(context: Context, tp: Int) : Extractable {
    lateinit var document: Document
    lateinit var date: String
    var devotionals = ArrayList<Meditacao>()
    val ctx = context
    var type = tp

    override fun extractDevotional(): ArrayList<Meditacao> {
        try {

            initUrl(type)
            var lastDay = 28

            do {
                document = Jsoup.connect(getLastUrlPreference(type)).get()

                val title = document.selectFirst("div.titleMeditacao").text()

                date = document.selectFirst("div.descriptionText.diaMesMeditacao").text()
                val idate = convertDate(date)

                val verse = document.selectFirst("div.descriptionText.versoBiblico").text()

                val content: Element = document.selectFirst("div.conteudoMeditacao")

                var text = ""
                for (paragraph in content.select("p")) text += "${paragraph.text()} \n\n"

                val devotional = Meditacao(title, idate, verse, text, type)
                devotionals.add(devotional)

                val day: Int = idate.substring(8, 10).toInt()
                if (day == 1) {
                    lastDay = getLastDayOfMonth(idate.substring(5, 7).toInt())
                }

                if (day < lastDay) {
                    setLastUrlPreference(type, document.selectFirst("link[rel=next]")
                            .attr("href"))
                }


            } while (day < lastDay)
        } catch (e: HttpStatusException) {
//            e.printStackTrace()

        } catch (e: Exception) {

//            e.printStackTrace()
            setLastUrlPreference(type, "")
        }
        return devotionals
    }

    private fun initUrl(type: Int) {
        if (getLastUrlPreference(type) == "") {
            when (type) {
                Meditacao.ADULTO -> setLastUrlPreference(Meditacao.ADULTO, "https://mais.cpb.com.br/?post_type=meditacao&p=45805")
                Meditacao.MULHER -> setLastUrlPreference(Meditacao.MULHER, "https://mais.cpb.com.br/?post_type=meditacao&p=45840")
                Meditacao.JUVENIL -> setLastUrlPreference(Meditacao.JUVENIL, "https://mais.cpb.com.br/?post_type=meditacao&p=45874")
            }
        }
    }

    override fun contentIsUpdated(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getLastDayOfMonth(month: Int): Int {
        val day = Calendar.getInstance()
        day.set(Calendar.MONTH, month - 1)
        return day.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun convertDate(date: String): String {
        try {
            val iday: Int = date.substring(0, 2).toInt()
            var imonth = ""
            val months = arrayOf("janeiro", "fevereiro", "março", "abril", "maio", "junho",
                    "julho", "agosto", "setembro", "outubro", "novembro", "dezembro")
            for (month in months) {
                if (date.contains(month)) {
                    imonth = (months.indexOf(month) + 1).toString()
                }
            }
            val currentYear = Calendar.getInstance()
            // YYYY-MM-DD
            return "${currentYear.get(Calendar.YEAR)}-${imonth.padStart(2, '0')}" +
                    "-${iday.toString().padStart(2, '0')}"
        } catch (e: Exception) {
            val devoltional = devotionals.get(devotionals.lastIndex)
            val dia = devoltional.data.substring(8, 10).toInt() + 1
            return "${devoltional.data.substring(0, 8)}${dia.toString().padStart(2, '0')}"
        }
    }

    fun getLastUrlPreference(type: Int): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        return prefs.getString(getNomeTipo(type), "")
    }

    fun setLastUrlPreference(type: Int, url: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        prefs.edit().putString(getNomeTipo(type), url).apply()
    }
}