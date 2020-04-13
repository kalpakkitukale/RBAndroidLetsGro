package com.ramanbyte.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.databinding.DialogLoadingBinding


/**
 * Added by Vinay K.
 *
 * @desc progress loader with message
 * @since 26/02/2020.
 */

class ProgressLoader<T : BaseViewModel> : Dialog {

    private var mContext: Context? = null
    private var baseViewModel: BaseViewModel? = null
    private var isInfoAlert: Boolean = false
    private var dialogBinding: DialogLoadingBinding? = null
    private var message = BindingUtils.string(R.string.str_loading)

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, baseViewModel: T) : super(context) {
        this.mContext = context
        this.baseViewModel = baseViewModel

        this.baseViewModel?.apply {

            isLoaderShowingLiveData.observeForever { showLoader ->

                try {
                    if (showLoader && !isShowing) {
                        show()
                    } else if (isShowing) {
                        dismiss()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                }
            }
        }
    }

    fun setMessage(message: String) {
        dialogBinding!!.txtLoaderMessage.text = message
    }

    fun setInfoAlert(infoAlert: Boolean) {
        isInfoAlert = infoAlert
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.dialog_loading,
                null,
                false
            )
            dialogBinding?.apply {
                baseViewModel = this@ProgressLoader.baseViewModel
                setContentView(root)
                txtLoaderMessage.text = message
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dialogBinding?.progressPercentage?.clearAnimation()
    }
}
