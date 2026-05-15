package com.example.karunadavanyaa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val LOCATION_PERMISSION_CODE = 1001

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        setupWebView()
        requestLocationPermission()

        // Load the Karunada-Vanya HTML app from assets folder
        webView.loadUrl("file:///android_asset/index.html")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings: WebSettings = webView.settings

        // JavaScript must be enabled — app uses React + Firebase
        settings.javaScriptEnabled = true

        // Firebase and React need DOM storage
        settings.domStorageEnabled = true

        // Allow reading local files from assets
        settings.allowFileAccess = true
        settings.allowContentAccess = true

        // Required for ES module imports from CDN (gstatic, googleapis)
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Viewport — prevent tiny text on high-res screens
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // Disable zoom — app handles its own layout
        settings.setSupportZoom(false)
        settings.displayZoomControls = false
        settings.builtInZoomControls = false

        // Enable geolocation for GPS wildlife reporting
        settings.setGeolocationEnabled(true)

        // Cache policy
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Hardware acceleration for smooth animations
        webView.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

        // Handle navigation — keep Firebase/CDN URLs loading inside WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false

                // These must load inside WebView (Firebase, CDN, local files)
                val internalHosts = listOf(
                    "file://",
                    "googleapis.com",
                    "gstatic.com",
                    "firebaseapp.com",
                    "firebaseio.com",
                    "google.com",
                    "fonts.googleapis.com",
                    "cdnjs.cloudflare.com"
                )

                val isInternal = internalHosts.any { url.contains(it) }

                return if (isInternal) {
                    false // Load inside WebView
                } else {
                    // Open external URLs (tel:, mailto:, maps, etc.) in system app
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        // Ignore if no app can handle it
                    }
                    true
                }
            }
        }

        // Handle browser features: geolocation prompt
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                // Grant geolocation to the local HTML file
                callback?.invoke(origin, true, false)
            }
        }
    }

    private fun requestLocationPermission() {
        val fineLocation = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocation = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsNeeded = mutableListOf<String>()
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    // Handle Android back button — go back in WebView history if possible
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    // Pause/resume WebView with Activity lifecycle
    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
