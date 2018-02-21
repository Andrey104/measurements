package ru.nextf.measurements

import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val gson = Gson()
val DEAL_KEY = "deal"
val STAGE_KEY = "stage"
val STAGE_COUNT = "stageCount"
val IS_IT_SEARCH = "IS_IT_SEARCH"
val MEASUREMENT_KEY = "measurement"
val MEASUREMENT_EXPANDED = "expanded"
val SYMBOL_KEY = "symbol"
val PROBLEM_KEY = "problem"
val MEASUREMENT_PHOTO = "MEASUREMENT_PHOTO"
val FROM_DEAL = "fromDeal"
val DEAL_ID = "dealId"
val RECALCULATION_NAME = "recalculation"
val COMMENT_DEAL = "commentDeal"
val MOUNT_NAME = "mount"
val MAIN_NAME = "main"
val ID_KEY = "id"
val APP_TOKEN = "token"
val STATUS_CURRENT = 0
val STATUS_REJECT = 1
val STATUS_CLOSE = 2
val CHECK = "check"
val APP_LIST_TODAY_CURRENT = "listTodayCurrent"
val APP_LIST_TOMORROW_CURRENT = "listTomorrowCurrent"
val APP_LIST_TODAY_CLOSED = "listTodayClosed"
val APP_LIST_TOMORROW_CLOSED = "listTomorrowClosed"
val APP_LIST_TODAY_REJECTED = "listTodayRejected"
val APP_LIST_TOMORROW_REJECTED = "listTomorrowRejected"
val APP_USER_INFO = "userInfo"
val BASE_URL = "http://188.225.46.31/"
var myWebSocket = MyWebSocket()

fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    return String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}

fun getTomorrowDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    return String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}

fun Fragment.toast(s: String) {
    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(@StringRes r: Int) {
    Toast.makeText(activity, r, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.toast(s: String) {
    Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.toast(@StringRes r: Int) {
    Toast.makeText(applicationContext, r, Toast.LENGTH_SHORT).show()
}

val backendDateFormat = SimpleDateFormat("yyyy-MM-dd")
private val normalDateFormat = SimpleDateFormat("dd.MM.yyyy")
fun formatDate(date: String): String {
    return try {
        normalDateFormat.format(backendDateFormat.parse(date))
    } catch (e: ParseException) {
        e.printStackTrace()
        "8888.88.88"
    }
}

val backendDateFormatTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
private val normalDateFormatTime = SimpleDateFormat("HH:mm dd.MM.yyyy")
fun formatDateTime(date: String): String {
    return try {
        normalDateFormatTime.format(backendDateFormatTime.parse(date))
    } catch (e: ParseException) {
        e.printStackTrace()
        "8888.88.88"
    }
}