package com.makebodywell.bodywell.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class CalendarUtil {
   companion object {
      var isItemClick = false
      var selectedDate: LocalDate? = null
      var selectedDate1: LocalDate? = null
      var selectedDate2: LocalDate? = null
      var selectedDays: ArrayList<LocalDate> = ArrayList<LocalDate>()
      var deleteList: ArrayList<Int> = ArrayList<Int>()

      fun dateTitle(date: LocalDate): String? {
         val formatter = DateTimeFormatter.ofPattern("yyyy  MMMM")
         return date.format(formatter)
      }

      fun monthArray(date: LocalDate?): ArrayList<LocalDate?> {
         val days = ArrayList<LocalDate?>()
         val yearMonth = YearMonth.from(date)
         val daysInMonth = yearMonth.lengthOfMonth()
         val firstOfMonth = selectedDate!!.withDayOfMonth(1)
         var dayOfWeek = firstOfMonth.dayOfWeek.value
         if (dayOfWeek == 7) {
            dayOfWeek = 0
         }
         val lastDate = firstOfMonth.minusMonths(1)
         val lastOfMonth = lastDate.with(TemporalAdjusters.lastDayOfMonth())
         for (i in 1..42) {
            if (i <= dayOfWeek) {
               days.add(lastOfMonth.minusDays((dayOfWeek - i).toLong()))
            } else if (i > daysInMonth + dayOfWeek) {
               val num = i - (daysInMonth + dayOfWeek + 1)
               days.add(firstOfMonth.plusMonths(1).plusDays(num.toLong()))
            } else {
               days.add(LocalDate.of(selectedDate!!.year, selectedDate!!.month, i - dayOfWeek))
            }
         }
         return days
      }

      fun weekArray(selectedDate: LocalDate?): ArrayList<LocalDate?> {
         val days = ArrayList<LocalDate?>()
         var sunday = sundayForDate(selectedDate!!)
         val endDate = sunday!!.plusWeeks(1)
         while (sunday!!.isBefore(endDate)) {
            days.add(sunday)
            sunday = sunday.plusDays(1)
         }
         return days
      }

      private fun sundayForDate(current: LocalDate): LocalDate? {
         var current = current
         val oneWeekAgo = current.minusWeeks(1)
         while (current.isAfter(oneWeekAgo)) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) {
               return current
            }
            current = current.minusDays(1)
         }
         return null
      }
   }
}