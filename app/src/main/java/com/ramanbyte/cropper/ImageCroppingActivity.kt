package com.ramanbyte.cropper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil

import java.io.ByteArrayOutputStream

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.ramanbyte.imagecropper.CropImage
import com.ramanbyte.imagecropper.CropImageView
import com.ramanbyte.R

import com.ramanbyte.aws_s3_android.utilities.S3Constant.Companion.mContext
import com.ramanbyte.databinding.ActivityImageCroppingBinding
import com.ramanbyte.utilities.*
import java.io.File
import java.io.InputStream

class ImageCroppingActivity : AppCompatActivity() {

    private var activityImageCroppingBinding: ActivityImageCroppingBinding? = null
    private var imagePath: String? = ""
    private var mCropImageViewOptions = CropImageViewOptions()
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mContext = this
            activityImageCroppingBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_cropping)
            initToolbar()
            Handler().postDelayed({
                initViews()
            },300)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message,e)
        }

    }

    private fun initViews() {
        try {


            val intent = intent
            imagePath = intent.getStringExtra(KEY_IMAGE_PATH)
            val isFixedAspectRatio = intent.getBooleanExtra(KEY_IS_FIXED_ASPECT_RATIO,true)

            if(isFixedAspectRatio) setInitialCropRect()

            updateCurrentCropViewOptions()

            if (imagePath != null && !imagePath!!.isEmpty()) {
                // setImage to image view
                /**Updated by Vinay K.
                 * FIxed issue of rotated image on camera capture.
                 * */
                val bitMap = handleSamplingAndRotationBitmap(mContext!!, Uri.fromFile(File(imagePath)))
                activityImageCroppingBinding!!.imgCropView.setImageBitmap(bitMap)
            }
            setListener()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

    }

    private fun setListener() {

        activityImageCroppingBinding!!.imgCropView.setOnSetImageUriCompleteListener { view, uri, error -> }

        activityImageCroppingBinding!!.imgCropView.setOnCropImageCompleteListener { view, result ->
            handleCropResult(
                result
            )
        }

    }

    private fun updateCurrentCropViewOptions() {
        val options = CropImageViewOptions()
        options.scaleType = activityImageCroppingBinding!!.imgCropView.scaleType
        options.cropShape = activityImageCroppingBinding!!.imgCropView.cropShape
        options.guidelines = activityImageCroppingBinding!!.imgCropView.guidelines
        options.aspectRatio = activityImageCroppingBinding!!.imgCropView.aspectRatio
        options.fixAspectRatio = activityImageCroppingBinding!!.imgCropView.isFixAspectRatio
        options.showCropOverlay = activityImageCroppingBinding!!.imgCropView.isShowCropOverlay
        options.showProgressBar = activityImageCroppingBinding!!.imgCropView.isShowProgressBar
        options.autoZoomEnabled = activityImageCroppingBinding!!.imgCropView.isAutoZoomEnabled
        options.maxZoomLevel = activityImageCroppingBinding!!.imgCropView.maxZoom
        options.flipHorizontally = activityImageCroppingBinding!!.imgCropView.isFlippedHorizontally
        options.flipVertically = activityImageCroppingBinding!!.imgCropView.isFlippedVertically
        mCropImageViewOptions = options
    }

    /**
     * Set the initial rectangle to use.
     * updated by Vinay K
     * chaged to aspect ratio.
     */
    private fun setInitialCropRect() {
//        activityImageCroppingBinding!!.imgCropView.cropRect = Rect(100, 300, 500, 1200)
        activityImageCroppingBinding!!.imgCropView.setAspectRatio(1, 1)

    }

    /**
     * Reset crop window to initial rectangle.
     */
    private fun resetCropRect() {
        activityImageCroppingBinding!!.imgCropView.resetCropRect()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_image_crop, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {

                android.R.id.home -> {
                    super.onBackPressed()
                    return true
                }

                R.id.nav_crop -> {
                    activityImageCroppingBinding!!.imgCropView.getCroppedImageAsync()
                    return true
                }

                R.id.nav_refresh -> {
                    resetCropRect()
                    return true
                }
            }

            return super.onOptionsItemSelected(item)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        } finally {
            return false
        }
    }

    private fun initToolbar() {
        try {
            setSupportActionBar(activityImageCroppingBinding!!.toolbar)

            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)

            activityImageCroppingBinding!!.toolbar.setTitle("")

            val currentApiVersion = Build.VERSION.SDK_INT
            if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
                if (activityImageCroppingBinding!!.appBar != null) {
                    activityImageCroppingBinding!!.appBar.setElevation(0.0f)

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

    }

    private fun handleCropResult(result: CropImageView.CropResult) {
        val intent = Intent()
        var resultCode = Activity.RESULT_CANCELED
        if (result.error == null) {

            if (result.uri != null) {
                intent.putExtra(
                    KEY_CROP_IMAGE_PATH,
                    ImageUtils.getRealPathFromURI(result.uri)
                )
            } else {
                val cropBitmap =
                    if (activityImageCroppingBinding!!.imgCropView.cropShape == CropImageView.CropShape.OVAL)
                        CropImage.toOvalBitmap(result.bitmap)
                    else
                        result.bitmap
                val cropPath = ImageUtils.getRealPathFromURI(getImageUri(mContext!!, cropBitmap))
                intent.putExtra(KEY_CROP_IMAGE_PATH, cropPath)
            }

            intent.putExtra(KEY_CROP_IMAGE_SIZE, result.sampleSize)

            resultCode = Activity.RESULT_OK
        }
        setResult(resultCode, intent)
        finish()
        super.onBackPressed()
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    companion object {
        fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri): Bitmap? {
            val MAX_HEIGHT = 500;
            val MAX_WIDTH = 500;
            var compressionRatio = 2

            // First decode with inJustDecodeBounds=true to check dimensions
            val options: BitmapFactory.Options = BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            var imageStream: InputStream? =
                mContext?.getContentResolver()?.openInputStream(selectedImage);
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream?.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            imageStream = context.getContentResolver().openInputStream(selectedImage);
            var img: Bitmap? = BitmapFactory.decodeStream(imageStream, null, options);

            img = img?.let { rotateImageIfRequired(it, selectedImage) };

            return img;
        }

        /**Calcualates the size of image*/
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and width
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                AppLog.infoLog("Image length ->"+heightRatio+"\n"+heightRatio)

                // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
                // with both dimensions larger than or equal to the requested height and width.
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

                // This offers some additional logic in case the image has a strange
                // aspect ratio. For example, a panorama may have a much larger
                // width than height. In these cases the total pixels might still
                // end up being too large to fit comfortably in memory, so we should
                // be more aggressive with sample down the image (=larger inSampleSize).

                val totalPixels = (width * height).toFloat()

                // Anything more than 2x the requested pixels we'll sample down further
                val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

                while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                    inSampleSize++
                }
            }
            return inSampleSize
        }

        private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap {

            val ei = ExifInterface(selectedImage.getPath());
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 ->
                    return rotateImage(img, 90);
                ExifInterface.ORIENTATION_ROTATE_180 ->
                    return rotateImage(img, 180);
                ExifInterface.ORIENTATION_ROTATE_270 ->
                    return rotateImage(img, 270);
                else ->
                    return img;
            }
        }


        private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
            img.recycle()
            return rotatedImg
        }
    }

}
