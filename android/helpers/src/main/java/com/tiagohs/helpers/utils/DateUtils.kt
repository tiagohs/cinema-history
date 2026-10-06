package com.tiagohs.helpers.utils

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val c = Calendar.getInstance()

    fun getYearByDate(dateString: String?): Int {
        val date: Date?

        try {
            date = formatter.parse(dateString)
        } catch (e: ParseException) {
            return 0
        }

        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    fun getDateDayWeek(dayWeek: Int): String {
        c.set(Calendar.DAY_OF_WEEK, dayWeek)
        return formatter.format(c.time)
    }

    fun getDateToday(): String {
        val c = Calendar.getInstance()
        return formatter.format(c.time)
    }

    fun getCurrentYear(): String {
        val c = Calendar.getInstance()
        return c.get(Calendar.YEAR).toString()
    }

    fun getDateBefore(numDays: Int): String {
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, -numDays)
        return formatter.format(c.time)
    }

    fun getDateAfter(numDays: Int): String {
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, numDays)
        return formatter.format(c.time)
    }

    fun formateDate(dateString: String?): String? {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date?

        try {
            date = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            return dateString
        }

        // Ordem dia/mês conforme o idioma (pt/es: dd/MM/yyyy, en: MM/dd/yyyy).
        dateFormat = localizedFormat("ddMMyyyy")

        return dateFormat.format(date)
    }

    /** Data por extenso no idioma atual (ex.: "3 de maio de 1950" / "May 3, 1950"). */
    fun formateDateLong(dateString: String): String {
        val date: Date = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString) ?: return dateString
        } catch (e: ParseException) {
            return dateString
        }

        return localizedFormat("MMMMdyyyy").format(date)
    }

    private fun localizedFormat(skeleton: String): SimpleDateFormat {
        val locale = Locale.getDefault()
        return SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, skeleton), locale)
    }

    fun formateDate(format: String, dateString: String): String {
        val customDate = SimpleDateFormat(dateString, Locale.US)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date?

        try {
            date = sdf.parse(format)
        } catch (e: ParseException) {
            return dateString
        }

        return customDate.format(date)
    }

    fun formateStringToCalendar(dateString: String): Calendar {
        val cal = Calendar.getInstance()

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            cal.time = sdf.parse(dateString)
        } catch (e: ParseException) {
            return Calendar.getInstance()
        }

        return cal
    }

}