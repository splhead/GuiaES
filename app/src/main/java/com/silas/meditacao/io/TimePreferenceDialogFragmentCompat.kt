package com.silas.meditacao.io

import android.os.Build
import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import com.silas.guiaes.activity.R

class TimePreferenceDialogFragmentCompat: PreferenceDialogFragmentCompat() {

    companion object {
        @JvmStatic fun newInstance(key: String): TimePreferenceDialogFragmentCompat {
            val fragment = TimePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }

        @JvmStatic fun hoursAndMinutes(min: Int): Pair<Int,Int> {
            val hours = (min / 60)
            val minutes = (min % 60)
            return Pair(hours, minutes)
        }
    }

    private lateinit var mTimePicker: TimePicker

    @Suppress("DEPRECATION")
    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        mTimePicker = view!!.findViewById(R.id.edit)
        val preference = getPreference()
        var minutesAfterMidnight = 0
        if (preference is TimePreference) {
            minutesAfterMidnight = preference.getTime()
        }

        minutesAfterMidnight.let {
            val pair = hoursAndMinutes(minutesAfterMidnight)
            val hours = pair.first
            val minutes = pair.second
            val is24hour = DateFormat.is24HourFormat(context)

//            preference.summary = "$hours:$minutes"

            mTimePicker.setIs24HourView(is24hour)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTimePicker.hour = hours
                mTimePicker.minute = minutes
            } else {
                mTimePicker.currentHour = hours
                mTimePicker.currentMinute = minutes
            }

        }
    }

    @Suppress("DEPRECATION")
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val hours = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTimePicker.hour
            } else {
                mTimePicker.currentHour
            }
            val minutes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTimePicker.minute
            } else {
                mTimePicker.currentMinute
            }

            val minutesAfterMidnight = hours * 60 + minutes
            val preference = preference
            if (preference is TimePreference) {
                if (preference.callChangeListener(minutesAfterMidnight)) {
                    preference.setTime(minutesAfterMidnight)
                    preference.summary = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
                }
            }
        }
    }
}
