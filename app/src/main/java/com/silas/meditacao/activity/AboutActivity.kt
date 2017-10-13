package com.silas.meditacao.activity

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.silas.guiaes.activity.R
import java.util.*

class AboutActivity : ThemedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val pInfo: PackageInfo
        var version = ""
        try {
            pInfo = packageManager.getPackageInfo(packageName, 0)
            version = pInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)

        val tvCopyright = findViewById<View>(R.id.copyright_textView) as TextView
        val out = "\u00A9 " + getString(R.string.title_activity_main) +
                " " + Calendar.getInstance().get(Calendar.YEAR) +
                " - versão: " + version
        tvCopyright.text = out
    }
}
