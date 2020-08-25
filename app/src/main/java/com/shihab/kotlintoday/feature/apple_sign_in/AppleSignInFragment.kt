package com.shihab.kotlintoday.feature.apple_sign_in

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.play.core.splitinstall.b
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.FragmentAppleSignInGragmentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppleSignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppleSignInFragment : Fragment() {
    private lateinit var bindingView: FragmentAppleSignInGragmentBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = bindingView.webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.setSupportZoom(true)
        settings.displayZoomControls = false

        bindingView.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                when (url) {

                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                bindingView.webView.visibility = View.VISIBLE
                bindingView.layoutProgress.visibility = View.GONE
                bindingView.progressBar.isIndeterminate = false
                super.onPageFinished(view, url)
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                bindingView.layoutProgress.visibility = View.VISIBLE
                bindingView.progressBar.isIndeterminate = true
                super.onPageStarted(view, url, favicon)
            }
        }

        if (isOnline()) {
            bindingView.webView.loadUrl("https://appleid.apple.com/auth/authorize?client_id&redirect_uri&response_type=id_token&scope=name%20email&response_mode=form_post&state")
        } else {
            val summary =
                "<html><body><font color='red'>No Internet Connection</font></body></html>"
            bindingView.webView.loadData(summary, "text/html", null)
            Toast.makeText(context, "No Internet Connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isOnline(): Boolean {
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingView = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_apple_sign_in_gragment,
            container,
            false
        )
        // Inflate the layout for this fragment
        return bindingView.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppleSignInGragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppleSignInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}