package com.ramanbyte.views

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @author Vinay Kumbhar <vinay.pkumbhar></vinay.pkumbhar>@gmail.com>
 * @since 11-04-2020
 */
class ListPaddingDecoration(context: Context,PADDING_IN_DIPS : Float) : ItemDecoration() {
    private val mPadding: Int
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
        if (itemPosition == 0) {
            outRect.top = mPadding
        }
    }



    init {
        val metrics = context.resources.displayMetrics
        mPadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            PADDING_IN_DIPS.toFloat(),
            metrics
        ).toInt()
    }
}