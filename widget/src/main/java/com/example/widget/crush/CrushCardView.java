package com.example.widget.crush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ex-huangzhiyi001
 * @date 2019/1/12
 */
@SuppressWarnings("ALL")
public class CrushCardView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "CrushCardView";
    private static final long RATE = 1000 / 60; //millsecond
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mBitmap;
    private Paint mPaint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private boolean isCreated = false;
    private Thread mThread;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private Future<?> mSubmit;
    private Matrix mMatrix;
    private ArrayList<Integer> mFirstPath;
    private final int mDrawLoop = 5;
    private int mCurrDrawLoop = 0;
    private int pathInteger = 0;
    private Iterator<Integer> mFirstIterator;

    public CrushCardView (Context context) {
        this(context, null);
    }

    public CrushCardView (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrushCardView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init () {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);/* 设置画布背景透明 */
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMatrix = new Matrix();

    }

    private void initData () {
        mFirstPath = new ArrayList<>();
        mFirstPath.add(-mBitmap.getWidth()/2);
        for (int i = 0; i < 101; i++){
            if (i % 2 == 0){
                mFirstPath.add(mBitmap.getWidth() * 3/2);
            }else {
                mFirstPath.add(-mBitmap.getWidth() * 3/2);
            }

        }
        mFirstPath.add(-mBitmap.getWidth()/2);

    }

    public void setCrushBitmap (Bitmap bitmap) {
        mBitmap = bitmap;
        if (isCreated){
            initStaticContent();
        }
    }

    /**
     * after surfaceview oncreated to invocate
     */
    private void initStaticContent () {
        if (mBitmap == null){
            return;
        }
        initData();

        int w = mMeasuredWidth - mBitmap.getWidth();
        int h = mMeasuredHeight - mBitmap.getHeight();
        Rect rect = new Rect(w / 2, h / 2,
                w / 2 + mBitmap.getWidth(), h / 2 + mBitmap.getHeight());
        //Log.e(TAG, "initStaticContent: rect " + rect);
        Canvas canvas = mSurfaceHolder.lockCanvas(rect);
        canvas.drawBitmap(mBitmap, w / 2, h / 2, mPaint);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void startAnimation () {
        addAnimation();
        if (mSubmit != null && !mSubmit.isDone()){
            return;
        }
        mSubmit = mExecutorService.submit(this);
    }

    private void addAnimation () {
        Log.e(TAG, "addAnimation: start ");
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        if (mMeasuredWidth == 0 || mMeasuredHeight == 0){
            mMeasuredWidth = 600;
            mMeasuredHeight = 800;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        isCreated = true;
        initStaticContent();
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: ");
        isCreated = false;
    }

    @Override
    public void run () {
        Canvas canvas;
        mFirstIterator = mFirstPath.iterator();
        int condition = 0;//阶段绘画标志, -1表示绘制完毕
        while (isCreated){
            long diffT = 0L;
            if (condition != -1){
                long currT = System.currentTimeMillis();
//                if (isHardwareAccelerated()){
//                    //硬件加速
//                    canvas = mSurfaceHolder.lockHardwareCanvas();
//                } else{
//                    canvas = mSurfaceHolder.lockCanvas();
//                }
                canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null){
                    Log.e(TAG, "run: drawing canvas is null");
                    break;
                }
                //绘画
                condition = dispatchToDraw(canvas, condition);

                diffT = System.currentTimeMillis() - currT;
            } else{
                break;
            }
            try{
                if (diffT < RATE){
                    Thread.sleep(RATE - diffT);
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private int dispatchToDraw (Canvas canvas, int condition) {
        int flag = -1;
        switch (condition){
        case 0:
            //开始
            Log.e(TAG, "dispatchToDraw: 1" );
            flag = drawFirst(canvas) ? 1 : 0;
            break;
        case 1:
            drawTwo(canvas);
            flag = 2;
            break;
        case 2:
            drawThree(canvas);
            flag = 3;
            break;
        case 3:
            drawEnd(canvas);
            flag = -1;
            break;
        default:
            break;
        }
        return flag;
    }

    private void drawEnd (Canvas canvas) {

    }

    private void drawThree (Canvas canvas) {

    }

    private void drawTwo (Canvas canvas) {

    }

    /**
     *
     * @param canvas
     * @return true 表示绘制完成，继续下一个绘制
     */
    private boolean drawFirst (Canvas canvas) {
        boolean flag = false;
        if (mCurrDrawLoop == 0){
            if (mFirstIterator.hasNext()){
                pathInteger = mFirstIterator.next();
            }else {
                mMatrix.reset();
                return true;
            }
        }
        Log.e(TAG, "drawFirst: pathInteger "+pathInteger );
        //左右抖动
        mMatrix.preTranslate(pathInteger*(mCurrDrawLoop/mDrawLoop), 0);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        if (mCurrDrawLoop > mDrawLoop){
            mCurrDrawLoop = 0;
        }else {
            mCurrDrawLoop++;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public interface CrushInterface {
    }
}
