package com.shihab.kotlintoday.utility

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun Activity.showErrorMessage(context: Context, message: String) {

    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun Activity.showSuccessMessage(context: Context, message: String) {

    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
fun String.isEmailValid(): Boolean {
    val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}
fun String.formatPhoneNumber(context: Context, region: String): String? {
    val phoneNumberKit = PhoneNumberUtil.createInstance(context)
    val number = phoneNumberKit.parse(this, region)
    if (!phoneNumberKit.isValidNumber(number))
        return null

    return phoneNumberKit.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
}
/*val phone = "(202)555-0156" // Phone number is fake, but has valid format
val formattedPhone = phone.formatPhoneNumber(this, "US")
if (formattedPhone == null) {
println("Phone number is not valid")
} else {
println("Sending $formattedPhone to API")
}*/

val String.containsLatinLetter: Boolean
    get() = matches(Regex(".*[A-Za-z].*"))

val String.containsDigit: Boolean
    get() = matches(Regex(".*[0-9].*"))

val String.isAlphanumeric: Boolean
    get() = matches(Regex("[A-Za-z0-9]*"))

val String.hasLettersAndDigits: Boolean
    get() = containsLatinLetter && containsDigit

val String.isIntegerNumber: Boolean
    get() = toIntOrNull() != null

val String.toDecimalNumber: Boolean
    get() = toDoubleOrNull() != null


val cl = "Contains letters".containsLatinLetter // true
val cnl = "12345".containsLatinLetter // false
val cd = "Contains digits 123".containsDigit // true
val istr = "123".isIntegerNumber // true
val dstr = "12.9".toDecimalNumber // true

val String.jsonObject: JSONObject?
    get() = try {
        JSONObject(this)
    } catch (e: JSONException) {
        null
    }

val String.jsonArray: JSONArray?
    get() = try {
        JSONArray(this)
    } catch (e: JSONException) {
        null
    }

val json = "{\"key\": \"value\"}".jsonObject  // {"key": "value"}
val jsonAgain = json?.toString() // "{"key": "value"}"
val stringFromJson = json?.getString("key") // "value"

val String.lastPathComponent: String
    get() {
        var path = this
        if (path.endsWith("/"))
            path = path.substring(0, path.length - 1)
        var index = path.lastIndexOf('/')
        if (index < 0) {
            if (path.endsWith("\\"))
                path = path.substring(0, path.length - 1)
            index = path.lastIndexOf('\\')
            if (index < 0)
                return path
        }
        return path.substring(index + 1)
    }

fun Context?.isOnline(): Boolean {
    this?.apply {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
    return false
}

fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) { }
    return false
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun SpannableString.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): SpannableString {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = android.text.SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = spannable
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
    setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

private fun String.toSpannable() = SpannableStringBuilder(this)

fun String.foregroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = ForegroundColorSpan(color)
    return toSpannable().spanText(span)
}

fun String.backgroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = BackgroundColorSpan(color)
    return toSpannable().spanText(span)
}

fun String.relativeSize(size: Float): SpannableStringBuilder {
    val span = RelativeSizeSpan(size)
    return toSpannable().spanText(span)
}

fun String.supserscript(): SpannableStringBuilder {
    val span = SuperscriptSpan()
    return toSpannable().spanText(span)
}

fun String.strike(): SpannableStringBuilder {
    val span = StrikethroughSpan()
    return toSpannable().spanText(span)
}

fun String.hextoRGB() : Triple<String, String, String>{
    var name = this
    if (!name.startsWith("#")){
        name = "#$this"
    }
    var color = Color.parseColor(name)
    var red = Color.red(color)
    var green = Color.green(color)
    var blue = Color.blue(color)

    return Triple(red.toString(), green.toString(), blue.toString())
}

fun Int.colorToHexString(): String? {
    var data = String.format("#%06X", -0x1 and this).replace("#FF","#")
    return data
}