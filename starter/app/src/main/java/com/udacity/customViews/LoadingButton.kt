package com.udacity.customViews

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

    companion object {
        private const val DEFAULT_BACKGROUND_COLOR = Color.GREEN
        private const val DEFAULT_PROGRESS_BAR_COLOR = Color.DKGRAY
        private const val DEFAULT_PROGRESS_CIRCLE_COLOR = Color.YELLOW
        private const val DEFAULT_TEXT_COLOR = Color.WHITE
    }

    private var widthSize = 0
    private var heightSize = 0
    private var bgColor = DEFAULT_BACKGROUND_COLOR
    private var progressBarColor = DEFAULT_PROGRESS_BAR_COLOR
    private var progressCircleColor = DEFAULT_PROGRESS_CIRCLE_COLOR
    private var textColor = DEFAULT_TEXT_COLOR

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
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton, 0, 0
        )
        bgColor = typedArray.getColor(R.styleable.LoadingButton_buttonBackground, DEFAULT_BACKGROUND_COLOR)
        progressBarColor = typedArray.getColor(R.styleable.LoadingButton_progressBarColor, DEFAULT_PROGRESS_BAR_COLOR)
        progressCircleColor = typedArray.getColor(R.styleable.LoadingButton_progressCircleColor, DEFAULT_PROGRESS_CIRCLE_COLOR)
        textColor = typedArray.getColor(R.styleable.LoadingButton_textColor, DEFAULT_TEXT_COLOR)
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
        paint.color = bgColor
        save()
        translate(0f, 0f)
        drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        restore()
    }

    private fun Canvas.drawProgressBar() {
        paint.color = progressBarColor
        save()
        val dx = width * progress
        drawRect(0f, 0f, dx, height.toFloat(), paint)
        restore()
    }


    private fun Canvas.drawText() {
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        paint.color = textColor

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
        paint.color = progressCircleColor
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