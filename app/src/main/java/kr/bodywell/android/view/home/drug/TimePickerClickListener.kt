package kr.bodywell.android.view.home.drug

interface TimePickerClickListener {
    fun onPositiveClick(hour: Int, minute: Int)
    fun onNegativeClick()
}