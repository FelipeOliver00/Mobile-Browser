package br.com.navegadormobile

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.settings_dialog.view.*

class MainActivity : AppCompatActivity() {

    var mainLayout: View? = null
    var toolbar: Toolbar? = null
    var webView: WebView? = null

    val SHAREDPREF_FILENAME ="br.com.navegadormobile.web_prefs"
    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainLayout = layoutInflater.inflate(R.layout.activity_main, null)
        setContentView(mainLayout)

        title = ""

        sharedPref = this.getSharedPreferences(SHAREDPREF_FILENAME, 0)

        toolbar = this.mainLayout?.toolbar
        setSupportActionBar(toolbar)

        var LoadLastViewed : Boolean = false
        LoadLastViewed = sharedPref!!.getBoolean("LoadLastURL", false)

        if(LoadLastViewed){

        }else{
            setUrl(sharedPref!!.getString("HomePageURL","https://google.com")!!)
        }

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

        //A última página visitada sendo trocada aqui para salvar edit e putString
        sharedPref?.edit()?.putString("LastVisitedURL", webUrl)?.apply()
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

        setUrl(sharedPref!!.getString("HomePageURL", "https://google.com")!!)

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

        val alertDialogBuilder = AlertDialog.Builder(this)

        val dialogLayout = layoutInflater.inflate(R.layout.settings_dialog, null)
        alertDialogBuilder.setView(dialogLayout)

        val homePageEditText = dialogLayout.txtHomePageUrl as EditText
        homePageEditText.setText(sharedPref!!.getString("HomePageURL","https.//google.com"))

        val checkedTextView = dialogLayout.checkedTextView as CheckedTextView
        var showCheck = sharedPref!!.getBoolean("LoadLastURL",false)
        checkedTextView.setCheckMarkDrawable(if(showCheck)R.drawable.checked
        else R.drawable.unchecked)

        checkedTextView.setOnClickListener{
            checkedTextView.isChecked = !checkedTextView.isChecked()
            checkedTextView.setCheckMarkDrawable(
                if(checkedTextView.isChecked())R.drawable.checked
                else R.drawable.unchecked)
            sharedPref?.edit()?.putBoolean(("LoadLastURL"),checkedTextView.isChecked())?.apply()
        }

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(getString(R.string.btn_save)
            ){dialog, id ->
                sharedPref?.edit()?.putString("HomePageURL", homePageEditText.text.toString())?.apply()
            }
            .setNegativeButton(getString(R.string.btn_cancel)
            ){dialog, id -> dialog.cancel()}
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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