package com.udacity.customViews

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.udacity.R
import kotlin.properties.Delegates

private const val ANIMATION_BASE_DURATION_MS: Long = 2000 // milliseconds

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()
    private var progress = 0f
    var text = resources.getString(R.string.button_download)
    private val circleRadius = resources.getDimension(R.dimen.progress_circle_radius)
    private val circleRightOffset = resources.getDimension(R.dimen.progress_circle_right_offset)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Clicked) {
            progress = 1f
        }
        if (new == ButtonState.Loading) {
            progress = 0f
            isEnabled = false
            text = resources.getString(R.string.button_loading)
        } else if (new == ButtonState.Completed) {
            progress = 1f
            isEnabled = true
            text = resources.getString(R.string.button_download)
        }
    }

    val textSize = resources.getDimension(R.dimen.default_text_size)
    val paint = Paint().apply {

    }

    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBackground()
        if (buttonState == ButtonState.Loading) {
            canvas.drawProgressBar()
            canvas.drawProgressCircle()
        }
        canvas.drawText()

    }

    private fun Canvas.drawBackground() {
        paint.color = resources.getColor(R.color.colorPrimary)
        save()
        translate(0f, 0f)
        drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        restore()
    }

    private fun Canvas.drawProgressBar() {
        paint.color = resources.getColor(R.color.colorPrimaryDark, resources.newTheme())
        save()
        val dx = width * progress
        drawRect(0f, 0f, dx, height.toFloat(), paint)
        restore()
    }


    private fun Canvas.drawText() {
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        paint.color = resources.getColor(R.color.white, resources.newTheme())

        save()
        val xPos = (width / 2f)
        val yPos = (height / 2 - (paint.descent() + paint.ascent()) / 2)
        translate(xPos, yPos)
        drawText(text, 0f, 0f, paint)
        restore()
    }


    private fun Canvas.drawProgressCircle() {
        val rectF = RectF(
            0f,
            0f,
            circleRadius,
            circleRadius
        )
        save()
        translate(width - circleRightOffset, (height - circleRadius) / 2f)
        paint.color = resources.getColor(R.color.colorAccent, resources.newTheme())
        drawArc(rectF, -90f, progress * 360, true, paint)
        restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        valueAnimator = ValueAnimator.ofFloat(0f, progress).apply {
            interpolator = DecelerateInterpolator()
            // The animation duration is longer for a larger arc
            duration =
                ANIMATION_BASE_DURATION_MS + (progress * ANIMATION_BASE_DURATION_MS).toLong()
            addUpdateListener { animator ->
                this@LoadingButton.progress = animator.animatedValue as Float
                this@LoadingButton.invalidate()
            }
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                buttonState = ButtonState.Loading
            }

            override fun onAnimationEnd(p0: Animator?) {
                buttonState = ButtonState.Completed
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        valueAnimator.start()
        invalidate()
        return super.performClick()
    }
}