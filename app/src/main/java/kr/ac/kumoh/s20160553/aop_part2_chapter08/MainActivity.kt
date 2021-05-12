package kr.ac.kumoh.s20160553.aop_part2_chapter08

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val progressBar: ContentLoadingProgressBar by lazy{
        findViewById(R.id.progressBar)
    }

    private val goHomeButton: ImageButton by lazy{
        findViewById(R.id.goHomeButton)
    }

    private val goForwardButton: ImageButton by lazy{
        findViewById(R.id.goForwardButton)
    }

    private val goBackButton: ImageButton by lazy{
        findViewById(R.id.goBackButton)
    }

    private val addressBar: EditText by lazy{
        findViewById(R.id.addressBar)
    }

    private val webView: WebView by lazy{
        findViewById(R.id.webView)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy{
        findViewById(R.id.refreshLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")

    private fun initViews() {
        webView.apply{
            //웹 클라이언트를 구글이나 삼성인터넷 대신 내가 만든걸 사용
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            //자바스크립트 동작 허용
            settings.javaScriptEnabled = true
            //최초 실행시 구글(홈페이지 지정)
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindViews() {
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                } else {
                    webView.loadUrl("http://$loadingUrl")
                }
                webView.loadUrl(v.text.toString())
            }

            return@setOnEditorActionListener false
        }

        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient () {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object{
        private const val DEFAULT_URL = "https://www.google.com"
    }
}