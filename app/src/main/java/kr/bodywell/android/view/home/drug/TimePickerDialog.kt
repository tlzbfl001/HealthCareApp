package kr.bodywell.android.view.home.drug

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import kr.bodywell.android.R

class TimePickerDialog(context: Context, timePickerClickListener: TimePickerClickListener) : Dialog(context) {
    private var timePicker : TimePicker? = null
    private var listener = timePickerClickListener
    private var tvCancel : TextView? = null
    private var tvConfirm : TextView? = null
    private var setHourValue = 0
    private var setMinuteValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_drug_time)

        timePicker = findViewById(R.id.timePicker)
        tvCancel = findViewById(R.id.tvCancel)
        tvConfirm = findViewById(R.id.tvConfirm)

        timePicker?.setOnTimeChangedListener(TimePicker.OnTimeChangedListener { _, hour, minute ->
            setHourValue = hour
            setMinuteValue = minute
        })

        tvConfirm?.setOnClickListener {
            listener.onPositiveClick(setHourValue, setMinuteValue)
            dismiss()
        }

        tvCancel?.setOnClickListener {
            listener.onNegativeClick()
            dismiss()
        }
    }
}