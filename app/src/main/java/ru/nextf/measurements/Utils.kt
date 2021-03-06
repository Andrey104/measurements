package ru.nextf.measurements

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import ru.nextf.measurements.fragments.CommentsMountFragment
import ru.nextf.measurements.fragments.InstallersMountFragment
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
val MOUNT_KEY = "MOUNT_KEY"
val RECALCULATION_NAME = "recalculation"
val COMMENT_DEAL = "commentDeal"
val MOUNT_NAME = "mount"
val MAIN_NAME = "main"
val ID_KEY = "id"
val DEAL_STATUS = "DEAL_STATUS"
val EDIT_MOUNT = "EDIT_MOUNT"
val EDIT_MOUNT_DATE = "EDIT_MOUNT_DATE"
val EDIT_MOUNT_DESCRIPTION = "EDIT_MOUNT_DESCRIPTION"
val APP_TOKEN = "token"
val MY_ID_USER = "MY_ID_USER"
val PHOTO_ID_DELETE = "PHOTO_ID_DELETE"
val MEASUREMENT_ID_DELETE = "MEASUREMENT_ID_DELETE"
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
val BASE_URL = "http://natcom-crm.nextf.ru/"
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
fun formatDate(date: String): String {
    return try {
        normalDateFormat.format(backendDateFormat.parse(date))
    } catch (e: ParseException) {
        e.printStackTrace()
        "8888.88.88"
    }
}

fun formatDateMount(date: String): String {
    var months: Array<String> = MyApp.instance.resources.getStringArray(ru.nextf.measurements.R.array.months)
    val month = SimpleDateFormat("MM", Locale.US)
    var m = ""
    try {
        m = month.format(backendDateFormat.parse(date))
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val normalFormat = SimpleDateFormat("dd ${months[m.toInt() - 1]} yyyy года", Locale.US)
    return try {
        normalFormat.format(backendDateFormat.parse(date))
    } catch (e: ParseException) {
        e.printStackTrace()
        "8888.88.88"
    }
}

val ONE_MINUTE_IN_MILLIS = 60000
val backendDateFormatTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
val abackendDateFormatTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
private val normalFormatTime = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US)
fun formatTime(date: String): String {
    return try {
        val hoursZ = localTime.substring(0, 3).toInt()
        val minutesZ = (localTime.substring(0, 1) + localTime.substring(3, localTime.length)).toInt() + hoursZ * 60
        val date1 = backendDateFormatTime.parse(date)
        val t = date1.time
        val afterAdding = Date(t + (minutesZ * ONE_MINUTE_IN_MILLIS))
        normalFormatTime.format(afterAdding)
    } catch (e: ParseException) {
        e.printStackTrace()
        try {
            val hoursZ = localTime.substring(0, 3).toInt()
            val minutesZ = (localTime.substring(0, 1) + localTime.substring(3, localTime.length)).toInt() + hoursZ * 60
            val date1 = abackendDateFormatTime.parse(date)
            val t = date1.time
            val afterAdding = Date(t + (minutesZ * ONE_MINUTE_IN_MILLIS))
            normalFormatTime.format(afterAdding)
        } catch (e: ParseException) {
            "8888.88.88"
        }
    }
}

val normalDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
fun formatDateTime(date: String): String {
    val date = try {
        formatTime(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        "8888.88.88"
    }
    println(date + "-------date")
    return date
}

val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
val currentLocalTime = calendar.time
val dateZ = SimpleDateFormat("Z")
val localTime = dateZ.format(currentLocalTime)

fun newInstanceComment(mountString: String): CommentsMountFragment {
    val args = Bundle()
    args.putString(MOUNT_KEY, mountString)
    val fragment = CommentsMountFragment()
    fragment.arguments = args
    return fragment
}

fun newInstanceMount(mountString: String): InstallersMountFragment {
    val args = Bundle()
    args.putString(MOUNT_KEY, mountString)
    val fragment = InstallersMountFragment()
    fragment.arguments = args
    return fragment
}