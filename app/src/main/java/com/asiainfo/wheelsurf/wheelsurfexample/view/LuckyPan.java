package com.asiainfo.wheelsurf.wheelsurfexample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.asiainfo.wheelsurf.wheelsurfexample.R;

import static android.graphics.BitmapFactory.decodeResource;

/**
 * SurfaceView扩展类
 */

public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    /**
     * 用于绘制的线程
     *
     * @param context
     */
    private Thread mThread;

    /**
     * 线程的控制开关
     *
     * @param context
     */
    private boolean isRunning;

    /**
     * 盘快的奖项
     */
    private String[] mtrStrs = new String[]{"SonyCamera", "MacAir2", "恭喜发财", "iPhone7Plus", "Burberry风衣", "再接再厉!"};

    /**
     * 奖项的图片
     */
    private int[] mImags = new int[]{R.drawable.pan_camera, R.drawable.pan_ipad, R.drawable.pan_congratulation, R.drawable.pan_iphone, R.drawable.pan_beauty, R.drawable.pan_misslucky};


    /**
     * 与图片对应的Bitmap数组
     */
    private Bitmap mImageBitmap[];

    private Bitmap mBgBitmap = decodeResource(getResources(),
            R.drawable.pan_center);

    /**
     * 盘快的颜色
     */

    private int[] mColors = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01};

    private int mItemCount = 6;


    /**
     * 绘制文本的画笔
     *
     * @param context
     */
    private Paint mTextPaint;

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    /**
     * 整个盘快的范围
     */
    private RectF mRange = new RectF();

    /**
     * 整个盘快的直径
     *
     * @param context
     */
    private int mRadius;

    /**
     * 绘制盘快的画笔
     *
     * @param context
     */
    private Paint mArcPaint;

    /**
     * 盘快滚动的速度
     *
     * @param context
     */
    private double mSpeed;

    /**
     * 盘快的速度
     *
     * @param context
     */
    private volatile float mStartAngle = 0;

    /**
     * 判断是否点击了停止按钮
     */
    private boolean isShouldEnd;

    /**
     * 转盘的中心位置
     */
    private int mCenter;

    /**
     * 这里我们的padding直接已paddingLeft为准
     *
     * @param context
     */
    private int mPadding;

    public LuckyPan(Context context) {
        this(context, null);
    }

    /**
     * 在SurfaceTempalte 构造 里面初始化Holder,设置我们常用的设置
     */

    public LuckyPan(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();

        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
    }

    /**
     * 在surfaceCreated启动我们的线程
     */

    @Override
    public void surfaceCreated(SurfaceHolder holde) {

        //初始化盘快的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化盘快的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        //初始化盘快绘制的范围
        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);

        //初始化图片
        mImageBitmap = new Bitmap[mItemCount];

        for (int i = 0; i < mItemCount; i++) {
            mImageBitmap[i] = BitmapFactory.decodeResource(getResources(), mImags[i]);
        }
        isRunning = true;

        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 在 surfaceDestroyed 关闭我们的线程
     */

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        isRunning = false;
    }


    /**
     * 在线程中进行我们真正的绘制操作
     */

    @Override
    public void run() {

        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();

            if (mCanvas != null) {
                //draw something
                drawBg();

                //绘制盘快

                float tmpAngle = mStartAngle;

                float sweepAngle = 360 / mItemCount;

                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);
                    //绘制盘快
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);

                    //绘制文本
                    drawText(tmpAngle, sweepAngle, mtrStrs[i]);

                    //绘制Icon图标
                    drawIcon(tmpAngle, mImageBitmap[i]);

                    tmpAngle += sweepAngle;

                }

                mStartAngle += mSpeed;

                //如果点击了停止按钮
                if (isShouldEnd) {
                    mSpeed -= 1;
                }

                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (mCanvas != null) {

                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 点击启动旋转
     */
    public void luckyStart(int index) {

        //计算每一项的角度
        int angle = 360 / mItemCount;

        //计算每一项中奖的范围(当前index)
        //0--->150~210
        //0--->210~270

        float from = 270 - (index + 1) * angle;
        float end = from + angle;

        //设置停下来需要旋转的距离
        float targetFrom = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        /**
         * <pre>
         *    v1-->0 每次速度减1
         *    100 + 1
         *    (v1 + 0)*(v1+1)/2
         *    v1 = (-1 + Math.sqrt(1+8*targetFrom))/2
         *
         * </pre>
         */
        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

        mSpeed = v1 + Math.random() * (v2 - v1); //永远是ipad,可以根据索引随机抽选自己的奖品
        //mSpeed = v1;   //停在MacAri2边缘线
        //mSpeed = 50; //随机抽奖
        isShouldEnd = false;
    }

    /**
     * 点击停止旋转
     */
    public void luckyEnd() {
        mStartAngle = 0;
        isShouldEnd = true;
    }

    /**
     * 转盘是否在旋转
     */
    public boolean isStart() {

        return mSpeed != 0;
    }

    /**
     * 停止按钮是否按下
     */
    public boolean isShouldEnd() {

        return isShouldEnd;
    }

    /**
     * 绘制Icon
     */
    private void drawIcon(float tmpAngle, Bitmap bitmap) {

        //设置图片的宽度为直径的1/8
        int imgWidth = mRadius / 8;

        //Math.PI/180
        float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        //确定图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);

        mCanvas.drawBitmap(bitmap, null, rect, null);

    }

    /**
     * 绘制每个盘快的文本
     */
    private void drawText(float tmpAngle, float sweepAngle, String mtrStr) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        //利用水平偏移量将文字居中

        float textWidth = mTextPaint.measureText(mtrStr);
        int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);

        int vOffset = mRadius / 2 / 5;//垂直偏移量
        mCanvas.drawTextOnPath(mtrStr, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2,
                mPadding / 2, getMeasuredWidth() - mPadding / 2,
                getMeasuredWidth() - mPadding / 2), null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredWidth());
        //半径
        mRadius = width - getPaddingLeft() * 2;

        mPadding = getPaddingLeft();
        //中心点
        mCenter = width / 2;

        setMeasuredDimension(width, width);
    }
}
