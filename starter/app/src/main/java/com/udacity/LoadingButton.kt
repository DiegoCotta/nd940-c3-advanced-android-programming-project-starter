package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes
import com.udacity.ButtonState.*
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var backgroundButtonColor: Int = 0
    private var loadingCircleColor: Int = 0
    private var textColor: Int = 0
    private var defaultText: String = ""
    private var loadingText: String = ""
    private var loadingBackgroundColor: Int = 0

    private var currentText = ""

    private var widthSize = 0
    private var heightSize = 0
    private val rect = Rect()
    private var progressArc = RectF()
    private var progressIndicator = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        typeface = Typeface.create("Roboto", Typeface.BOLD)
    }

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(Completed) { _, _, newValue ->
        when (newValue) {
            Loading -> {
                valueAnimator = ValueAnimator.ofInt(0, 1000).apply {
                    addUpdateListener {
                        progressIndicator = animatedValue as Int
                        invalidate()
                    }
                    duration = 30000
                    doOnStart {
                        currentText = loadingText
                        this@LoadingButton.isEnabled = false
                    }

                    doOnEnd {
                        progressIndicator = 0
                        this@LoadingButton.isEnabled = true
                        currentText = defaultText
                    }
                    start()
                }
            }

            Completed -> {
                progressIndicator = 0
                this@LoadingButton.isEnabled = true
                currentText = defaultText
            }

            Clicked -> {
                buttonState = Loading
                this.isEnabled = false
            }
        }
        invalidate()
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundButtonColor = getColor(R.styleable.LoadingButton_backgroundColor, Color.BLUE)
            defaultText = getString(R.styleable.LoadingButton_text) ?: ""
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: "Loading"
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, Color.RED)
            loadingBackgroundColor = getColor(R.styleable.LoadingButton_loadingBackGroundColor, Color.MAGENTA)
            currentText = defaultText
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Background button
        paint.color = backgroundButtonColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        //progress arc and progress rect
        if (buttonState == Loading) {
            //rect progress
            paint.color = loadingBackgroundColor
            val progressRect = progressIndicator / 1000f * widthSize
            canvas.drawRect(0f, 0f, progressRect, heightSize.toFloat(), paint)

            //circle progress
            val sweepAngle = progressIndicator / 1000f * 360f
            paint.color = loadingCircleColor
            canvas.drawArc(progressArc, 0f, sweepAngle, true, paint)
        }
        //text button
        paint.color = textColor
        paint.getTextBounds(currentText, 0, currentText.length, rect)
        val centerbutton = measuredHeight.toFloat() / 2 - rect.centerY()
        canvas.drawText(currentText, widthSize / 2f, centerbutton, paint)

    }

    @SuppressLint("DrawAllocation")
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
        progressArc = RectF(widthSize - 100f, heightSize / 2 - 25f, widthSize.toFloat() - 50f, heightSize / 2 + 25f)
    }

    fun startLoading() {
        buttonState = Loading
    }

    fun setCompleted() {
        buttonState = Completed
    }

    fun setDelayedCompleted() {
        val fraction = valueAnimator.animatedFraction
        valueAnimator.cancel()
        valueAnimator.setCurrentFraction(fraction+0.1f)
        valueAnimator.duration = 1000
        valueAnimator.start()
    }
}