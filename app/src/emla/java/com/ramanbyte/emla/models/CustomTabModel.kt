package com.ramanbyte.emla.models

import android.graphics.drawable.Drawable
import android.view.View

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 25/3/21
 */
class CustomTabModel {
    var id: Int? = null
    var icon: Drawable? = null
    var title: String = ""
    var clickListener: (view: View, obj: Any,position : Int) -> Unit? = { view: View, obj: Any,position : Int -> }
}