package com.ramanbyte.emla.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import com.ramanbyte.R
import android.os.Bundle
import android.os.Environment
import android.print.PdfConverter
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ramanbyte.BuildConfig

import com.ramanbyte.aws_s3_android.utilities.S3Constant
import com.ramanbyte.databinding.ActivityApplicationFormViewerBinding
import com.ramanbyte.utilities.*
import java.io.File

class CertificateViewerActivity : AppCompatActivity() {

    private var formBinding: ActivityApplicationFormViewerBinding? = null
    private var newUrl = KEY_BLANK
    private val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"
    private val MOBILE_USER_AGENT =
        "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
    private var isPdfAlreadyExist = false
    private var isPdfGenerationTaskRunning = false
    var courseId = 0
    var dm: DownloadManager? = null
    private var source = KEY_BLANK
    private var certificateFilePath: Uri? = null

    private var downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action?.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE, true) == true) {
                formBinding?.fileViewerWebview?.setDownloadListener(null)

                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

                val uri = dm?.getUriForDownloadedFile(downloadId)

                val pdfView = formBinding?.pdfView

                pdfView?.apply {

                    val pdfConfig =
                        fromUri(uri)

                    pdfConfig.apply {

                        password(null)
                        defaultPage(0)
                        enableSwipe(true)
                        swipeHorizontal(false)
                        enableDoubletap(true)
                        onDraw { canvas, pageWidth, pageHeight, displayedPage -> }
                        onDrawAll { canvas, pageWidth, pageHeight, displayedPage -> }
                        onPageError { page, t ->
                            Toast.makeText(
                                this@CertificateViewerActivity,
                                "Error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        onPageChange { page, pageCount -> }
                        onTap { true }
                        onRender { nbPages, pageWidth, pageHeight -> pdfView.fitToWidth() }
                        enableAnnotationRendering(true)
                        invalidPageColor(Color.WHITE)
                        load()
                    }
                }

                formBinding?.progress?.visibility = View.GONE
                certificateFilePath = uri
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        formBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_application_form_viewer)
        initToolbar()
        intent.apply {
            val userId = getIntExtra("userId", 0)
            courseId = getIntExtra("courseId", 0)

            newUrl =
                "http://report.ramanbyte.com/test/idbicertificate.aspx?UserId=$userId&CourseId=$courseId"

            //http://report.ramanbyte.com/Prod/idbicertificate.aspx?UserId=1&CourseId=1

            AppLog.infoLog("urllll $newUrl")

        }

        dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

//        setupFileUri()

        setupWebView()

        formBinding!!.progress.visibility = View.VISIBLE
    }





    private fun initToolbar() {
        try {

            setSupportActionBar(formBinding?.appBarLayout?.toolbar)

            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)

            formBinding!!.appBarLayout.title = BindingUtils.string(R.string.view_certificate)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //StaticMethodUtilitiesKtx.changeStatusBarColor(window, R.color.colorYellow)
            }

            val currentApiVersion = android.os.Build.VERSION.SDK_INT
            if (currentApiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (formBinding!!.appBarLayout.appBar != null) {
                    formBinding!!.appBarLayout.appBar.elevation = 0.0f
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }


    fun setupWebView() {
        formBinding!!.apply {

            registerReceiver(
                downloadReceiver,
                IntentFilter().apply { addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE) })

            fileViewerWebview.webViewClient = WebViewClient()
            //fileViewerWebview.settings.setSupportZoom(true)
            fileViewerWebview.webChromeClient =
                WebChromeClient() //this is the chrome client for the web view


            fileViewerWebview.loadUrl(newUrl)

            fileViewerWebview.setDownloadListener(downloadListener)
        }
    }


    val downloadListener =
        DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->

            formBinding?.progress?.visibility = View.VISIBLE

            val request = DownloadManager.Request(
                Uri.parse(url)
            ) // studentclassroomplusgit
            request.setMimeType(mimeType)
            val cookies = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)

            request.setDestinationInExternalFilesDir(
                this,
                Environment.DIRECTORY_DOCUMENTS,
                "CourseCertificate_$courseId.pdf"
            )

            dm?.enqueue(request)
        }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemID = item.itemId

        when (itemID) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.view_share ->{
                if(certificateFilePath != null) {
                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.setType("application/pdf")
                    intentShareFile.putExtra(
                        Intent.EXTRA_STREAM,
                        certificateFilePath
                    )

                    intentShareFile.putExtra(
                        Intent.EXTRA_SUBJECT,
                        "Sharing Certificate..."
                    )
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Certificate...")

                    startActivity(Intent.createChooser(intentShareFile, "Share Certificate"))
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
            //return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_share, menu)
        return true

    }

    @SuppressLint("StaticFieldLeak")
    inner class PDFGenerationTask(var programId: Int) : AsyncTask<String, String, String>() {
        private var file: File? = null

        override fun onPreExecute() {
            super.onPreExecute()
            isPdfGenerationTaskRunning = true
        }

        override fun doInBackground(vararg params: String?): String? {
            try {
                val converter = PdfConverter.getInstance()
                /*file = File(
                    KEY_APP_STORAGE_ABSOLUTE_PATH + PATH_SEPARATOR + "${programId}.pdf"
                )*/

                val formFilename = "CourseCertificate_$programId.pdf"
                file = File(FileUtils.getNewCreatedFilePath(formFilename))
                converter.convert(
                    this@CertificateViewerActivity, source, file
                )
            } catch (throwable: Throwable) {
                AppLog.infoLog(throwable.message!!)
                throwable.printStackTrace()
            }
            return ""
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (file!!.length() > 0) {
                isPdfAlreadyExist = true
                //sharePdf(file!!)
                formBinding?.root?.snackbar(
                    String.format(
                        BindingUtils.string(
                            R.string.file_stored_message
                        ), "${file?.absolutePath}"
                    )
                )
            } else {
                PDFGenerationTask(programId).execute()
                //Toast.makeText(this@TransactionReceiptActivity, "", Toast.LENGTH_LONG).show()
            }
            isPdfGenerationTaskRunning = false
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            WRITE_STORGAE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted for external storage
                    if (!isPdfGenerationTaskRunning) {
                        PDFGenerationTask(courseId).execute()
                    }
                } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    //permission denied by user with don ask again
                    //Now further we check if used denied permanently then goto setting page of the application to enable permission manually
                    val appname = "<b>${BindingUtils.string(R.string.app_name)}</b>"
                    val message = BindingUtils.string(R.string.storage_permission_desc)
                    val myMessage = String.format(message, appname)

                    requestPermissionAlert(myMessage)
                }
            }
        }
    }

    fun requestPermissionAlert(message: String) {
        AlertUtilities.showAlertDialog(
            S3Constant.mContext!!, message,
            "Set",
            BindingUtils.string(R.string.strCancel),
            { dialog, which ->
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                }
            },
            { dialog, which -> })
    }

    override fun onDestroy() {
        try {
            if (downloadReceiver != null)
                unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        super.onDestroy()
    }
}
