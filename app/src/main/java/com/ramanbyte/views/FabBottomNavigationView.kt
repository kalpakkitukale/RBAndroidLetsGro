package com.ramanbyte.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapePathModel
import com.ramanbyte.R

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 10-04-2020
 */
class FabBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {
    private var topCurvedEdgeTreatment: TopCurvedEdgeTreatment
    private var materialShapeDrawable: MaterialShapeDrawable
    private var fabSize = 0F
    var fabCradleMargin = 0F
    var fabCradleRoundedCornerRadius = 0F
    var cradleVerticalOffset = 0F

    init {
        val ta =
            context.theme.obtainStyledAttributes(attrs, R.styleable.FabBottomNavigationView, 0, 0)
        fabSize = ta.getDimension(R.styleable.FabBottomNavigationView_fab_size, 0F)
        fabCradleMargin = ta.getDimension(R.styleable.FabBottomNavigationView_fab_cradle_margin, 0F)
        fabCradleRoundedCornerRadius = ta.getDimension(
            R.styleable.FabBottomNavigationView_fab_cradle_rounded_corner_radius,
            0F
        )
        cradleVerticalOffset =
            ta.getDimension(R.styleable.FabBottomNavigationView_cradle_vertical_offset, 0F)

        topCurvedEdgeTreatment =
            TopCurvedEdgeTreatment(
                fabCradleMargin,
                fabCradleRoundedCornerRadius,
                cradleVerticalOffset
            ).apply {
                fabDiameter = fabSize
            }

        val shapePathModel = ShapePathModel().apply {
            topEdge = topCurvedEdgeTreatment
        }

        materialShapeDrawable = MaterialShapeDrawable(shapePathModel).apply {
            setTint(ContextCompat.getColor(context, R.color.colorBackgroundWhiteOnLightBlackOnDark))
            shadowElevation = 4
            shadowRadius = 16
            isShadowEnabled = true
            paintStyle = Paint.Style.FILL_AND_STROKE
        }

        background = materialShapeDrawable
    }


    fun transform(fab: FloatingActionButton, visible: Boolean = false) {
        if(visible){
            if(!fab.isVisible){
                ValueAnimator.ofFloat(materialShapeDrawable.interpolation, 1F).apply {
                    addUpdateListener {
                        materialShapeDrawable.interpolation = it.animatedValue as Float
                    }
                    doOnEnd { fab.show() }
                    start()
                }
            }
        }else{
            if (fab.isVisible){
                fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                    override fun onHidden(fab: FloatingActionButton?) {
                        super.onHidden(fab)
                        ValueAnimator.ofFloat(materialShapeDrawable.interpolation, 0F).apply {
                            addUpdateListener {
                                materialShapeDrawable.interpolation = it.animatedValue as Float
                            }
                            start()
                        }
                    }
                })
            }
        }
        /*if (fab.isVisible) {
            fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    ValueAnimator.ofFloat(materialShapeDrawable.interpolation, 0F).apply {
                        addUpdateListener {
                            materialShapeDrawable.interpolation = it.animatedValue as Float
                        }
                        start()
                    }
                }
            })
        } else {
            ValueAnimator.ofFloat(materialShapeDrawable.interpolation, 1F).apply {
                addUpdateListener {
                    materialShapeDrawable.interpolation = it.animatedValue as Float
                }
                doOnEnd { fab.show() }
                start()
            }
        }*/
    }


}