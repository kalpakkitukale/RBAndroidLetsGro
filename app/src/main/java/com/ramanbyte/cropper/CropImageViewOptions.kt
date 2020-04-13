package com.ramanbyte.cropper
import android.util.Pair
import com.ramanbyte.imagecropper.CropImageView


/**
 * @author Akash Inkar <akash.d@ramanbyte.com>
 * @since 18/10/19
 */

class CropImageViewOptions {

    var scaleType: CropImageView.ScaleType = CropImageView.ScaleType.CENTER_INSIDE

    var cropShape: CropImageView.CropShape = CropImageView.CropShape.RECTANGLE

    var guidelines: CropImageView.Guidelines = CropImageView.Guidelines.ON_TOUCH

    var aspectRatio = Pair(1, 1)

    var autoZoomEnabled: Boolean = false

    var maxZoomLevel: Int = 0

    var fixAspectRatio: Boolean = false

    var multitouch: Boolean = false

    var showCropOverlay: Boolean = false

    var showProgressBar: Boolean = false

    var flipHorizontally: Boolean = false

    var flipVertically: Boolean = false
}