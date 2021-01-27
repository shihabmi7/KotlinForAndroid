package com.shihab.kotlintoday.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shihab on 10/9/17.
 */

public class AppUtils {

    public static String generateRandomID() {
        return UUID.randomUUID().toString();
    }

    public static String sha256Hex(String stringValue) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update(stringValue.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static String randInt(int min, int max, int length) {

        String randomNumber = "";
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            randomNumber = randomNumber.concat(Integer.toString(rand.nextInt((max - min) + 1) + min));

        }
        long random = Long.parseLong(randomNumber);
        //Add unix time to make random number more unique
        long nanoTIme = System.nanoTime();

        random = random + nanoTIme;
        randomNumber = Long.toString(random);
        //After adding UNIX time with random number it might be more than the desired length. So
        //it should be checked as follows:
        randomNumber = (randomNumber.length() > length ? randomNumber.substring(0, length) : randomNumber);

        //if new random number is greater than the desire length then make recursive call
        if (randomNumber.length() < length) {
            return randInt(0, 9, length);
        }


        return randomNumber;
    }

    /**
     * Convert from yyyy-MM-dd to dd/MM/yyyy
     *
     * @param date
     * @return String
     */
    public static String formatDate(String date) {
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date parsed = null;
        try {
            parsed = parseFormat.parse(date);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return targetFormat.format(parsed).toString();
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


    public static void showAlertForInternet(Activity activity) {


        AlertDialog.Builder builder1;
        builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage("Please check your internet connection");
        builder1.setCancelable(false);
        builder1.setTitle("Network Error!");
//        builder1.setIcon()
        builder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
//        builder1.setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        ((Activity) LogInActivity.this).finish();
//                    }
//                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /* From Activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void goToForgotPassActivity(Activity activity) {

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    public static void playSound(Context context, int sound) {
        MediaPlayer mp;
        mp = MediaPlayer.create(context, sound);
        mp.start();

    }


}
