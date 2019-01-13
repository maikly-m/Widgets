package com.example.widget.crush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

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
//    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
//    private Future<?> mSubmit;
    private Matrix mMatrix;
    private int mBitmapLeft;
    private int mBitmapTop;
    private int mOffX = 0;//x偏移距离
    private int mPerOffX = 20;// 每次移动的距离
    private final int mFirstTempXMask = 15;
    private int mFirstTempX = 1 << mFirstTempXMask;//16bit 携带方向和位移信息,高位最前面是1表示方向向左，是0表示方向向右
    private int mTempXCycleCount = 0;
    private final int mXCycleCount = 10;//x循环总次数
    private int mScaleAllCount;//缩放次数
    private int mScaleCount = 0;//缩放次数
    private static final float g = 0.04f;// 二次函数系数
    private final int mCrushCount = 25;//炸开的次数
    private final int maxPerExplode = 25;//每次炸开时的最大值
    private int mTempCrushCount = 0;//次数
    private ArrayList<ArrayList<ExplodeBean>> mExplodeBeans = new ArrayList<>();//所有炸开的数据集合
    private Stack<ExplodeBean> mTempExplodeBeans = new Stack<>();//缓存
    private Random mRandom;
    private ArrayList<Integer>  mColorArrayList = new ArrayList<>();;


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
        mPaint.setColor(Color.WHITE);
        mMatrix = new Matrix();
        mRandom = new Random();

    }

    private void initData () {
        //添加小球颜色集合
        mColorArrayList.clear();
        //获取9个就够了
        int w = mBitmap.getWidth() / 3;
        int h = mBitmap.getHeight() / 3;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                int x = w * i + mRandom.nextInt(w);
                int y = h * j + mRandom.nextInt(h);
                int color = mBitmap.getPixel(x, y);
                mColorArrayList.add(color);
            }
        }
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
        mBitmapLeft = (mMeasuredWidth - mBitmap.getWidth()) / 2;
        mBitmapTop = (mMeasuredHeight - mBitmap.getHeight()) / 2;
        Rect rect = new Rect(mBitmapLeft, mBitmapTop,
                mBitmapLeft + mBitmap.getWidth(), mBitmapTop + mBitmap.getHeight());
        Canvas canvas = mSurfaceHolder.lockCanvas(rect);
        canvas.drawBitmap(mBitmap, mBitmapLeft, mBitmapTop, mPaint);
        mSurfaceHolder.unlockCanvasAndPost(canvas);

        initData();
    }

    public void startAnimation () {
        addAnimation();
        if (mThread != null && mThread.isAlive()){
            return;
        }
        mThread = new Thread(this);
        mThread.start();
//        if (mSubmit != null && !mSubmit.isDone()){
//            return;
//        }
//        mSubmit = mExecutorService.submit(this);
    }

    private void addAnimation () {
        //Log.e(TAG, "addAnimation: start ");
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
        //Log.e(TAG, "surfaceCreated: ");
        isCreated = true;
        initStaticContent();
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
        //Log.e(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        //Log.e(TAG, "surfaceDestroyed: ");
        isCreated = false;
    }

    @Override
    public void run () {
        Canvas canvas;
        mOffX = mBitmap.getWidth() / 5;
        mTempXCycleCount = 0;
        mFirstTempX = (1 << 15) | mOffX;//初始化
        mScaleCount = 0;
        mScaleAllCount = mBitmap.getWidth() / (2 * 6);//6px 缩放一次
        mTempCrushCount = 0;
        mExplodeBeans.clear();

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
                //清屏
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawPaint(mPaint);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
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
        Log.e(TAG, "run: over " );
    }

    private int dispatchToDraw (Canvas canvas, int condition) {
        int flag = -1;
        switch (condition){
        case 0:
            //开始
            flag = drawFirst(canvas) ? 1 : 0;
            break;
        case 1:
            flag = drawTwo(canvas) ? 2 : 1;
            break;
        case 2:
            flag = drawThree(canvas) ? 3 : 2;
            break;
        case 3:
            flag = drawEnd(canvas) ? -1 : 3;
            break;
        default:
            break;
        }
        return flag;
    }

    private boolean drawEnd (Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(60);
        canvas.drawText(" The End! ", 400, 600, mPaint);
        Log.e(TAG, "drawEnd: end " );
        return true;
    }

    /**
     * 炸开
     *
     * @param canvas
     * @return true 表示绘制完成，继续下一个绘制
     */
    private boolean drawThree (Canvas canvas) {
        // 炸开
        boolean flag = false;
        if (mTempCrushCount++ < mCrushCount){
            //添加数据
            int count = mRandom.nextInt(maxPerExplode);
            ArrayList<ExplodeBean> beans = new ArrayList<>();
            for (int i = 0; i < count; i++){
                //ExplodeBean explodeBean = getCacheExplodeBeans();
                ExplodeBean explodeBean = new ExplodeBean();
                explodeBean.theta = mRandom.nextInt(180);//theta值
                explodeBean.velocity = 1 + mRandom.nextFloat() * 8;//速度值
                explodeBean.color = mColorArrayList.get(mRandom.nextInt(9));
                explodeBean.size = 8 + mRandom.nextInt(15);
                explodeBean.time = System.currentTimeMillis();
                beans.add(explodeBean);
            }
            mExplodeBeans.add(beans);
        }
        if (mExplodeBeans.size() > 0){
            //绘制
            ExplodeBean explodeBean;
            long l;
            long diff;
            float velocityToY;
            float velocityToX;

            Iterator<ArrayList<ExplodeBean>> eIterator = mExplodeBeans.iterator();
            while (eIterator.hasNext()){
                ArrayList<ExplodeBean> explodeBeans = eIterator.next();
                if (explodeBeans.size() > 0){
                    //处理元素 是否越过边界
                    Iterator<ExplodeBean> bIterator = explodeBeans.iterator();
                    while (bIterator.hasNext()){
                        explodeBean = bIterator.next();
                        l = System.currentTimeMillis();
                        diff = l - explodeBean.time;
                        velocityToX = (float) (explodeBean.velocity * Math.cos(explodeBean.theta));
                        velocityToY = (float) (explodeBean.velocity * Math.sin(explodeBean.theta));
                        int[] position = calPosition(diff, velocityToX, velocityToY);
                        mPaint.setColor(explodeBean.color);
                    //Log.e(TAG, "drawThree: position[0] "+position[0]);
                    //Log.e(TAG, "drawThree: position[1] "+position[1]);
                        if (Math.abs(position[0]) > 1080 || Math.abs(position[1]) > 1920){
                            //移除
                            bIterator.remove();
                            //加入到缓存中,由于数据基本创建完毕了，后面回收的beans用不上...呃呃呃
                            //addCacheExplodeBeans(explodeBean);
                        }else {
                            canvas.drawCircle(position[0], position[1], explodeBean.size, mPaint);
                        }
                    }
                }
                if (explodeBeans.size() == 0){
                    //ExplodeBeans 为空，移除该容器
                    eIterator.remove();
                }
            }

        }else {
            //清理数据完毕
            mPaint.setColor(Color.WHITE);
            return true;
        }
        return flag;
    }

    /**
     * 加入缓存
     * @param explodeBean
     */
    private void addCacheExplodeBeans (ExplodeBean explodeBean) {
        mTempExplodeBeans.push(explodeBean);
    }

    /**
     * 从缓存中获取，没有就new一个
     * @return ExplodeBean
     */
    private ExplodeBean getCacheExplodeBeans (){
        ExplodeBean explodeBean;
        if (mTempExplodeBeans.empty()){
            explodeBean = new ExplodeBean();
        }else {
            explodeBean = mTempExplodeBeans.pop();
            if (explodeBean == null){
                explodeBean = new ExplodeBean();
            }else {
            }
        }
        return explodeBean;
    }

    /**
     * 计算抛物线的位置
     *
     * @param t millsecond
     * @param velocityToX
     * @param velocityToY
     */
    private int[] calPosition (long t, float velocityToX, float velocityToY) {
        t = t / 20;
        int[] p = new int[2];
        p[0] = (int) (velocityToX * t) + getMeasuredWidth()/2;
        //坐标倒过来
        p[1] = -(int) (velocityToY * t - t * t * g) + getMeasuredHeight()/2;
        return p;
    }

    /**
     * 缩放
     *
     * @param canvas
     * @return true 表示绘制完成，继续下一个绘制
     */
    private boolean drawTwo (Canvas canvas) {
        // 缩小
        boolean flag = false;
        if (++mScaleCount > mScaleAllCount){
            mMatrix.reset();
            return true;
        }
        mMatrix.reset();
        float scaleX = 1 - mScaleCount * 1f / mScaleAllCount;
        float scaleY = 1 - mScaleCount * 1f / mScaleAllCount;
        mMatrix.preTranslate(mBitmapLeft, mBitmapTop);
        mMatrix.postScale(scaleX, scaleY, mBitmapLeft + mBitmap.getWidth() / 2, mBitmapTop + mBitmap.getHeight() / 2);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        return flag;
    }

    /**
     * 左右晃动
     *
     * @param canvas
     * @return true 表示绘制完成，继续下一个绘制
     */
    private boolean drawFirst (Canvas canvas) {
        boolean flag = false;
        int direction = mFirstTempX >> mFirstTempXMask;
        int mFirstX = mFirstTempX & ((1 << mFirstTempXMask) - 1);
        if (direction == 0){
            //向右
            int d = 0;
            mFirstX += mPerOffX;
            if (mFirstX > mOffX * 2){
                mTempXCycleCount++;
                //换方向,向左
                d = 1;
                mFirstX = mOffX * 2;
            }
            mFirstTempX = d << mFirstTempXMask | mFirstX;
        } else{
            //向左
            mFirstX -= mPerOffX;
            int d = 1;
            if (mFirstX < 0){
                mTempXCycleCount++;
                //换方向,向右
                d = 0;
                mFirstX = 0;
            }
            mFirstTempX = d << mFirstTempXMask | mFirstX;
        }
        if (mTempXCycleCount > mXCycleCount){
            mMatrix.reset();
            return true;
        }
        //左右抖动
        mMatrix.reset();
        mMatrix.setTranslate(-mOffX + mBitmapLeft + mFirstX, mBitmapTop);
        //Log.e(TAG, "drawFirst: mFirstX "+mFirstX);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        return false;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public class ExplodeBean {
        int theta;
        float velocity;
        int color;
        float size;
        long time;
    }

    public interface CrushInterface {
    }
}
