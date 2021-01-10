package br.com.navegadormobile

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.widget.Toolbar.*
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    var mainLayout: View? = null
    var toolbar: Toolbar? = null
    var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainLayout = layoutInflater.inflate(R.layout.activity_main, null)
        setContentView(mainLayout)

        title = ""

        toolbar = this.mainLayout?.toolbar
        setSupportActionBar(toolbar)

        setUrl("https://www.google.com/")
    }

    @SuppressLint("SetJavaScriptEnable")
    private fun setUrl(url: String) {
        var webUrl = url
        if(!webUrl.startsWith("http")){
            webUrl = "http://$url"
        }

        webView = findViewById<WebView>(R.id.webBrowser)
        webView!!.getSettings().javaScriptEnabled = true
        webView!!.getSettings().javaScriptCanOpenWindowsAutomatically = true
        webView!!.webViewClient = object : WebViewClient(){}

        webView!!.loadUrl(webUrl)
        mainLayout?.webUrl!!.setText(webUrl)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        when (id){
            R.id.actionGo -> navigation()
            R.id.actionHome -> home()
            R.id.actionRefresh -> reload()
            R.id.actionSettings -> config()
            else -> showToast(getString(R.string.no_click))
        }
        return super.onOptionsItemSelected(item)
    }




    private fun home() {

    }

    private fun reload() {
        val webUrlString = webView!!.url

        if(webUrlString.isNullOrEmpty()){
            if (!mainLayout?.webUrl!!.getText().trim().isNullOrEmpty()) {
                setUrl(mainLayout?.webUrl!!.text.toString())
            }
        }else{
            setUrl(webUrlString)
        }
    }

    private fun navigation() {
        if(mainLayout?.webUrl!!.text.trim().isNullOrEmpty()){
            showToast(getString(R.string.no_url))
        }else{
            setUrl(mainLayout?.webUrl!!.text.toString())
        }

    }


    private fun config() {

        

    }

    private fun showToast(message: String) {
        Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    //Função de botão para volta
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if(event.getAction() === KeyEvent.ACTION_DOWN){
            when(keyCode){
                KeyEvent.KEYCODE_BACK->{
                    if(webView!!.canGoBack()){
                        webView!!.goBack()
                    }else{
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}