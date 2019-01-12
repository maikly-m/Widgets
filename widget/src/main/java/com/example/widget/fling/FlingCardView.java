package com.example.widget.fling;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.example.widget.Utils;

import java.util.LinkedList;

/**
 * Created by ex-huangzhiyi001 on 2019/1/8.
 */
public class FlingCardView extends FrameLayout {
    private static final String TAG = "FlingCardView";
    public View mBottomView;
    public View mTopView;
    public View mInnerView;
    private float mInterceptDownX;
    private float mInterceptDownY;
    private float mTouchDownX;
    private float mTouchDownY;
    private int mTopViewTop;
    private int mTopViewLeft;
    private float mTempMoveX;
    private float mTempMoveY;
    private AccelerateInterpolator mAccelerateInterpolator;
    private OverScroller mOverScroller;
    private boolean directionToRight;
    private int cardOffsetX;
    private int cardOffsetY;
    private int mInnerView_top;
    private int mInnerView_left;
    private int mBottomView_top;
    private int mBottomView_left;
    private int childRes;
    private int childCount = 4;// default, 4个
    private int mTopViewOffsetLeft;
    private int mTopViewOffsetTop;
    private boolean ignoredMoveUp;
    private boolean mResetLayout;
    private boolean isContinue = true;
    private LinkedList<DataHolder> mDataHolders = new LinkedList<>();
    private DataInterface mDataInterface;
    private CardFlingInterface mCardFlingInterface;


    public FlingCardView (@NonNull Context context) {
        this(context, null);
    }

