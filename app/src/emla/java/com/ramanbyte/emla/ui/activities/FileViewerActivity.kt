package com.ramanbyte.emla.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityFileViewerBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.FileViewerViewModel
import com.ramanbyte.utilities.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class FileViewerActivity : BaseActivity<ActivityFileViewerBinding, FileViewerViewModel>(
    authModuleDependency
) {

    private var activityFileViewerBinding: ActivityFileViewerBinding? = null
    private var fileName: String? = null
    private var mContext: Context? = null

    override val viewModelClass: Class<FileViewerViewModel> = FileViewerViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_file_viewer

    override fun initiate() {
        initComponents()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initComponents() {
        try {
            mContext = this
            ProgressLoader(this@FileViewerActivity, viewModel)
            AlertDialog(this@FileViewerActivity, viewModel)
            activityFileViewerBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_file_viewer)
            fileName = intent.extras?.getString(KEY_FILE_NAME)!!
            setUpToolBar()

            val loadS3URL = GetS3FileInstance()
            loadS3URL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileName)

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.infoLog(e.message!!)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setURLToWebView(finalLoadURL: String) {
        AppLog.infoLog("File Url $finalLoadURL")
        activityFileViewerBinding?.fileViewerWebview?.apply {
            webViewClient = AppWebViewClients()
            settings?.apply {
                builtInZoomControls = true
                loadWithOverviewMode = true
                useWideViewPort = true
                javaScriptEnabled = true
            }
            loadUrl(finalLoadURL)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetS3FileInstance : AsyncTask<String?, Int?, String?>() {
        override fun doInBackground(vararg voids: String?): String? {
            val fileName = voids[0]
            /*val s3link = (fileName?.let {
                AppS3Client.createInstance(mContext!!)
                    .getFileAccessUrl(it).toString()
            })*/
            val s3link =
                StaticMethodUtilitiesKtx.getS3DynamicURL(fileName!!, mContext!!)

            val fileExtension =
                StaticMethodUtilitiesKtx.getFileExtension(FileUtils.getOriginalFileName(fileName!!))

            val mimeType = FileUtils.getMimeType(extension = fileExtension!!)
            AppLog.debugLog("MimeType ----$mimeType")
            /*here is the check the file type for showing in the file this activity*/
            return if (mimeType != null && mimeType.contains(KEY_IMAGE)) {
                s3link
            } else {
                val encodedUrl = encodeURIComponent(s3link!!) + "&embedded=true"
                "https://docs.google.com/viewer?url=$encodedUrl"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            setURLToWebView(result!!)
        }
    }

    inner class AppWebViewClients : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            activityFileViewerBinding!!.progress.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            activityFileViewerBinding!!.progress.visibility = View.GONE
        }
    }

    private fun encodeURIComponent(s: String): String? {
        var result: String? = null
        try {
            result = URLEncoder.encode(s, "UTF-8")
                .replace("\\+".toRegex(), "%20")
                .replace("!", "%21")
                .replace("'", "%27")
                .replace("(", "%28")
                .replace(")", "%29")
                .replace("~", "%7E")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            result = s
        }
        // This exception should never occur.
        return result
    }

    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, FileViewerActivity::class.java)
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(activityFileViewerBinding!!.appBarLayout.toolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val name = FileUtils.getFileNameFromPath(FileUtils.getOriginalFileName(fileName!!))
        activityFileViewerBinding!!.appBarLayout.title = name

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.file_viewer_menu_file, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnSave -> {
                if (PermissionsManager.checkPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        WRITE_STORGAE_PERMISSION_REQUEST_CODE
                    )
                ) {
                    downloadFile()
                }
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    /* download the file from server */
    private fun downloadFile() {

        viewModel.apply {
            setAlertDialogResourceModelMutableLiveData(
                BindingUtils.string(R.string.file_download_msg), true
                /*BindingUtils.drawable(R.drawable.ic_element_api_exception)!!*/,
                KEY_OK,
                {
                    isAlertDialogShown.postValue(false)
                })
            isAlertDialogShown.postValue(true)
        }

        val name = FileUtils.getFileNameFromPath(FileUtils.getOriginalFileName(fileName!!))
        val filePath = FileUtils.getNewCreatedPdfFilePath(name)
        AppS3Client.createInstance(this).apply {
            download(fileName!!, filePath, KEY_FILE_DOWNLOAD, 1)
        }
    }


}


