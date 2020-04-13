package com.ramanbyte.utilities

import android.graphics.drawable.Drawable
import com.ramanbyte.R
import com.ramanbyte.views.RoundedTextDrawable


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
class ViewUtils {

    companion object {
        fun getCharacterDrawable(character: String): Drawable? {
            return RoundedTextDrawable.builder()
                .beginConfig()
                .textColor(BindingUtils.color(R.color.colorWhite))
                .width(BindingUtils.dimen(R.dimen.dp_43).toInt())
                .height(BindingUtils.dimen(R.dimen.dp_43).toInt())
                .endConfig()
                .round()
                .build(
                    character.toUpperCase(),
                    StaticMethodUtilitiesKtx.getRandomColors()
                )
        }
    }
}