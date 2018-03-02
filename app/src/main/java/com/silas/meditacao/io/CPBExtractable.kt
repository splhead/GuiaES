/*
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
//            if (type == Meditacao.ADULTO) setLastUrlPreference(type, "https://mais.cpb.com.br/meditacao/fidelidade-incondicional/")
//            val lub = getLastUrlPreference(type)
//            Log.d("CPB", "Last url before $lub")
            initUrl(type)
            val today = Calendar.getInstance()
            val currentMonth = today.get(Calendar.MONTH) + 1
            val lastDay = getLastDayOfMonth(currentMonth)


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
                val month: Int = idate.substring(5, 7).toInt()

                */
/*if (day == 1 || day == 31) {
                    lastDay = getLastDayOfMonth(month)
                }*//*


                if ((day < lastDay && month == currentMonth) || month < currentMonth) {
                    val url: String = getUrl(document)
                    setLastUrlPreference(type, url)
                }


            } while ((day < lastDay && month == currentMonth) || month < currentMonth)
//            val lua = getLastUrlPreference(type)
//            Log.d("CPB", "Last url after $lua")
        } catch (e: HttpStatusException) {
//            e.printStackTrace()
            extractDevotional()

        } catch (e: Exception) {

//            e.printStackTrace()
            setLastUrlPreference(type, "")
        }
        return devotionals
    }

    private fun getUrl(document: Document): String {
        val urlElement = document.selectFirst("a.nextMeditacao")
        val nextElement = document.selectFirst("link[rel=next]")
        var href = ""
        if (urlElement != null) {
            if (urlElement.hasAttr("href")) {
                href = urlElement.attr("href")
                return href
            } else {
                if (nextElement != null) {
                    if (nextElement.hasAttr("href"))
                        href = nextElement.attr("href")
                    return href
                }
            }
        }
        href = getLastUrlPreference(type)
        return href
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

    private fun getLastDayOfMonth(month: Int): Int {
        val day = Calendar.getInstance()
        day.set(Calendar.MONTH, month - 1)
        return day.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun convertDate(date: String): String {
        try {
            val iday: Int = date.substring(0, 2).toInt()
            var imonth = ""
            val months = arrayOf("janeiro", "fevereiro", "março", "abril", "maio", "junho",
                    "julho", "agosto", "setembro", "outubro", "novembro", "dezembro")
            months
                    .filter { date.contains(it) }
                    .forEach { imonth = (months.indexOf(it) + 1).toString() }
            val currentYear = Calendar.getInstance()
            // YYYY-MM-DD
            return "${currentYear.get(Calendar.YEAR)}-${imonth.padStart(2, '0')}" +
                    "-${iday.toString().padStart(2, '0')}"
        } catch (e: Exception) {
            val devoltional = devotionals[devotionals.lastIndex]
            val dia = devoltional.data.substring(8, 10).toInt() + 1
            return "${devoltional.data.substring(0, 8)}${dia.toString().padStart(2, '0')}"
        }
    }

    private fun getLastUrlPreference(type: Int): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        return prefs.getString(getNomeTipo(type), "")
    }

    private fun setLastUrlPreference(type: Int, url: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        prefs.edit().putString(getNomeTipo(type), url).apply()
    }
}
*/
