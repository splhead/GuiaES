package com.silas.meditacao.io

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import com.silas.guiaes.activity.R

class TimePreference : DialogPreference {
    constructor(ctx: Context): super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?): super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, style: Int): super(ctx, attrs, style)
    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int, sAttr: Int): super(ctx, attrs, defStyle, sAttr)
    companion object {
        const val TIMEPREFERENCE_KEY = "pref_notification_hour"
        const val DEFAULT_VALUE = 360
    }

    private var mTime: Int = 0
    private val mDialogLayoutResId = R.layout.pref_dialog_time

    fun getTime(): Int {
        return mTime
    }

    fun setTime(time: Int) {
        mTime = time

        // save sharedprefs
        persistInt(time)
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a!!.getInt(index, DEFAULT_VALUE)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val pair: Pair<Int,Int>

        if (restorePersistedValue) {
            setTime(getPersistedInt(mTime))
            pair = TimePreferenceDialogFragmentCompat.hoursAndMinutes(mTime)

        } else {
            val time = defaultValue.toString().toInt()
            setTime(time)
            pair = TimePreferenceDialogFragmentCompat.hoursAndMinutes(time)
        }
        summary = "${pair.first.toString().padStart(2, '0')}:${pair.second.toString().padStart(2, '0')}"
    }

    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }
}