    public FlingCardView (@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlingCardView (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init (Context context) {
        mAccelerateInterpolator = new AccelerateInterpolator();
        mOverScroller = new OverScroller(context, mAccelerateInterpolator);
        cardOffsetX = Utils.dp2px(context, 20);
        cardOffsetY = Utils.dp2px(context, 20);
    }

    public void setChild (@LayoutRes int childRes, LinkedList<DataHolder> linkedList) {
        this.childRes = childRes;
        setDataList(linkedList);
        setChildViews();
    }

    private void setChildViews () {
        Context context = getContext();
        if (context == null){
            throw new IllegalAccessError("Context is null");
        }
        removeAllViews();
        if (childCount > mDataHolders.size()){
            childCount = mDataHolders.size();
        }
        for (int i = 0; i < childCount; i++){
            View v = View.inflate(getContext(), childRes, null);
            LayoutParams lp = new LayoutParams(Utils.dp2px(context, 300), Utils.dp2px(context, 400));
            addView(v, lp);
        }
    }

    /**
     * card 的个数， 最多不过四个
     *
     * @param count
     */
    public void setChildCount (int count) {
        if (count > 5){
            count = 5;
        } else if (count < 3){
            count = 3;
        }
        this.childCount = count;
        setChildViews();
    }

    /**
     * x偏移值, 最多不过40
     *
     * @param offX
     */
    public void setCardOffsetX (int offX) {
        if (offX > 40){
            offX = 40;
        }
        Context context = getContext();
        if (context == null){
            throw new IllegalAccessError("Context is null");
        }
        this.cardOffsetX = Utils.dp2px(context, offX);
    }


    /**
     * y偏移值, 最多不过40
     *
     * @param offY
     */
    public void setCardOffsetY (int offY) {
        if (offY > 40){
            offY = 40;
        }
        Context context = getContext();
        if (context == null){
            throw new IllegalAccessError("Context is null");
        }
        this.cardOffsetY = Utils.dp2px(context, offY);
    }

    private void setDataList(LinkedList<DataHolder> dataList){
        mDataHolders.clear();
        mDataHolders.addAll(dataList);
    }

    public LinkedList<DataHolder> getDataHolders () {
        return mDataHolders;
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev) {
        boolean flag = false;
        if (getChildCount() == 0){
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction() & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
            mInterceptDownX = ev.getX();
            mInterceptDownY = ev.getY();
            flag = false;
            break;
        case MotionEvent.ACTION_MOVE:
            float x = ev.getX();
            float y = ev.getY();
            float mX = Math.abs(x - mInterceptDownX);
            float mY = Math.abs(y - mInterceptDownY);
            if (mX > 30){
                // 滑动
                flag = true;
            } else{
                // 点击
                flag = false;
            }
            break;
        case MotionEvent.ACTION_UP:
            flag = false;
            break;
        default:
            break;
        }
        return flag;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (getChildCount() == 0){
            return super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
            mTouchDownX = event.getX();
            mTouchDownY = event.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            float x = event.getX();
            float y = event.getY();
            //没有走down事件的时候
            if (mTouchDownX == 0){
                mTouchDownX = x;
            }
            if (mTouchDownY == 0){
                mTouchDownY = y;
            }

            int dx = (int) (x - mTouchDownX);
            if (Math.abs(dx) < 10){
                ignoredMoveUp = true;
                break;
            }
            ignoredMoveUp = false;
            directionToRight = dx > 0;
            if (dx > mTopView.getMeasuredWidth() * 1.2){
                break;
            } else if (dx < -mTopView.getMeasuredWidth() * 1.2){
                break;
            }
            dx = dx << 1;
            layoutTop(dx);
            layoutInner(dx);
            layoutBottom(dx);
            invalidate();

            mTempMoveX = x;
            mTempMoveY = y;
            break;
        case MotionEvent.ACTION_UP:
            if (!ignoredMoveUp){
                mTopViewOffsetLeft = mTopView.getLeft();
                mTopViewOffsetTop = mTopView.getTop();
                mTouchDownX = 0;
                mTouchDownY = 0;
                startResetLayout();
            }
            break;
        default:
            break;
        }
        return true;
    }

    private void layoutInner (int dx) {

        dx = Math.abs(dx);
        int x = dx * cardOffsetX / 400;
        int y = dx * cardOffsetY / 400;
        if (x > cardOffsetX){
            x = cardOffsetX;
        }
        if (y > cardOffsetY){
            y = cardOffsetY;
        }
        for (int i = 0; i < getChildCount() - 1; i++){
            //去除最顶层
            View v = getChildAt(i);
            int coe = getChildCount() - 1 - i;
            if (getChildCount() >= childCount && i == 0){
                continue;
            }
            int mX = x - coe * cardOffsetX;
            int mY = y - coe * cardOffsetY;

            v.layout(mTopViewLeft + mX, mTopViewTop + mY, mTopViewLeft + mX + v.getMeasuredWidth(),
                    mTopViewTop + mY + v.getMeasuredHeight());
        }
    }

    private void layoutBottom (int dx) {
    }

    private void startResetLayout () {
        int mX = mTopViewOffsetLeft - mTopViewLeft;
        if (Math.abs(mX) > mTopView.getMeasuredWidth() / 2){
            //移出界面
            mResetLayout = false;
            if (mTopViewLeft - mTopViewOffsetLeft > 0){
                mOverScroller.startScroll(mTopViewOffsetLeft, mTopViewOffsetTop,
                        mTopViewLeft - mTopViewOffsetLeft - mTopView.getMeasuredWidth() * 2,
                        mTopViewTop - mTopViewOffsetTop - mTopView.getMeasuredHeight() / 8, 300);
            }else {
                mOverScroller.startScroll(mTopViewOffsetLeft, mTopViewOffsetTop,
                        mTopViewLeft - mTopViewOffsetLeft + mTopView.getMeasuredWidth() * 2,
                        mTopViewTop - mTopViewOffsetTop - mTopView.getMeasuredHeight() / 8, 300);
            }
        } else{
            //复位界面
            mResetLayout = true;
            mOverScroller.startScroll(mTopViewOffsetLeft, mTopViewOffsetTop,
                    mTopViewLeft - mTopViewOffsetLeft, mTopViewTop - mTopViewOffsetTop, 300);
        }
        invalidate();
    }

    private void layoutTop (int dx) {
        //调整方向
        int dy = dx / 12;
        if (dx > 0){
            dy = -dx / 12;
        }
        mTopView.layout(mTopViewLeft + dx, mTopViewTop + dy, mTopViewLeft + dx + mTopView.getMeasuredWidth(),
                mTopViewTop + dy + mTopView.getMeasuredHeight());
        float degree = dx * 10f / mTopView.getMeasuredWidth();
        mTopView.setPivotX(mTopView.getMeasuredWidth() >> 1);
        mTopView.setPivotY(mTopView.getMeasuredHeight() >> 1);
        mTopView.setRotation(degree);
        if (mCardFlingInterface != null){
            mCardFlingInterface.cardFlinging(mTopView, dx, mDataHolders.peek());
        }
    }

    @Override
    public void computeScroll () {
        super.computeScroll();
        if (mOverScroller.computeScrollOffset()){
            resetTopLayout();
            resetInnerLayout();
            resetBottomLayout();
            //重置布局
            resetLayout();
        }
    }

    private void resetLayout () {
        int currX = mOverScroller.getCurrX();
        if (!mResetLayout){
            if (currX == (mTopViewLeft + mTopView.getMeasuredWidth() * 2) ||
                    currX == (mTopViewLeft - mTopView.getMeasuredWidth() * 2)){
                if (mCardFlingInterface != null){
                    mCardFlingInterface.cardFlingOut(mTopView);
                }
                setTop2Bottom();
            }
        }
        if (mCardFlingInterface != null){
            mCardFlingInterface.cardFlinging(mTopView, currX, mDataHolders.peek());
        }
        invalidate();
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private void resetInnerLayout () {
        int currX = mOverScroller.getCurrX() - mTopViewLeft;

        if (mResetLayout){
            int x = 0;
            int y = 0;
            if (mTopViewLeft - mTopViewOffsetLeft != 0 && Math.abs(mTopViewLeft - mTopViewOffsetLeft) >= 400){
                x = currX * cardOffsetX / (mTopViewLeft - mTopViewOffsetLeft);
                y = currX * cardOffsetY / (mTopViewLeft - mTopViewOffsetLeft);
            }else {
                x = -Math.abs(currX) * cardOffsetX / 400;
                y = -Math.abs(currX) * cardOffsetY / 400;
            }
            for (int i = 0; i < getChildCount() - 1; i++){
                //去除最顶层
                View v = getChildAt(i);
                int coe = getChildCount() - 1 - i;
                if (getChildCount() >= childCount && i == 0){
                    continue;
                }
                int mX = -x - coe * cardOffsetX;
                int mY = -y - coe * cardOffsetY;
                v.layout(mTopViewLeft + mX, mTopViewTop + mY, mTopViewLeft + mX + v.getMeasuredWidth(),
                        mTopViewTop + mY + v.getMeasuredHeight());
            }
        }
    }

    private void resetBottomLayout () {
    }

    private void resetTopLayout () {
        int currX = mOverScroller.getCurrX();
        int currY = mOverScroller.getCurrY();
        mTopView.layout(currX, currY, currX + mTopView.getMeasuredWidth(),
                currY + mTopView.getMeasuredHeight());

        float degree = (currX - mTopViewLeft) * 10f / mTopView.getMeasuredWidth();
        mTopView.setPivotX(mTopView.getMeasuredWidth() >> 1);
        mTopView.setPivotY(mTopView.getMeasuredHeight() >> 1);
        mTopView.setRotation(degree);
    }

    private void setTop2Bottom () {
        View tempView = mTopView;
        removeViewAt(getChildCount() - 1);
        if (mDataHolders.size() > 0 && isContinue){
            setViewContent(tempView, false, 0);
            addView(tempView, 0);
        }else {
            if (getChildCount() == 0 && mCardFlingInterface != null){
                mCardFlingInterface.cardEnd(tempView);
            }
        }
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();

        for (int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            int w = view.getMeasuredWidth();
            int h = view.getMeasuredHeight();

            Object tag = view.getTag(0x0f << 24);
            if (tag == null || (int)tag != 0x100){
                //初次设置
                setViewContent(view, true, getChildCount() - i - 1);
            }
            view.setTag(0x0f << 24, 0x100);

            view.setPivotX(w >> 1);
            view.setPivotY(h >> 1);
            view.setRotation(0f);

            int coe = getChildCount() - i - 1;
            if (getChildCount() >= childCount && i == 0){
                coe = coe - 1;
            }
            view.layout((parentWidth - w) / 2 - coe * cardOffsetX, (parentHeight - h) / 2 - coe * cardOffsetY,
                    (parentWidth + w) / 2 - coe * cardOffsetX, (parentHeight + h) / 2 - coe * cardOffsetY);
        }
        if (getChildCount() != 0){
            mTopView = getChildAt(getChildCount() - 1);
            mTopViewLeft = mTopView.getLeft();
            mTopViewTop = mTopView.getTop();
        }
    }

    private void setViewContent (View view, boolean firstLayout, int index) {
        if (mDataHolders.size() > 0 && mDataInterface != null){
            if (firstLayout){
                if (mDataHolders.size() - 1 < index){
                    return;
                }
                DataHolder dataHolder = mDataHolders.get(index);
                mDataHolders.remove(index);
                mDataInterface.setCardContent(view, dataHolder);
            }else {
                mDataInterface.setCardContent(view, mDataHolders.poll());
            }
        }
    }

    public void setDataInterface(DataInterface dataInterface){
        mDataInterface = dataInterface;
    }

    public interface DataInterface{
        void setCardContent(View view, DataHolder dataHolder);
    }

    public void setCardFlingInterface(CardFlingInterface cardFlingInterface){
        mCardFlingInterface = cardFlingInterface;
    }

    public interface CardFlingInterface{
        void cardFlingOut(View view);
        void cardFlinging(View view, int dx, DataHolder dataHolder);
        void cardEnd(View lastView);
    }

    public static class DataHolder{
        public int id;
        public String content;
        public @DrawableRes int bgId;
    }
}
