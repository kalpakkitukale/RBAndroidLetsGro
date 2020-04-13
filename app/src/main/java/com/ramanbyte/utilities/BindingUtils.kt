package com.ramanbyte.utilities

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.ramanbyte.BaseAppController


object BindingUtils {

    @JvmStatic
    var filter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (Character.isWhitespace(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }

    @JvmStatic
    fun string(resourceId: Int): String {
        return BaseAppController.applicationInstance?.resources?.getString(resourceId)!!
    }

    @JvmStatic
    fun string(resourceId: Int, vararg parameters: Any): String {
        return BaseAppController.applicationInstance?.resources?.getString(resourceId, *parameters)!!
    }

    @JvmStatic
    fun stringArray(resourceId: Int): Array<String> {
        return BaseAppController.applicationInstance?.resources?.getStringArray(resourceId)!!
    }

    @JvmStatic
    fun integerArray(resourceId: Int): IntArray {
        return BaseAppController.applicationInstance?.resources?.getIntArray(resourceId)!!
    }

    @JvmStatic
    fun color(resourceId: Int): Int {
        return ContextCompat.getColor(BaseAppController.applicationInstance!!, resourceId)
    }

    @JvmStatic
    fun drawable(resourceId: Int): Drawable? {
        return ContextCompat.getDrawable(BaseAppController.applicationInstance!!, resourceId)
    }

    @JvmStatic
    fun dimen(resourceId: Int): Float {
        return BaseAppController.applicationInstance?.resources?.getDimension(resourceId)!!
    }

    @JvmStatic
    fun getColorFromAttribute(mContext: Context, referencedAttribute: Int): Int {
        val attrs = intArrayOf(referencedAttribute)
        val theme = mContext.theme
        val ta = theme.obtainStyledAttributes(attrs)

        val color = ta.getColor(0, color(android.R.color.transparent))
        ta.recycle()
        return color
    }

    @JvmStatic
    fun getColorDrawable(mContext: Context, color: Int, resourceId: Int): Drawable? {
        try {
            val mDrawable = ContextCompat.getDrawable(mContext, resourceId)!!
            mDrawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
            return mDrawable
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            return ContextCompat.getDrawable(mContext, resourceId)
        }
    }

    fun getToolbarHeight(mContext: Context): Int {

        val attrs = arrayOf(android.R.attr.actionBarSize).toIntArray()
        val ta = mContext.obtainStyledAttributes(attrs)
        val toolBarHeight = ta.getDimensionPixelSize(0, -1)
        ta.recycle()
        return toolBarHeight
    }

    fun dpToPx(context: Context, value: Float): Float {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            r.displayMetrics
        )
    }
}
