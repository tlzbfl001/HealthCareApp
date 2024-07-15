package kr.bodywell.health.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

class CalendarUtil {
   companion object {
      var selectedDate: LocalDate = LocalDate.now()

      fun dateFormat(date: LocalDate?): String? {
         val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
         return date?.format(formatter)
      }

      fun weekFormat(date: LocalDate?): String {
         val calendar = Calendar.getInstance()
         val split = date.toString().split("-")
         calendar.set(split[0].toInt(), split[1].toInt()-1, split[2].toInt())
         return "${split[0]}년 ${split[1]}월 ${calendar.get(Calendar.WEEK_OF_MONTH)}주차"
      }

      fun monthFormat(date: LocalDate?): String {
         val split = date.toString().split("-")
         return "${split[0]}년 ${split[1]}월"
      }

      fun monthArray(): ArrayList<LocalDate?> {
         val days = ArrayList<LocalDate?>()
         val yearMonth = YearMonth.from(selectedDate)
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

      fun monthArray2(date: LocalDate?): ArrayList<LocalDate?> {
         val days = ArrayList<LocalDate?>()
         val yearMonth = YearMonth.from(date)
         val daysInMonth = yearMonth.lengthOfMonth()
         var firstOfMonth = date?.withDayOfMonth(1)

         for (i in 0 until daysInMonth) {
            days.add(firstOfMonth)
            firstOfMonth = firstOfMonth?.plusDays(1)
         }

         return days
      }

      fun weekArray(date: LocalDate): ArrayList<LocalDate?> {
         val days = ArrayList<LocalDate?>()
         var sunday = sundayForDate(date)
         val endDate = sunday!!.plusWeeks(1)
         while (sunday!!.isBefore(endDate)) {
            days.add(sunday)
            sunday = sunday.plusDays(1)
         }
         return days
      }

      private fun sundayForDate(current: LocalDate): LocalDate? {
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
