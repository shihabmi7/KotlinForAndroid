package com.shihab.kotlintoday.utility;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApplicationData {


    public static final String USER_NAME = "USER_NAME";
    public static final String USER_DOB = "USER_DOB";
    public static final String USER_DISTRICT = "USER_DISTRICT";
    public static final String USER_ID = "USER_ID";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";     //public static final String USER_REGISTERED_PHONE_NUMBER = "USER_REGISTERED_PHONE_NUMBER";

    public static final String BLOOD_GROUP = "BLOOD_GROUP";
    public static final String USER_REGISTERED_PHONE_NUMBER = "USER_REGISTERED_PHONE_NUMBER";
    public static final String EMAIL = "EMAIL";
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final String FIREBASE_TOKEN = "FIRE_BASE_TOKEN";
    public static final String CONTACT_UPDATED_AT = "CONTACT_UPDATED_AT";
    public static final String FIRST_INSTALL_DONE = "FIRST_INSTALL_DONE";
    public static final String SELECTED_BLOOD_GROUP = "selected_blood_group";

    public static final String SETTINGS_TYPE_NOTIFICATION = "notification";
    public static final String SETTINGS_NOTIFICATION_YES = "yes";
    public static final String SETTINGS_NOTIFICATION_NO = "no";


    public static final String SETTINGS_TYPE_VISIBILITY = "visibility";
    public static final String SETTINGS_VISIBILITY_STATUS_PUBLIC = "public";
    public static final String SETTINGS_VISIBILITY_STATUS_PRIVATE = "private";


    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int LOGIN_FAILED_RESPONSE_CODE = 450;
    public static final int VERIFICATION_REQUIRED_RESPONSE_CODE = 430;
    public static final int WRONG_VERIFICATION_RESPONSE_CODE = 440;
    public static final int INVALID_LOGIN_ID = 460;

    public static final int ORDER_NUMBER_LENGTH = 15;
    /* DCC client: Branch */
    public static final int BRANCH_ID = 19;

    public static final int TRACK_ALERT_SERVER_PROBLEM = 100;
    public static final int TRACK_ALERT_VERIFICATION_REQUIRED = 200;
    public static final int TRACK_ALERT_WRONG_VERIFICATION = 300;
    public static final int TRACK_ALERT_CORRECT_VERIFICATION = 400;
    public static final int TRACK_ALERT_ORDER_FAILED = 500;
    public static final int TRACK_ALERT_DELETE_ITEM = 600;
    public static final int TRACK_ALERT_LOGIN_REQUIRED = 700;
    public static final int TRACK_ALERT_FAVOURITE_PRODUCT_DELETE_ = 800;

    public static final String CURRENCY = "TAKA";
    public static final String CURRENCY_SYMBOL = "৳";

    public static final boolean IS_LOCAL_HOST = false;
    //    public static final String BASE_BLOOD_SHARE_API_URL = "http://159.65.156.116/fb_order_api/";
    /* todo : need to check here  : API end points */
    public static final String BASE_BLOOD_SHARE_API_URL = "http://bloodshare.com.bd/admin/";
    public static final String BASE_BLOOD_SHARE_PHOTO_URL = "http://bloodshare.com.bd/admin/uploads/profile/";


    public static final String FACEBOOK_FINAL_URL = "https://graph.accountkit.com";
    public static final String LATITUDE = "23.8103";
    public static final String LONGITUDE = "90.4125";
//    public static final String USER_VISIBILITY_PUBLIC = "PUBLIC";
//    public static final String USER_VISIBILITY_PRIVATE = "PRIVATE";
    public static final String FCM_DEFAULT_CHANNEL_ID = "12345";
    public static final String USER_PHOTO_NAME = "USER_PHOTO_NAME";

    public static String APP_NAME = "Frame Big Blood Bank";

    public static String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.framebig.bloodshare";
    public static String SHARE_BODY = "Share blood on trusted network! Download From Google Play \n" + PLAYSTORE_URL;

    //App Secret test
    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        LogMe.d("st::", digest);
        return digest;
    }

    public static boolean getOneDayDifference(long oldDate) {

        //https://stackoverflow.com/questions/10690370/how-do-i-get-difference-between-two-dates-in-android-tried-every-thing-and-pos
        long currentDate = new Date().getTime();

        long diff = currentDate - oldDate;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 1) {

            Log.e("oldDate", "is previous date");
            Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                    + " hours: " + hours + " days: " + days);

            return true;

        }
        return false;
    }


}
