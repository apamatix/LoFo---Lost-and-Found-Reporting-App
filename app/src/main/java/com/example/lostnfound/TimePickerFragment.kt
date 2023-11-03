package com.example.lostnfound

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {
    companion object {
        const val EXTRA_TIME = "com.android.lostnfound.date"
        private const val ARG_TIME = "time"

        fun newInstance(time: Date): TimePickerFragment {
            val args = Bundle()
            args.putSerializable(ARG_TIME, time)

            val fragment = TimePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mTimePicker: TimePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val time = arguments?.getSerializable(ARG_TIME) as Date

        val calendar = Calendar.getInstance()
        calendar.time = time
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val v: View = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_time, null)

        mTimePicker = v.findViewById(R.id.dialog_time_picker)
        mTimePicker.hour = hour
        mTimePicker.minute = minute

        return AlertDialog.Builder(activity)
            .setView(v)
            .setTitle(R.string.time_picker_title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val hour = mTimePicker.hour
                val minute = mTimePicker.minute
                val calendar = Calendar.getInstance()
                calendar.time = time
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val newTime: Date = calendar.time
                sendResult(Activity.RESULT_OK, newTime)
            }
            .create()
    }

    private fun sendResult(resultCode: Int, date: Date) {
        targetFragment?.let {
            val intent = Intent()
            intent.putExtra(EXTRA_TIME, date)

            it.onActivityResult(targetRequestCode, resultCode, intent)
        }
    }
}
