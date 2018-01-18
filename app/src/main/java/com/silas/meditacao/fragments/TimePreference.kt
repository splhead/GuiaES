package com.silas.meditacao.fragments

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import java.util.*

class TimePreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    private lateinit var picker: TimePicker
    private lateinit var time: Calendar
    @JvmField
    val NOVO = ""

    override fun onCreateDialogView(): View {
        time = Calendar.getInstance()
        picker = TimePicker(context)

        return picker
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.hour = time.get(Calendar.HOUR_OF_DAY)
        } else {
            picker.currentHour = time.get(Calendar.HOUR_OF_DAY)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                time.set(Calendar.HOUR, picker.hour)
                time.set(Calendar.MINUTE, picker.minute)
            } else {
                time.set(Calendar.HOUR, picker.currentHour)
                time.set(Calendar.MINUTE, picker.currentMinute)
            }

            summary = calendarToString(time)
            if (callChangeListener(time.timeInMillis)) {
                persistLong(time.timeInMillis)
                notifyChanged()
            }
        }
        super.onDialogClosed(positiveResult)
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a!!.getString(index)
    }

    private fun calendarToString(t: Calendar): String {
        return "${t.get(Calendar.HOUR)}:${t.get(Calendar.MINUTE)}"
    }
}