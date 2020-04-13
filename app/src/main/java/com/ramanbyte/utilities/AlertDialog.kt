package com.ramanbyte.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.databinding.AlertBinding

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
class AlertDialog<T : BaseViewModel>(
    private val mContext: Context,
    private val baseViewModel: T
) :
    Dialog(mContext) {

    private lateinit var alertBinding: AlertBinding

    init {
        baseViewModel.apply {

            isAlertDialogShown.observeForever { showAlert ->
                if (showAlert && !isShowing) {
                    show()
                } else if (isShowing) {
                    dismiss()
                }
            }
        }
    }

    /* fun setPositiveButton(buttonText: String, clickFunctionality: () -> Unit) {

         baseViewModel.positiveButtonClickFunctionality = clickFunctionality

         positiveButtonText = buttonText
     }

     fun setNegativeButton(buttonText: String, clickFunctionality: () -> Unit) {

         baseViewModel.negativeButtonClickFunctionality = clickFunctionality

         negativeButtonText = buttonText
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)

            alertBinding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.alert,
                null,
                false
            )

            alertBinding.apply {

                baseViewModel = this@AlertDialog.baseViewModel

                setContentView(root)

               /* if (alertDrawableResource != 0) {
                    imgAlert.setImageDrawable(BindingUtils.drawable(alertDrawableResource))
                } else {
                    imgAlert.visibility = View.GONE
                }*/

                /* if (isInfoAlert)
                     btnNo.visibility = View.GONE
                 else
                     btnNo.visibility = View.VISIBLE*/
            }

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    class AlertDialogResourceModel {

        var isInfoAlert: Boolean = false
        var message: String = BindingUtils.string(R.string.alert)
        var alertDrawableResource: Drawable? =
            BindingUtils.drawable(R.drawable.circle_launcher_white)!!
        var alertDrawableResourceSign: Drawable =
            BindingUtils.drawable(R.drawable.circle_launcher_white)!!

        var positiveButtonText = BindingUtils.string(R.string.strOk)
        var negativeButtonText = BindingUtils.string(R.string.strCancel)

        var positiveButtonClickFunctionality: () -> Unit = {}

        var negativeButtonClickFunctionality: () -> Unit = {}
    }
}