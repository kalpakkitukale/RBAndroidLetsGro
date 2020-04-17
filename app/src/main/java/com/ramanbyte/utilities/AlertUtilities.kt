package com.ramanbyte.utilities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import com.ramanbyte.R
import com.ramanbyte.databinding.DialogLoadingBinding

@SuppressLint("StaticFieldLeak")
object AlertUtilities {


    private var progressBar: ProgressBar? = null

    fun showLoader(mContext: Context): Dialog? {
        try {
            val dialogBuilder = Dialog(mContext)
            dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogBuilder.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
            dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null, false)
            val dialogLoadingBinding = DialogLoadingBinding.bind(view)
            val drawable = BindingUtils.getColorDrawable(
                mContext,

                BindingUtils.getColorFromAttribute(mContext, R.attr.dialogLoaderColor),
                R.drawable.progress_dialog_bg
            )
            dialogLoadingBinding?.dialogContainer?.background = (drawable)
            /*  try {
                  Glide.with(mContext).asGif().load(R.drawable.loader)
                      .into(dialogLoadingBinding.imgLoader)
              } catch (e: Exception) {
                  e.printStackTrace()
                  AppLog.errorLog(e.message, e)
              }*/

            dialogBuilder.setContentView(dialogLoadingBinding.getRoot())
            dialogBuilder.setCancelable(false)
            dialogBuilder.setCanceledOnTouchOutside(false)
            return dialogBuilder
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun showInfoAlertDialog(mContext: Context, msg: String) {
        try {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(msg)
            builder.setPositiveButton(
                BindingUtils.string(R.string.strOk)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

    }

    fun showAlertDialog(
        mContext: Context,
        msg: String,
        positiveBtnText: String?,
        negativeBtnText: String?,
        positiveListener: (DialogInterface, Int) -> Unit,
        negativeListener: (DialogInterface, Int) -> Unit
    ) {
        try {
            val builder = AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle)
            builder.setMessage(msg)
            builder.setPositiveButton(
                if (positiveBtnText != null && !positiveBtnText.isEmpty()) positiveBtnText else BindingUtils.string(
                    R.string.strOk
                ), positiveListener
            )

            builder.setNegativeButton(
                if (negativeBtnText != null && !negativeBtnText.isEmpty()) negativeBtnText else BindingUtils.string(
                    R.string.strCancel
                ), negativeListener
            )


            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setBackgroundColor(BindingUtils.color(R.color.colorPrimary))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setBackgroundColor(BindingUtils.color(R.color.colorPrimary))

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

    }

    fun showToast(mContext: Context, msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
    }

}