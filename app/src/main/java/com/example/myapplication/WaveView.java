package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class WaveView extends View {

    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    //宽高
    private float mWidth = 0, mHeight = 0;
    //网格画笔
    private Paint mLinePaint;
    //图例矩形画笔
    private Paint mRectPaint;
    //文字画笔
    private Paint mTextPaint;
    //数据线画笔
    private Paint[] mWavePaint = new Paint[3];
    //线条的路径
    private Path mPath;
    //保存已绘制的数据坐标
    private float[][] dataArray;
    //数据最大值，默认-20~20之间
    private float MAX_VALUE = 20;
    //线条粗细
    private float WAVE_LINE_STROKE_WIDTH = 3;
    //波形颜色
    private int[] waveLineColor = {Color.rgb(255, 0, 0), Color.rgb(0, 255, 0), Color.rgb(0, 0, 255)};
    //当前的x，y坐标
    private float nowX, nowY;
    private float startY;

    //线条的长度，可用于控制横坐标
    private int WAVE_LINE_WIDTH = 2;
    //数据点的数量
    private int row;
    private int draw_index;
    private boolean isRefresh;
    //常规模式下，需要一次绘制的点的数量
    private int draw_point_length;
    //网格是否可见
    private boolean gridVisible;
    //网格的宽高
    private final int GRID_WIDTH = 50;
    //网格的横线和竖线的数量
    private int gridHorizontalNum, gridVerticalNum;
    //网格线条的粗细
    private final int GRID_LINE_WIDTH = 2;
    //网格颜色
    private int gridLineColor = Color.parseColor("#1b4200");
    //图表题目
    private String waveTitle = null;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @SuppressLint("ResourceAsColor")
    private void init(AttributeSet attrs) {
        MAX_VALUE = attrs.getAttributeIntValue(NAMESPACE, "max_value", 20);
        WAVE_LINE_STROKE_WIDTH = attrs.getAttributeIntValue(NAMESPACE, "wave_line_stroke_width", 3);
        gridVisible = attrs.getAttributeBooleanValue(NAMESPACE, "grid_visible", true);
        waveTitle = attrs.getAttributeValue(NAMESPACE, "wave_title");


        String wave_line_color = attrs.getAttributeValue(NAMESPACE, "wave_line_color");
        if (wave_line_color != null && !wave_line_color.isEmpty()) {
            for (int j = 0; j < 3; j++) {
                waveLineColor[j] = Color.parseColor(wave_line_color);
            }
        }

        String grid_line_color = attrs.getAttributeValue(NAMESPACE, "grid_line_color");
        if (grid_line_color != null && !grid_line_color.isEmpty()) {
            gridLineColor = Color.parseColor(grid_line_color);
        }

        String wave_background = attrs.getAttributeValue(NAMESPACE, "wave_background");
        if (wave_background != null && !wave_background.isEmpty()) {
            setBackgroundColor(Color.parseColor(wave_background));
        }


        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(GRID_LINE_WIDTH);
        /** 抗锯齿效果*/
        mLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(50);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.WHITE);
        //mRectPaint.setStyle(Paint.Style.FILL);

        for (int j = 0; j < 3; j++) {
            mWavePaint[j] = new Paint();
            mWavePaint[j].setStyle(Paint.Style.STROKE);
            mWavePaint[j].setColor(waveLineColor[j]);
            mWavePaint[j].setStrokeWidth(WAVE_LINE_STROKE_WIDTH);
            mWavePaint[j].setAntiAlias(true);
        }
        mPath = new Path();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        gridHorizontalNum = (int) (mHeight / GRID_WIDTH);
        gridVerticalNum = (int) (mWidth / GRID_WIDTH);
        row = 200;//(int) (mWidth / WAVE_LINE_WIDTH);
        WAVE_LINE_WIDTH = (int) mWidth / 200 + 1;
        dataArray = new float[3][row];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gridVisible) {
            drawGrid(canvas);
        }
        drawWaveLineNormal(canvas);
        draw_index += 1;
        if (draw_index >= row) {
            draw_index = 0;
        }
    }

    //常规模式绘制折线
    //@param canvas
    private void drawWaveLineNormal(Canvas canvas) {
        drawPathFromDatas(canvas, 0, row - 1);
        for (int j = 0; j < 3; j++) {
            if (row - draw_point_length >= 0)
                System.arraycopy(dataArray[j], 0 + draw_point_length, dataArray[j], 0, row - draw_point_length);
        }
    }


    private void drawPathFromDatas(Canvas canvas, int start, int end) {
        String[] legends = {"x", "y", "z"};
        canvas.drawRect(mWidth - 150, 10, mWidth - 10, 150, mRectPaint);
        canvas.drawText(waveTitle, mWidth / 2, 50, mTextPaint);
        for (int j = 1; j <= 3; j++) {
            mPath.reset();
            float X = mWidth - 80;
            float Y = j * 50 - 20;
            mPath.moveTo(X, Y);
            mPath.lineTo(X + 50, Y);
            canvas.drawPath(mPath, mWavePaint[j - 1]);
            canvas.drawText(legends[j - 1], X - 50, Y + 10, mTextPaint);
        }

        for (int j = 0; j < 3; j++) {
            mPath.reset();
            startY = mHeight / 2 - dataArray[j][start] * (mHeight / (MAX_VALUE * 2)) + 50;
            mPath.moveTo(end * WAVE_LINE_WIDTH, startY);
            for (int i = end; i > start - 1; i--) {
                if (isRefresh) {
                    isRefresh = false;
                    return;
                }

                nowX = mWidth - (199 - i) * WAVE_LINE_WIDTH;
                if (nowX < 0) {
                    break;
                }

                float dataValue = dataArray[j][i];
                /** 判断数据为正数还是负数  超过最大值的数据按最大值来绘制*/
                if (dataValue > 0) {
                    if (dataValue > MAX_VALUE) {
                        dataValue = MAX_VALUE;
                    }
                } else {
                    if (dataValue < -MAX_VALUE) {
                        dataValue = -MAX_VALUE;
                    }
                }
                nowY = mHeight / 2 - dataValue * (mHeight / (MAX_VALUE * 2)) + 50;
                mPath.lineTo(nowX, nowY);
            }
            canvas.drawPath(mPath, mWavePaint[j]);
        }
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        /** 设置颜色*/
        mLinePaint.setColor(gridLineColor);
        /** 绘制横线*/
        for (int i = 0; i < gridHorizontalNum + 1; i++) {
            canvas.drawLine(0, i * GRID_WIDTH,
                    mWidth, i * GRID_WIDTH, mLinePaint);
        }
        /** 绘制竖线*/
        for (int i = 0; i < gridVerticalNum + 1; i++) {
            canvas.drawLine(i * GRID_WIDTH, 0,
                    i * GRID_WIDTH, mHeight, mLinePaint);
        }
    }

    //添加新的数据
    public void addDataArray(float[] line) {
        /** 常规模式数据添加至最后一位*/
        draw_point_length = 1;
        for (int j = 0; j < 3; j++) {
            dataArray[j][row - 1] = line[j];
        }
        postInvalidate();
    }

    public float[][] getDataArray() {
        return dataArray;
    }

    public void clearDataArray() {
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < row; i++) {
                dataArray[j][i] = 0;
            }
        }
        postInvalidate();
    }

    public WaveView setMaxValue(int max_value) {
        this.MAX_VALUE = max_value;
        return this;
    }

    public WaveView setWaveLineColor(String[] colorString) {
        for (int j = 0; j < 3; j++) {
            this.waveLineColor[j] = Color.parseColor(colorString[j]);
        }
        return this;
    }

    public WaveView setGridVisible(boolean visible) {
        this.gridVisible = visible;
        return this;
    }

    public WaveView setGridLineColor(String colorString) {
        this.gridLineColor = Color.parseColor(colorString);
        return this;
    }

    public WaveView setWaveBackground(String colorString) {
        setBackgroundColor(Color.parseColor(colorString));
        return this;
    }


    public void setWaveLineWidth(int width) {
        this.WAVE_LINE_WIDTH = width;
    }
}