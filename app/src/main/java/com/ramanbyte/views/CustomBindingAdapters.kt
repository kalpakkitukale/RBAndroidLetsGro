package com.ramanbyte.views

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.ramanbyte.utilities.GlideApp


/**
 * @author Shital Kadaganchi <shital.k@ramanbyte.com>
 * @since 30/12/19
 */
class CustomBindingAdapters {

    companion
    object {

        @JvmStatic
        @BindingAdapter("app:errorText")
        fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
            view.error = errorMessage
            if (errorMessage != null) {
                view.requestFocus()
                view.isErrorEnabled = true
            } else
                view.isErrorEnabled = false
        }

        @JvmStatic
        @BindingAdapter(value = ["app:imageUrl", "app:placeholder"], requireAll = false)
        fun setImageUrl(imageView: ImageView, url: String, drawable: Drawable?) {

            GlideApp.with(imageView.context)
                .load(url)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView.setImageDrawable(drawable)
                        return false
                    }
                })
                .into(imageView)

        }

        @JvmStatic
        @BindingAdapter("android:isCheckedAttrChanged")
        fun setCheckedStatusAttrChangedListener(
            compoundButton: RadioButton,
            isCheckedAttrChangedListener: InverseBindingListener?
        ) {


            compoundButton.setOnCheckedChangeListener { compoundButton, b ->
                isCheckedAttrChangedListener?.onChange()
            }
        }

        @JvmStatic
        @BindingAdapter("app:isChecked")
        fun setCheckedStatus(compoundButton: RadioButton, checkedStatus: Boolean) {
            compoundButton.isChecked = checkedStatus
        }

        @JvmStatic
        @InverseBindingAdapter(
            attribute = "app:isChecked",
            event = "android:isCheckedAttrChanged"
        )
        fun isChecked(compoundButton: RadioButton): Boolean {

            return compoundButton.isChecked
        }

        @SuppressLint("SetTextI18n")
        @BindingAdapter("android:intValue")
        fun setText(view: TextView, value: Int) {
            view.text = value.toString()
        }

        @InverseBindingAdapter(attribute = "android:intValue")
        fun getText(view: TextView): Int {
            return Integer.parseInt(view.text.toString())
        }

        @JvmStatic
        @BindingAdapter("app:attachFloatingButton")
        fun bindRecyclerViewWithFB(recyclerView: RecyclerView, fb: FloatingActionButton) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0 && fb.isShown) {
                        fb.hide()
                    } else if (dy < 0 && !fb.isShown) {
                        fb.show()
                    }
                }
            })
        }

        @JvmStatic
        @BindingAdapter("app:setHtmlText")
        fun setHtmlText(webView: WebView, htmlText: String?) {
//            webView.loadData(htmlText,"text/html", "utf-8")
            webView.apply {
                /**
                 * Disable text selection
                 */
                setOnLongClickListener { true }
                isLongClickable = false
                // Below line prevent vibration on Long click
                isHapticFeedbackEnabled = false
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                loadDataWithBaseURL("", htmlText, "text/html", "utf-8", "")
            }
        }
    }
}