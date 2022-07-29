package com.suhel.gameoflife

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.floor

class GameScreen @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var isEditable = false

    private val grid: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val squareBlank: Paint
    private val squareFilled: Paint
    private val data: Array<Array<BooleanArray>>
    private var currentSwapIndex = 0
    private var oldX = 0
    private var oldY = 0
    private var width = 0f
    private var height = 0f
    private var squareWidth = 0f
    private var squareHeight = 0f

    init {
        grid.color = -0x111112
        grid.style = Paint.Style.STROKE
        squareBlank = Paint(Paint.ANTI_ALIAS_FLAG)
        squareBlank.color = Color.WHITE
        squareBlank.style = Paint.Style.FILL
        squareFilled = Paint(Paint.ANTI_ALIAS_FLAG)
        squareFilled.color = Color.BLACK
        squareFilled.style = Paint.Style.FILL
        data = Array(SWAP_SIZE) { Array(SIZE) { BooleanArray(SIZE) } }
    }

    private fun calcPosX(x: Int): Int = (squareWidth * x).toInt()
    private fun calcPosY(y: Int): Int = (squareHeight * y).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        height = width.coerceAtMost(height)
        width = height
        squareWidth = width / SIZE
        squareHeight = height / SIZE
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until SIZE) for (j in 0 until SIZE) canvas.drawRect(
            calcPosX(i).toFloat(),
            calcPosY(j).toFloat(),
            calcPosX(i) + squareWidth,
            calcPosY(j) + squareHeight,
            if (data[currentSwapIndex][i][j]) squareFilled else squareBlank
        )

        // Drawing the grid
        for (i in 0..SIZE) canvas.drawLine(
            calcPosX(i).toFloat(),
            0f,
            calcPosX(i).toFloat(),
            height,
            grid
        )
        for (i in 0..SIZE) canvas.drawLine(
            0f,
            calcPosY(i).toFloat(),
            width,
            calcPosY(i).toFloat(),
            grid
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val squareX = floor((event.x / squareWidth).toDouble()).toInt()
        val squareY = floor((event.y / squareHeight).toDouble()).toInt()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                oldX = squareX
                oldY = squareY
            }
            MotionEvent.ACTION_MOVE -> {
                drawLine(oldX, oldY, squareX, squareY)
                oldX = squareX
                oldY = squareY
            }
        }
        return isEditable
    }

    fun nextFrame() {
        for (x in 0 until SIZE) {
            for (y in 0 until SIZE) {
                data[nextSwapIndex][x][y] = shouldLive(x, y)
            }
        }
        incrementSwapIndex()
        invalidate()
    }

    private fun getSquare(x: Int, y: Int): Boolean {
        return x in 0 until SIZE && y >= 0 && y < SIZE && data[currentSwapIndex][x][y]
    }

    private fun getLiveNeighbourCount(x: Int, y: Int): Int {
        var c = 0
        c += if (getSquare(x - 1, y - 1)) 1 else 0
        c += if (getSquare(x, y - 1)) 1 else 0
        c += if (getSquare(x + 1, y - 1)) 1 else 0
        c += if (getSquare(x - 1, y)) 1 else 0
        c += if (getSquare(x + 1, y)) 1 else 0
        c += if (getSquare(x - 1, y + 1)) 1 else 0
        c += if (getSquare(x, y + 1)) 1 else 0
        c += if (getSquare(x + 1, y + 1)) 1 else 0
        return c
    }

    private fun shouldLive(x: Int, y: Int): Boolean {
        val isLiving = getSquare(x, y)
        val count = getLiveNeighbourCount(x, y)
        return if (isLiving) !(count < 2 || count > 3) else count == 3
    }

    private val nextSwapIndex: Int
        get() = (currentSwapIndex + 1) % SWAP_SIZE

    private fun incrementSwapIndex() {
        currentSwapIndex = nextSwapIndex
    }

    fun clear() {
        for (i in 0 until SIZE) for (j in 0 until SIZE) data[currentSwapIndex][i][j] = false
        invalidate()
    }

    // Bresenham's algorithm
    private fun drawLine(x0: Int, y0: Int, x1: Int, y1: Int) {
        if (abs(y1 - y0) < abs(x1 - x0)) {
            if (x0 > x1) drawLineLow(x1, y1, x0, y0) else drawLineLow(x0, y0, x1, y1)
        } else {
            if (y0 > y1) drawLineHigh(x1, y1, x0, y0) else drawLineHigh(x0, y0, x1, y1)
        }
        invalidate()
    }

    private fun drawLineHigh(x0: Int, y0: Int, x1: Int, y1: Int) {
        var dx = x1 - x0
        val dy = y1 - y0
        var xi = 1
        if (dx < 0) {
            xi = -1
            dx = -dx
        }
        var D = 2 * dx - dy
        var x = x0
        for (y in y0..y1) {
            plot(x, y)
            if (D > 0) {
                x += xi
                D -= 2 * dy
            }
            D += 2 * dx
        }
    }

    private fun drawLineLow(x0: Int, y0: Int, x1: Int, y1: Int) {
        val dx = x1 - x0
        var dy = y1 - y0
        var yi = 1
        if (dy < 0) {
            yi = -1
            dy = -dy
        }
        var D = 2 * dy - dx
        var y = y0
        for (x in x0..x1) {
            plot(x, y)
            if (D > 0) {
                y += yi
                D -= 2 * dx
            }
            D += 2 * dy
        }
    }

    private fun plot(x: Int, y: Int) {
        if (x in 0 until SIZE && y >= 0 && y < SIZE) data[currentSwapIndex][x][y] = true
    }

    companion object {
        private const val SIZE = 64
        private const val SWAP_SIZE = 4
    }
}