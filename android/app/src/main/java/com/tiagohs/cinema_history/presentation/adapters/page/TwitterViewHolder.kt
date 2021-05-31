package com.tiagohs.cinema_history.presentation.adapters.page

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentTwitter
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_page_twitter.*

class TwitterViewHolder(
    val view: View
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val contentTwitter = item as? ContentTwitter ?: return

        handleInWebiew(contentTwitter)

        setupContentFooterInformation(contentTwitter.information)
    }

    private fun handleInWebiew(contentTwitter: ContentTwitter) {
        val value = contentTwitter.twitterHtml

        webView.apply {
            loadingProgress.show()

            visibility = View.INVISIBLE

            webView.webChromeClient = WebChromeClient()

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = true
                defaultTextEncodingName = "UTF-8"

                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                useWideViewPort = false
            }

            isHorizontalScrollBarEnabled = false;
            isVerticalScrollBarEnabled = false;
            isScrollContainer = false;

            setOnTouchListener { _: View?, event: MotionEvent -> event.action == MotionEvent.ACTION_MOVE }

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {

                    Handler().postDelayed({
                        loadingProgress.hide()
                        webView?.visibility = View.VISIBLE
                    }, 3000)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                    if (!url.isNullOrEmpty()) {
                        val uri = Uri.parse(url)
                        val twitter = Intent(Intent.ACTION_VIEW, uri)
                        twitter.setPackage("com.twitter.android")
                        try {
                            context.startActivity(twitter)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                    }
                    return true
                }
            }
        }

        try {
            webView.loadDataWithBaseURL("https://twitter.com", value, "text/html", "utf-8", null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_twitter
    }
}