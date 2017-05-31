package ren.yale.android.scrollviewslipping.Behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import ren.yale.android.scrollviewslipping.R;

/**
 * Created by Yale on 2017/5/25.
 */
public class ScrollCommonBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "ScrollCommonBehavior";

    private int mEnd= 200;
    private int mStart = 0;
    private int mLastY = 0;
    private ValueAnimator mValueAnimator;
    private View mViewTarget;
    private ScrollOffsetListener mScrollOffsetListener;

    public ScrollCommonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollViewSlip);
        mStart = (int) typedArray.getDimension(R.styleable.ScrollViewSlip_startOffset,0);
        mEnd = (int) typedArray.getDimension(R.styleable.ScrollViewSlip_endOffset,200);
        typedArray.recycle();
    }
    public  ScrollCommonBehavior(int start,int end) throws Exception{
        mEnd = end;
        mStart = start;
        if (mStart>=mEnd){
            throw new Exception("end must > start");
        }
    }

    private void moveView(View view ,int offset){
        ViewCompat.offsetTopAndBottom(view, offset);

        if (mScrollOffsetListener!=null){
            mScrollOffsetListener.scroll(getTop(view),getRate());
        }
    }
    private int getTop(View v){
        return v.getTop()-mStart;
    }
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        child.layout(0,mEnd,parent.getWidth(),parent.getHeight()+mEnd);
        mViewTarget = child;
        return true;
    }

    private boolean canViewScrollUp(View view) {
        return ViewCompat.canScrollVertically(view, -1);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        Log.i(TAG, "onNestedPreScroll: dy = " + dy);

        if (canViewScrollUp(target) || dy <= 0) {
            return;
        }
        if (dy > 0) {
            if (getTop(child)>0){
                consumed[1]=Math.abs(dy);
                moveView(child,-dy);
            }
        }
        if (getTop(child)<0){
            moveView(child, -getTop(child));
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        Log.i(TAG, "onStopNestedScroll: ");
        Log.i(TAG,"getRate "+getRate());
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "onNestedScroll: dyConsumed = " + dyConsumed +" dyUnconsumed = "+dyUnconsumed);
        if (dyUnconsumed < 0 && !(canViewScrollUp(target))) {
            int dy = -dyUnconsumed;
            moveView(child, dy);
        }
        if (child.getTop()>mEnd){
            moveView(child, mEnd-child.getTop());
        }
    }
    private void fling(int start, int end, final View view){
        if (mValueAnimator!=null){
            mValueAnimator.cancel();
        }
        mLastY = 0;

        mValueAnimator = ValueAnimator.ofInt(start,end);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = (int) animation.getAnimatedValue();
                if (mLastY>0){
                    moveView(view, v-mLastY);
                }
                mLastY = v;
            }
        });
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.setDuration(200);
        mValueAnimator.start();
    }
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, final View child, View target,
                                    float velocityX, float velocityY) {

        Log.i(TAG, "onNestedPreFling:  velocityY = " + velocityY);
        if (mValueAnimator!=null&&mValueAnimator.isRunning()){
            return false;
        }
        if (getTop(child)<=0){
            return false;
        }
        int distance = (int) (Math.abs(velocityY)/10);
        if (velocityY>0){
            if (distance>getTop(child)){
                distance = getTop(child);
            }
            fling(distance,mStart,child);
        }else{
            if (distance+child.getTop()>mEnd){
                distance = mEnd - child.getTop();
            }
            fling(child.getTop(),child.getTop()+distance,child);
        }
        return true;
    }


    public float getRate(){
        float v = (float)getTop(mViewTarget)/(mEnd-mStart);
        return  (float) ((int)(100*v))/100;
    }
    public void setScrollOffsetListener(ScrollOffsetListener listener){
        mScrollOffsetListener = listener;
    }
    public  interface ScrollOffsetListener{
        void scroll(int offset,float rate);
    }

}