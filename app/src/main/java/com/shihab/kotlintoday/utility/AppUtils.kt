package com.shihab.kotlintoday.utility

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Shihab on 10/9/17.
 */
object AppUtils {

    const val ANALYTICS_KEY = "KotlinTodayAnalytics"

    fun generateRandomID(): String {
        return UUID.randomUUID().toString()
    }

    fun randInt(min: Int, max: Int, length: Int): String {
        var randomNumber = ""
        val rand = Random()
        for (i in 0 until length) {
            randomNumber += (rand.nextInt(max - min + 1) + min).toString()
        }
        var random = randomNumber.toLong()
        //Add unix time to make random number more unique
        val nanoTIme = System.nanoTime()
        random = random + nanoTIme
        randomNumber = java.lang.Long.toString(random)
        //After adding UNIX time with random number it might be more than the desired length. So
        //it should be checked as follows:
        randomNumber =
            if (randomNumber.length > length) randomNumber.substring(0, length) else randomNumber

        //if new random number is greater than the desire length then make recursive call
        return if (randomNumber.length < length) {
            randInt(0, 9, length)
        } else randomNumber
    }

    /**
     * Convert from yyyy-MM-dd to dd/MM/yyyy
     *
     * @param date
     * @return String
     */
    fun formatDate(date: String?): String {
        val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        var parsed: Date? = null
        try {
            parsed = parseFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return targetFormat.format(parsed).toString()
    }

    /*public static void customDialogOneButton(
            Activity activity,
            Object object,
            String title,
            String message,
            int checkboxVisibility,
            String buttonText,
            int icon,
            int trackingNumber)
    {

        android.app.FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        android.app.Fragment prev = activity.getFragmentManager().findFragmentByTag("custom_dialog_button1");

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        AlertDialogOneButton newFragment = AlertDialogOneButton
                .newInstance(object, title, message, buttonText, checkboxVisibility, icon, trackingNumber);
        newFragment.show(ft, "custom_dialog_button1");//.show(ft, "custom_dialog_button1");

    }*/
    /*public static void customDialogTwoButtons(
            Activity activity,
            Object object,
            String title,
            String message,
            int checkboxVisibility,
            String negativeButtonText,
            String positiveButtonText,
            int icon,
            int trackingNumber)
    {

        android.app.FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        android.app.Fragment prev = activity.getFragmentManager().findFragmentByTag("custom_dialog_button2");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        AlertDialogTwoButton newFragment = AlertDialogTwoButton
                .newInstance(object, title, message, negativeButtonText, positiveButtonText,checkboxVisibility, icon, trackingNumber);
        newFragment.show(ft, "custom_dialog_button2");

    }*/
    fun showAlertForInternet(activity: Activity?) {
        val builder1: AlertDialog.Builder
        builder1 = AlertDialog.Builder(activity!!)
        builder1.setMessage("Please check your internet connection")
        builder1.setCancelable(false)
        builder1.setTitle("Network Error!")
        //        builder1.setIcon()
        builder1.setPositiveButton(
            "Ok"
        ) { dialog, id -> dialog.dismiss() }
        //        builder1.setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        ((Activity) LogInActivity.this).finish();
//                    }
//                });
        val alert11 = builder1.create()
        alert11.show()
    }

    const val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    /* From Activity
     */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun validateEmail(email: String?): Boolean {
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun goToForgotPassActivity(activity: Activity?) {}
    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }

    fun playSound(context: Context?, sound: Int) {
        val mp: MediaPlayer
        mp = MediaPlayer.create(context, sound)
        mp.start()
    }
}