package kr.bodywell.health.view.home.medicine

interface TimePickerClickListener {
    fun onPositiveClick(hour: Int, minute: Int)
    fun onNegativeClick()
}