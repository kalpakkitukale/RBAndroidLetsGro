package com.ramanbyte.utilities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.ofFloat
import android.annotation.TargetApi
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
object ViewAnimationUtils {


    fun expand(v: View) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targtetHeight = v.measuredHeight

        v.layoutParams.height = 0
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    (targtetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        var duration = (targtetHeight / v.context.resources.displayMetrics.density).toInt()
        duration = 400
        //        a.setInterpolator(ClassRoomAppController.getInstance(), android.R.anim.linear_interpolator);
        a.duration = duration.toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        var duration = (initialHeight / v.context.resources.displayMetrics.density).toInt()
        duration = 400
        //        a.setInterpolator(ClassRoomAppController.getInstance(), android.R.anim.linear_interpolator);
        a.duration = duration.toLong()
        v.startAnimation(a)
    }

    fun rotateView(view: View, rotationAngle: Int, timeInMillis: Int) {

        view.animate().rotation(rotationAngle.toFloat()).setDuration(timeInMillis.toLong()).start()
    }

    /**
     * Vinay K.
     * Desc :circular reveal animation for steppers.
     *
     * @param view          on isCourseView animation to be done.
     * @param isReverse     is reverse.
     * @param startDrawable starting drawable.
     * @param endDrawable   end drawable.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun circularRevel(
        view: View,
        isReverse: Boolean,
        startDrawable: Drawable,
        endDrawable: Drawable
    ) {
        val endRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt()

        val cx = (view.x + view.width / 2).toInt()
        val cy = (view.y + view.height / 2).toInt()
        var animator: Animator? = null
        if (!isReverse) {
            animator = android.view.ViewAnimationUtils.createCircularReveal(
                view,
                cx,
                cy,
                0f,
                endRadius.toFloat()
            )
            animator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.setBackgroundDrawable(endDrawable)
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    view.setBackgroundDrawable(startDrawable)
                }
            })
        } else {
            animator = android.view.ViewAnimationUtils.createCircularReveal(
                view,
                cx,
                cy,
                view.width.toFloat(),
                (view.width / 2).toFloat()
            )
            animator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    //                    super.onAnimationEnd(animation);
                    view.setBackgroundDrawable(endDrawable)
                }

                override fun onAnimationStart(animation: Animator) {
                    //                    super.onAnimationStart(animation);
                    //view.setBackgroundDrawable(startDrawable);
                }
            })
        }
        animator.duration = 300
        animator.start()
    }

    fun startAlphaAnimation(duration: Long?, visibility: Int,vararg  view: Any) {
        val alphaAnimation =
            if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
        alphaAnimation.duration = duration!!
        alphaAnimation.fillAfter = true
        view.forEach {
            (it as View).startAnimation(alphaAnimation)
        }
    }


    fun shakeViewErrorAnimator(): TranslateAnimation {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 300
        shake.interpolator = CycleInterpolator(7f)
        return shake
    }

    fun <T : View> getValueAnimation(t: T, duration: Int): ValueAnimator {
        val valueAnimator = ofFloat(0f, 500f)
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            t.translationY = progress
        }
        return valueAnimator
    }
}
