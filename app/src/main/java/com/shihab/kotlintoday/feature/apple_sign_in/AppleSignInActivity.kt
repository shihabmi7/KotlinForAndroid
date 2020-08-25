package com.shihab.kotlintoday.feature.apple_sign_in

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityAppleSignInBinding
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class AppleSignInActivity : AppCompatActivity() {

    lateinit var binding: ActivityAppleSignInBinding
    lateinit var appleAuthURLFull: String
    lateinit var appledialog: Dialog
    lateinit var appleAuthCode: String
    lateinit var appleClientSecret: String

    var appleId = ""
    var appleFirstName = ""
    var appleMiddleName = ""
    var appleLastName = ""
    var appleEmail = ""
    var appleAccessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_apple_sign_in)
        setSupportActionBar(binding.toolbar)

        val state = UUID.randomUUID().toString()

        appleAuthURLFull =
            AppleConstants.AUTHURL + "?response_type=" + AppleConstants.RESPONSE_TYPE + "&response_mode=" + AppleConstants.RESPONSE_MODE + "&client_id=" + AppleConstants.CLIENT_ID + "&scope=" + AppleConstants.SCOPE + "&state=" + state + "&redirect_uri=" + AppleConstants.REDIRECT_URI

        LogMe.i("url",appleAuthURLFull)
        binding.content.relativeLayout.setOnClickListener {
            setupAppleWebviewDialog(appleAuthURLFull)
        }
    }

    private fun setupAppleWebviewDialog(url: String) {
        appledialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        appledialog.setContentView(R.layout.fragment_payment_method)
        val layout = appledialog.findViewById<LinearLayout>(R.id.layoutProgress)
        val progressBar = appledialog.findViewById<ProgressBar>(R.id.progressBar)
        val webView = appledialog.findViewById<WebView>(R.id.webView)
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = object : WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request!!.url.toString().startsWith(AppleConstants.REDIRECT_URI)) {
                    handleUrl(request.url.toString())
                    // Close the dialog after getting the authorization code
                    if (request.url.toString().contains("success=")) {
                        appledialog.dismiss()
                    }
                    return true
                }
                return true
            }

            // For API 19 and below
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(AppleConstants.REDIRECT_URI)) {
                    handleUrl(url)
                    // Close the dialog after getting the authorization code
                    if (url.contains("success=")) {
                        appledialog.dismiss()
                    }
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                webView.visibility = View.VISIBLE
                layout.visibility = View.GONE
                progressBar.isIndeterminate = false
                super.onPageFinished(view, url)
            }

            // Check webview url for access token code or error
            private fun handleUrl(url: String) {
                val uri = Uri.parse(url)

                val success = uri.getQueryParameter("success")
                if (success == "true") {

                    // Get the Authorization Code from the URL
                    appleAuthCode = uri.getQueryParameter("code") ?: ""
                    Log.i("Apple Code: ", appleAuthCode)

                    // Get the Client Secret from the URL
                    appleClientSecret = uri.getQueryParameter("client_secret") ?: ""
                    Log.i("Apple Client Secret: ", appleClientSecret)
                    // Save the Client Secret (appleClientSecret) using SharedPreferences
                    // This will allow us to verify if refresh Token is valid every time they open the app after cold start.
                    val sharedPref = getPreferences(Context.MODE_PRIVATE)
                    sharedPref.edit().putString("client_secret", appleClientSecret).apply()

                    //Check if user gave access to the app for the first time by checking if the url contains their email
                    if (url.contains("email")) {
                        //Get user's First Name
                        val firstName = uri.getQueryParameter("first_name")
                        Log.i("Apple User First Name: ", firstName ?: "")
                        appleFirstName = firstName ?: "Not exists"

                        //Get user's Middle Name
                        val middleName = uri.getQueryParameter("middle_name")
                        Log.i("Middle Name: ", middleName ?: "")
                        appleMiddleName = middleName ?: "Not exists"

                        //Get user's Last Name
                        val lastName = uri.getQueryParameter("last_name")
                        Log.i("Apple User Last Name: ", lastName ?: "")
                        appleLastName = lastName ?: "Not exists"

                        //Get user's email
                        val email = uri.getQueryParameter("email")
                        Log.i("Apple User Email: ", email ?: "Not exists")
                        appleEmail = email ?: ""
                    }

                    // Exchange the Auth Code for Access Token
                    requestForAccessToken(appleAuthCode, appleClientSecret)
                } else if (success == "false") {
                    Log.e("ERROR", "We couldn't get the Auth Code")
                }
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        appledialog.show()
    }

    /*@Suppress("OverridingDeprecatedMember")
    inner class AppleWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request!!.url.toString().startsWith(AppleConstants.REDIRECT_URI)) {
                handleUrl(request.url.toString())
                // Close the dialog after getting the authorization code
                if (request.url.toString().contains("success=")) {
                    appledialog.dismiss()
                }
                return true
            }
            return true
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(AppleConstants.REDIRECT_URI)) {
                handleUrl(url)
                // Close the dialog after getting the authorization code
                if (url.contains("success=")) {
                    appledialog.dismiss()
                }
                return true
            }
            return false
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // retrieve display dimensions
            val displayRectangle = Rect()
            val window = this@AppleSignInActivity.window
            window.decorView.getWindowVisibleDisplayFrame(displayRectangle)

            // Set height of the Dialog to 90% of the screen
            val layoutparms = view?.layoutParams
            layoutparms?.height = (displayRectangle.height() * 0.9f).toInt()
            view?.layoutParams = layoutparms
        }

        // Check webview url for access token code or error
        @SuppressLint("LongLogTag")
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)

            val success = uri.getQueryParameter("success")
            if (success == "true") {

                // Get the Authorization Code from the URL
                appleAuthCode = uri.getQueryParameter("code") ?: ""
                Log.i("Apple Code: ", appleAuthCode)

                // Get the Client Secret from the URL
                appleClientSecret = uri.getQueryParameter("client_secret") ?: ""
                Log.i("Apple Client Secret: ", appleClientSecret)
                // Save the Client Secret (appleClientSecret) using SharedPreferences
                // This will allow us to verify if refresh Token is valid every time they open the app after cold start.
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                sharedPref.edit().putString("client_secret", appleClientSecret).apply()

                //Check if user gave access to the app for the first time by checking if the url contains their email
                if (url.contains("email")) {
                    //Get user's First Name
                    val firstName = uri.getQueryParameter("first_name")
                    Log.i("Apple User First Name: ", firstName ?: "")
                    appleFirstName = firstName ?: "Not exists"

                    //Get user's Middle Name
                    val middleName = uri.getQueryParameter("middle_name")
                    Log.i("Apple User Middle Name: ", middleName ?: "")
                    appleMiddleName = middleName ?: "Not exists"

                    //Get user's Last Name
                    val lastName = uri.getQueryParameter("last_name")
                    Log.i("Apple User Last Name: ", lastName ?: "")
                    appleLastName = lastName ?: "Not exists"

                    //Get user's email
                    val email = uri.getQueryParameter("email")
                    Log.i("Apple User Email: ", email ?: "Not exists")
                    appleEmail = email ?: ""
                }

                // Exchange the Auth Code for Access Token
                requestForAccessToken(appleAuthCode, appleClientSecret)
            } else if (success == "false") {
                Log.e("ERROR", "We couldn't get the Auth Code")
            }
        }

    }*/

    private fun requestForAccessToken(code: String, clientSecret: String) {
        val grantType = "authorization_code"

        val postParamsForAuth =
            "grant_type=" + grantType + "&code=" + code + "&redirect_uri=" + AppleConstants.REDIRECT_URI + "&client_id=" + AppleConstants.CLIENT_ID + "&client_secret=" + clientSecret


        GlobalScope.launch {
            val url = URL(AppleConstants.TOKENURL)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            withContext(Dispatchers.IO) {
                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                outputStreamWriter.write(postParamsForAuth)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8

            val jsonObject = JSONTokener(response).nextValue() as JSONObject

            val accessToken = jsonObject.getString("access_token") //Here is the access token
            Log.i("Apple Access Token is: ", accessToken)
            appleAccessToken = accessToken

            val expiresIn = jsonObject.getInt("expires_in") //When the access token expires
            Log.i("expires in: ", expiresIn.toString())

            val refreshToken =
                jsonObject.getString("refresh_token") // The refresh token used to regenerate new access tokens. Store this token securely on your server.
            Log.i("refresh token: ", refreshToken)
            // Save the RefreshToken Token (refreshToken) using SharedPreferences
            // This will allow us to verify if refresh Token is valid every time they open the app after cold start.
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().putString("refresh_token", refreshToken ?: "").apply()


            val idToken =
                jsonObject.getString("id_token") // A JSON Web Token that contains the user’s identity information.
            Log.i("ID Token: ", idToken)

            // Get encoded user id by splitting idToken and taking the 2nd piece
            val encodedUserID = idToken.split(".")[1]

            //Decode encodedUserID to JSON
            val decodedUserData = String(Base64.decode(encodedUserID, Base64.DEFAULT))
            val userDataJsonObject = JSONObject(decodedUserData)
            // Get User's ID
            val userId = userDataJsonObject.getString("sub")
            Log.i("Apple User ID :", userId)
            appleId = userId

            withContext(Dispatchers.Main) {
                //
                //openDetailsActivity()
            }
        }
    }

}