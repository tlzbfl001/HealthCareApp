package com.makebodywell.bodywell.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class CalendarUtil {
   companion object {
      var isItemClick = false
      var selectedDate: LocalDate = LocalDate.now()
      var deleteList: ArrayList<Int> = ArrayList<Int>()
      var drugSelected1: LocalDate? = null
      var drugSelected2: LocalDate? = null

      fun calendarTitle(date: LocalDate): String? {
         val formatter = DateTimeFormatter.ofPattern("yyyy  MMMM")
         return date.format(formatter)
      }

      fun dateFormat(date: LocalDate?): String? {
         val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
         return date?.format(formatter)
      }

      fun monthArray(date: LocalDate?): ArrayList<LocalDate?> {
         val days = ArrayList<LocalDate?>()
         val yearMonth = YearMonth.from(date)
         val daysInMonth = yearMonth.lengthOfMonth()
         val firstOfMonth = selectedDate.withDayOfMonth(1)
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
               days.add(LocalDate.of(selectedDate.year, selectedDate.month, i - dayOfWeek))
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

      fun sundayForDate(current: LocalDate): LocalDate? {
         var curr = current
         val oneWeekAgo = curr.minusWeeks(1)
         while (curr.isAfter(oneWeekAgo)) {
            if (curr.dayOfWeek == DayOfWeek.SUNDAY) {
               return curr
            }
            curr = curr.minusDays(1)
         }
         return null
      }
   }
}