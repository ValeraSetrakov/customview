package com.valerasetrakov.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import kotlin.random.Random

class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val figurePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textView = TextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            this.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    @ColorInt var color: Int = Color.GREEN
    var colors = mutableListOf<Int>()
    private val randomColor : Int
        get() {
            if (colors.isEmpty()) return color
            val randomIndex = Random.nextInt(0, colors.size)
            return colors[randomIndex]
        }

    var startSize: Int = 0
    var endSize: Int = 0
    private val randomSize: Int
        get() = Random.nextInt(startSize, endSize)

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private val figures = mutableListOf<DrawInfo>()

    init {

        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomView,
            defStyleAttr,
            R.style.CustomViewStyle
        )

        for(i in 0 until ta.indexCount) {
            val attr = ta.getIndex(i)
            when(attr) {
                R.styleable.CustomView_color -> {
                    color = ta.getColor(attr, Color.GREEN)
                }
                R.styleable.CustomView_startSize -> {
                    startSize = ta.getDimensionPixelSize(attr, 0)
                }
                R.styleable.CustomView_endSize -> {
                    endSize = ta.getDimensionPixelSize(attr, 0)
                }
            }
        }

        ta.recycle()

        addView(textView)

        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        figures.forEach { drawFigure(canvas, it) }
    }

    private fun drawFigure(canvas: Canvas, figure: DrawInfo) {
        when(figure) {
            is DrawInfo.CircleDrawInfo -> {
                drawCircle(canvas, figure.cx, figure.cy, figure.size, figure.color)
            }
            is DrawInfo.RectDrawInfo -> {
                drawRect(canvas, figure.cx, figure.cy, figure.size, figure.color)
            }
            is DrawInfo.RoundRectDrawInfo -> {
                drawRoundRect(canvas, figure.cx, figure.cy, 7f, 7f, figure.size, figure.color)
            }
        }
    }

    private fun generateRandomFigure(): DrawInfo {
        val figureType = Random.nextInt(0, 3)
        return when(figureType) {
            0 -> DrawInfo.CircleDrawInfo(lastTouchX, lastTouchY, randomSize.toFloat(), randomColor)
            1 -> DrawInfo.RectDrawInfo(lastTouchX, lastTouchY, randomSize.toFloat(), randomColor)
            2 -> DrawInfo.RoundRectDrawInfo(lastTouchX, lastTouchY, randomSize.toFloat(), randomColor)
            else -> throw Exception("Invalid figure type")
        }
    }

    private fun drawCircle(canvas: Canvas, cx: Float, cy: Float, radius: Float, color: Int) {
        figurePaint.color = color
        canvas.drawCircle(cx, cy, radius, figurePaint)
    }

    private fun drawRect(canvas: Canvas, cx: Float, cy: Float, size: Float, color: Int) {
        figurePaint.color = color
        val halfSize = size / 2
        canvas.drawRect(
            cx - halfSize,
            cy - halfSize,
            cx + halfSize,
            cy + halfSize,
            figurePaint
        )
    }

    private fun drawRoundRect(canvas: Canvas, cx: Float, cy: Float, rx: Float, ry: Float, size: Float, color: Int) {
        figurePaint.color = color
        val halfSize = size / 2
        canvas.drawRoundRect(
            cx - halfSize,
            cy - halfSize,
            cx + halfSize,
            cy + halfSize,
            rx,
            ry,
            figurePaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val superResult = super.onTouchEvent(event)
        event ?: return superResult

        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (figures.size >= 10) {
                    figures.clear()
                    textView.text = "0"
                    invalidate()
                    return superResult
                }

                lastTouchX = event.x
                lastTouchY = event.y
                val figure = generateRandomFigure()
                figures.add(figure)
                textView.text = "${figures.size}"
                invalidate()
            }
        }

        return superResult
    }


    private sealed class DrawInfo(
        val cx: Float,
        val cy: Float,
        val size: Float,
        val color: Int
    ) {
        class CircleDrawInfo(
            cx: Float,
            cy: Float,
            size: Float,
            color: Int
        ): DrawInfo(cx, cy, size, color)

        class RectDrawInfo(
            cx: Float,
            cy: Float,
            size: Float,
            color: Int
        ): DrawInfo(cx, cy, size, color)

        class RoundRectDrawInfo(
            cx: Float,
            cy: Float,
            size: Float,
            color: Int
        ): DrawInfo(cx, cy, size, color)
    }



}