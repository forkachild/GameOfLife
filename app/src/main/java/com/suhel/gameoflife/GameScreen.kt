package com.suhel.gameoflife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameScreen extends View {

    private static final int SIZE = 64;
    private static final int SWAP_SIZE = 4;
    private boolean isEditable = false;

    private Paint grid, squareBlank, squareFilled;

    private boolean[][][] data;
    private int swapIndex = 0;

    private int oldX = 0, oldY = 0;
    private float width, height, squareWidth, squareHeight;

    public GameScreen(Context context) {
        this(context, null);
    }

    public GameScreen(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameScreen(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        grid = new Paint(Paint.ANTI_ALIAS_FLAG);
        grid.setColor(0xFFEEEEEE);
        grid.setStyle(Paint.Style.STROKE);

        squareBlank = new Paint(Paint.ANTI_ALIAS_FLAG);
        squareBlank.setColor(Color.WHITE);
        squareBlank.setStyle(Paint.Style.FILL);

        squareFilled = new Paint(Paint.ANTI_ALIAS_FLAG);
        squareFilled.setColor(Color.BLACK);
        squareFilled.setStyle(Paint.Style.FILL);

        data = new boolean[SWAP_SIZE][SIZE][SIZE];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        width = height = width < height ? width : height;

        squareWidth = width / SIZE;
        squareHeight = height / SIZE;

        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                canvas.drawRect(getPosX(i), getPosY(j), getPosX(i) + squareWidth,
                        getPosY(j) + squareHeight, data[getCurrentSwapIndex()][i][j] ? squareFilled : squareBlank);

        // Drawing the grid
        for (int i = 0; i <= SIZE; i++)
            canvas.drawLine(getPosX(i), 0, getPosX(i), height, grid);

        for (int i = 0; i <= SIZE; i++)
            canvas.drawLine(0, getPosY(i), width, getPosY(i), grid);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int squareX = (int) Math.floor(event.getX() / squareWidth);
        int squareY = (int) Math.floor(event.getY() / squareHeight);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                oldX = squareX;
                oldY = squareY;
                break;

            case MotionEvent.ACTION_MOVE:

                drawLine(oldX, oldY, squareX, squareY);

                oldX = squareX;
                oldY = squareY;
                break;

        }
        return isEditable;
    }

    public void nextFrame() {
        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                data[getNextSwapIndex()][x][y] = shouldLive(x, y);
        incrementSwapIndex();
        invalidate();
    }

    private int getPosX(int x) {
        return (int) (squareWidth * x);
    }

    private int getPosY(int y) {
        return (int) (squareHeight * y);
    }

    public void setSquare(int swapIndex, int x, int y, boolean value) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            data[swapIndex][x][y] = value;
            invalidate();
        }
    }

    public boolean getSquare(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && data[getCurrentSwapIndex()][x][y];
    }

    private int getLiveNeighbourCount(int x, int y) {
        int c = 0;
        c += getSquare(x - 1, y - 1) ? 1 : 0;
        c += getSquare(x, y - 1) ? 1 : 0;
        c += getSquare(x + 1, y - 1) ? 1 : 0;

        c += getSquare(x - 1, y) ? 1 : 0;
        c += getSquare(x + 1, y) ? 1 : 0;

        c += getSquare(x - 1, y + 1) ? 1 : 0;
        c += getSquare(x, y + 1) ? 1 : 0;
        c += getSquare(x + 1, y + 1) ? 1 : 0;
        return c;
    }

    private boolean shouldLive(int x, int y) {
        boolean isLiving = getSquare(x, y);
        int count = getLiveNeighbourCount(x, y);

        if (isLiving)
            return !(count < 2 || count > 3);
        else
            return count == 3;
    }

    private int getCurrentSwapIndex() {
        return swapIndex;
    }

    private int getNextSwapIndex() {
        return (swapIndex + 1) % SWAP_SIZE;
    }

    private void incrementSwapIndex() {
        swapIndex = getNextSwapIndex();
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void clear() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                data[getCurrentSwapIndex()][i][j] = false;
        invalidate();
    }

    // Bresenham's algorithm
    private void drawLine(int x0, int y0, int x1, int y1) {
        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1)
                drawLineLow(x1, y1, x0, y0);
            else
                drawLineLow(x0, y0, x1, y1);
        } else {
            if (y0 > y1)
                drawLineHigh(x1, y1, x0, y0);
            else
                drawLineHigh(x0, y0, x1, y1);
        }
        invalidate();
    }

    private void drawLineHigh(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int D = 2 * dx - dy;
        int x = x0;

        for (int y = y0; y <= y1; y++) {
            plot(x, y);
            if (D > 0) {
                x = x + xi;
                D = D - 2 * dy;
            }
            D = D + 2 * dx;
        }
    }

    private void drawLineLow(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int D = 2 * dy - dx;
        int y = y0;

        for (int x = x0; x <= x1; x++) {
            plot(x, y);
            if (D > 0) {
                y = y + yi;
                D = D - 2 * dx;
            }
            D = D + 2 * dy;
        }
    }

    private void plot(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE)
            data[getCurrentSwapIndex()][x][y] = true;
    }

}
