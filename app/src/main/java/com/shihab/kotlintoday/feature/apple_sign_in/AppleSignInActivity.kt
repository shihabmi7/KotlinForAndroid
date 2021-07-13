package com.shihab.kotlintoday.feature.apple_sign_in

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityAppleSignInBinding
import com.shihab.kotlintoday.utility.LogMe
import java.util.*

class AppleSignInActivity : AppCompatActivity() {

    private val APPLE_TOKEN_KEY: String ="appleToken"
    lateinit var binding: ActivityAppleSignInBinding
    lateinit var appleAuthURLFull: String
    lateinit var appledialog: Dialog
    private var webViewBundle: Bundle? = null

    private var appleAccessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_apple_sign_in)
        setSupportActionBar(binding.toolbar)

        val state = UUID.randomUUID().toString()

        appleAuthURLFull =
            AppleConstants.AUTHURL + "?response_type=" + AppleConstants.RESPONSE_TYPE + "&response_mode=" + AppleConstants.RESPONSE_MODE + "&client_id=" + AppleConstants.CLIENT_ID + "&scope=" + AppleConstants.SCOPE + "&state=" + state + "&redirect_uri=" + AppleConstants.BEFORE_REDIRECT_URI

        LogMe.i("url",appleAuthURLFull)
        binding.content.relativeLayout.setOnClickListener {
            setupAppleWebviewDialog(appleAuthURLFull)
        }
    }

    private fun setupAppleWebviewDialog(url: String) {
        appledialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        appledialog.setContentView(R.layout.fragment_apple_sign_in)
        val layout = appledialog.findViewById<LinearLayout>(R.id.layoutProgress)
        val progressBar = appledialog.findViewById<ProgressBar>(R.id.progressBar)
        val webView = appledialog.findViewById<WebView>(R.id.webView)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.setSupportZoom(true)
        settings.displayZoomControls = false


        webView.settings.userAgentString = "Android_Tbbd"
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.domStorageEnabled = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("appleToken=",false)){
                    LogMe.i("ri-url",url)
                    handleURL(url)
                    appledialog.dismiss()

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
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                return true
            }
        }

        webView.setOnKeyListener( View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val webView = v as WebView

                when (keyCode) {
                    KeyEvent.KEYCODE_BACK -> if (webView.canGoBack()) {
                        webView.goBack()
                        return@OnKeyListener true
                    }
                }
            }
            false
        })

        if (isOnline()) {
            if (webViewBundle == null) {
                webView.loadUrl(url)
            } else {
                webView.restoreState(webViewBundle!!)
            }
        } else {
            val summary = "<html><body><font color='red'>No Internet Connection</font></body></html>"
            webView.loadData(summary, "text/html", null)
            Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show()
        }

        appledialog.show()
    }

    private fun handleURL(url: String) {
        val uri = Uri.parse(url)

        appleAccessToken = uri.getQueryParameter(APPLE_TOKEN_KEY)!!
        LogMe.i("appleAccessToken",appleAccessToken)
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}