package com.asiainfo.wheelsurf.wheelsurfexample.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * SurfaceView扩展类
 */

public class SurfaceTempalte extends SurfaceView implements SurfaceHolder.Callback,Runnable {

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
     * @param context
     */
    private boolean isRunning;

    public SurfaceTempalte(Context context) {
        this(context, null);
    }

    /**
     * 在SurfaceTempalte 构造 里面初始化Holder,设置我们常用的设置
     * @param context
     * @param attrs
     */

    public SurfaceTempalte(Context context, AttributeSet attrs) {
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
     * @param holde
     */

    @Override
    public void surfaceCreated(SurfaceHolder holde) {

        isRunning = true;

        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 在 surfaceDestroyed 关闭我们的线程
     * @param holder
     */

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        isRunning = false ;
    }


    /**
     *在线程中进行我们真正的绘制操作
     */

    @Override
    public void run() {

        while (isRunning){

            draw();
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder . lockCanvas();

            if (mCanvas !=  null){
                //draw something
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (mCanvas!=null){

                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

}
