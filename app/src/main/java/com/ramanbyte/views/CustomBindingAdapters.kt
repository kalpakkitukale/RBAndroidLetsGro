
package com.ramanbyte.views

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RadioButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
                .placeholder(drawable)
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
    }
}