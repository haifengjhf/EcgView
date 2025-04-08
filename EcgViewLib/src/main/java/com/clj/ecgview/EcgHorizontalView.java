package com.clj.ecgview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class EcgHorizontalView extends View {
    private final static String TAG = "EcgHorizontalView";
    private Paint mPaint;
    private List<Integer> mDataList = new ArrayList<>();

    private int minGridNum;
    private boolean littleGrid;
    private int backgroundColor;
    private int gridColor;
    private int littleGridColor;
    private int lineColor;
    private int totalSize;
    private int startColumn;
    private float mvData;
    private int littleGridPxNum;

    public EcgHorizontalView(Context context) {
        this(context, null);
    }

    public EcgHorizontalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EcgHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EcgView, defStyleAttr, 0);

        minGridNum = a.getInt(R.styleable.EcgHorizontalView_ecghLittleGridNum, 5);
        littleGridPxNum = a.getInt(R.styleable.EcgHorizontalView_ecghLittleGridPxSize,12);
        littleGrid = a.getBoolean(R.styleable.EcgHorizontalView_ecghLittleGrid, true);
        backgroundColor = a.getInt(R.styleable.EcgHorizontalView_ecghBackgroundColor, Color.WHITE);
        gridColor = a.getInt(R.styleable.EcgHorizontalView_ecghGridColor, Color.RED);
        littleGridColor = a.getInt(R.styleable.EcgHorizontalView_ecghLittleGridColor, Color.MAGENTA);
        lineColor = a.getInt(R.styleable.EcgHorizontalView_ecghLineColor, Color.BLACK);
        startColumn = a.getInt(R.styleable.EcgHorizontalView_ecghStartColumn, 0);
        mvData = a.getFloat(R.styleable.EcgHorizontalView_ecghMvData, 4.25f);
        a.recycle();

        Log.d(TAG,"startColumn:" + startColumn);
        init();
     }

    private void init() {
        mDataList = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();

        // 去除padding之后，图片可用的宽高范围
        int imageWidth = getWidth() - paddingLeft - paddingRight;
        int imageHeight = getHeight() - paddingTop - paddingBottom;

        // 锁定宽高比之后，图片最终显示的宽和高
        int actualWidth = imageWidth;
        int actualHeight = imageHeight;

        int largeGridPxNum = minGridNum * littleGridPxNum;

        // 画布是白色的
        canvas.drawColor(backgroundColor);

        if (littleGrid) {
            //绘制小网格（浅红色）
            mPaint.setColor(littleGridColor);
            // 每隔littleGridPxNum个像素画一条横线
            for (int i = 0; i < actualHeight; i += littleGridPxNum) {
                canvas.drawLine(paddingLeft + 0, paddingTop + i,
                        paddingLeft + actualWidth, paddingTop + i, mPaint);
            }

            // 每隔littleGridPxNum个像素画一条竖线
            for (int i = 0; i < actualWidth; i += littleGridPxNum) {
                canvas.drawLine(paddingLeft + i, paddingTop + 0,
                        paddingLeft + i, paddingTop + actualHeight, mPaint);
            }
        }

        //绘制大网格（红色）
        mPaint.setColor(gridColor);
        // 每隔mLargeGridPxNum个像素画一条横线
        for (int i = 0; i <= actualHeight; i += largeGridPxNum) {
            canvas.drawLine(paddingLeft + 0, paddingTop + i,
                    paddingLeft + actualWidth, paddingTop + i, mPaint);
        }

        // 每隔mLargeGridPxNum个像素画一条竖线
        for (int i = 0; i <= actualWidth; i += largeGridPxNum) {
            canvas.drawLine(paddingLeft + i, paddingTop + 0,
                    paddingLeft + i, paddingTop + actualHeight, mPaint);
        }

        // 开始绘制数据曲线（黑色）
        if (mDataList == null || mDataList.size() < 1)
            return;

        mPaint.setColor(lineColor);

        // 数据源总长度
        int length = mDataList.size();

        float unitPointPxNum = 2 * littleGridPxNum;
        // 第一列的起始数据源位置
        int firstLineStartPosition = 0;
//        // 第二列的起始数据源位置
        int secondLineStartPosition = (int)((actualWidth - startColumn * largeGridPxNum)/unitPointPxNum);
        totalSize = secondLineStartPosition;
        Log.d(TAG,"secondLineStartPosition:" + secondLineStartPosition + " actualWidth:" + actualWidth + " littleGridPxNum:" + littleGridPxNum);

        // 绘制第一行
        for (int i = firstLineStartPosition; i <= secondLineStartPosition && i < length - 1 ; i++) {
            // 计算第该点和横坐标和纵坐标（从下往上描点）
            float datamv = mDataList.get(i); // 将测量值转化为十进制，实际的MV数再除以42.5(这属于心电图的一个标准)
            float pointx =  paddingLeft + startColumn * largeGridPxNum + i * unitPointPxNum;  // 计算横坐标
            float pointy = paddingTop + (float) actualHeight /2 - (datamv / mvData) * littleGridPxNum ;   // 计算纵坐标,居中显示



            //然后计算该点相邻的后面那个点的坐标
            float datamv_next = mDataList.get(i + 1);
            float pointx_next = paddingLeft + startColumn * largeGridPxNum +  (i +1) * unitPointPxNum;
            float pointy_next = paddingTop + (float) actualHeight /2 - (datamv_next / mvData) * littleGridPxNum;

            // 连接这两个点
            canvas.drawLine(pointx, pointy, pointx_next, pointy_next, mPaint);
        }

    }


    public List<Integer> getDataList() {
        return mDataList;
    }

    public void setDataList(List<Integer> dataList) {
        this.mDataList = dataList;
        invalidate();
    }

    public void appendData(List<Integer> dataList){
        int size = dataList.size();
        if(mDataList.size() + size <= totalSize){
            mDataList.addAll(dataList);
        }else{
            mDataList.clear();
            mDataList.addAll(dataList);
        }
        invalidate();
    }

}
