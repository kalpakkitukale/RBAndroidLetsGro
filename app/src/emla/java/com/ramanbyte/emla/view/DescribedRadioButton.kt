package com.ramanbyte.emla.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.ramanbyte.R
import com.ramanbyte.databinding.LayoutDescribedRadioButtonBinding
import com.ramanbyte.emla.base.di.RadioCheckable
import kotlinx.android.synthetic.emla.layout_described_radio_button.view.*


class DescribedRadioButton(mContext: Context, attrs: AttributeSet?) :
    ConstraintLayout(mContext, attrs), RadioCheckable {

    var imageResource: Drawable? = null
        set(value) {

            if (value != null)
                layoutDescribedRadioButtonBinding?.apply {
                    radioDescriptionDrawable.setImageDrawable(value)
                }
            invalidate()
            requestLayout()
            field = value
        }

    var description: String? = null
        set(value) {

            if (value != null)
                layoutDescribedRadioButtonBinding?.apply {
                    radioDescription?.text = value
                }
            invalidate()
            requestLayout()
            field = value
        }

    private val mOnCheckedChangeListeners = ArrayList<RadioCheckable.OnCheckedChangeListener>()

    private var layoutDescribedRadioButtonBinding: LayoutDescribedRadioButtonBinding? = null

    private var mChecked: Boolean = false
    /*set(value) {

        layoutDescribedRadioButtonBinding?.apply {
            radioButton?.isChecked = value
        }
        invalidate()
        requestLayout()
        field = value
    }*/

    init {

        var a: TypedArray? = null

        try {
            if (attrs != null) {
                a = context.obtainStyledAttributes(attrs, R.styleable.DescribedRadioButton)
                imageResource = a.getDrawable(R.styleable.DescribedRadioButton_drb_drawableResource)
                description = a.getString(R.styleable.DescribedRadioButton_drb_description)
                mChecked = a.getBoolean(R.styleable.DescribedRadioButton_drb_isChecked, false)
            }

            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            layoutDescribedRadioButtonBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.layout_described_radio_button,
                    this,
                    true
                )
        } finally {
            a?.recycle()
        }

        layoutDescribedRadioButtonBinding?.apply {
            if (imageResource != null)
                radioDescriptionDrawable?.setImageDrawable(imageResource)
            //     radioDescriptionDrawable?.visibility = View.VISIBLE

            if (description != null) {
                radioDescription?.text = description
            }

            radioButton.isChecked = mChecked

            /*mainlayout?.setOnClickListener {
                this@DescribedRadioButton.isChecked = !mChecked
            }*/
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            /*  if (mOnCheckedChangeListeners.isNotEmpty()) {
                  for (i in mOnCheckedChangeListeners.indices) {
                      mOnCheckedChangeListeners[i].onCheckedChanged(this, mChecked)
                  }
              }*/
            layoutDescribedRadioButtonBinding?.radioButton?.isChecked = mChecked
        }
        invalidate()
        requestLayout()
    }

    override fun addOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener) {
        mOnCheckedChangeListeners.add(onCheckedChangeListener)
    }

    override fun removeOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener) {
        mOnCheckedChangeListeners.remove(onCheckedChangeListener)
    }
}