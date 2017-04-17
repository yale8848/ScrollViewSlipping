package ren.yale.android.scrollviewslipping;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by Yale on 2017/4/17.
 */

public class ScrollviewWrapperLayout extends ViewGroup {
    private static final String TAG="ScrollviewWrapperLayout";
    private int OFFSET = 0;
    private int SLOT = 10;

    private int mStartY = 0;
    private boolean mCanDragging =false;
    private int mOffset = -1;

    private float mMaxVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = -1;

    private View mHeadView;
    private View mContentView;
    private View mScroolView;


    private boolean mIsCanSlipDown = true;


    public ScrollviewWrapperLayout(Context context) {
        super(context);
    }

    public ScrollviewWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollviewWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private View findScrollView(ViewGroup viewGroup){

         for (int i=0;i<viewGroup.getChildCount();i++){
             View v = viewGroup.getChildAt(i);
             if(v instanceof ListView || v instanceof RecyclerView || v instanceof WebView){
                 return v;
             }else if (v instanceof  ViewGroup){
                return findScrollView((ViewGroup) v);
             }
         }
         return null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeadView = getChildAt(0);
        mContentView = getChildAt(1);
        if(mHeadView == null || mContentView==null ||
                ! (mHeadView instanceof ScrollHeadView)|| !(mContentView instanceof  ScrollContentView)){

            throw new RuntimeException("please ensure headView and contentView");
        }
        mScroolView = findScrollView((ViewGroup) mContentView);
        if (mScroolView == null){
            throw new RuntimeException("please ensure contentView head only on scroll child like ListView ,WebView etc.");
        }

        final ViewConfiguration vc = ViewConfiguration.get(getContext());

        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        SLOT = vc.getScaledTouchSlop();
        mScroller = new Scroller(this.getContext());

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int scrollMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        int scrollMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);

        mScroolView.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
        //mHeadView.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
        measureChild(mHeadView, scrollMeasureWidthSpec, scrollMeasureHeightSpec);
        //measureChild(mContentView, scrollMeasureWidthSpec, scrollMeasureHeightSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        OFFSET = mHeadView.getMeasuredHeight();
        mOffset = OFFSET;
        mHeadView.layout(0,0,getMeasuredWidth(),mHeadView.getMeasuredHeight());
        mContentView.layout(0,OFFSET,getMeasuredWidth(),getMeasuredHeight()+OFFSET);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    public boolean canChildNotScrollDown() {

        if (mScroolView instanceof AbsListView){
            final AbsListView absListView = (AbsListView) mScroolView;
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        } else if( mScroolView instanceof  RecyclerView){
            RecyclerView recyclerView = (RecyclerView) mScroolView;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager!=null){
                   return recyclerView.getChildCount()>0&&(layoutManager.findFirstVisibleItemPosition()>0 || recyclerView.getChildAt(0).getTop()<
                   recyclerView.getPaddingTop());
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        Log.d(TAG,"onTouchEvent "+mCanDragging+" "+canChildNotScrollDown()+" "+ev.getAction());
        if(canChildNotScrollDown()){
            return false;
        }

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mActivePointerId = ev.getPointerId(0);
                mStartY = (int) ev.getY();
                mCanDragging = false;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                dragging((int) ev.getY());
                break;
            }
            case MotionEvent.ACTION_UP:{

                break;
            }
        }

        return mCanDragging;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG,"onTouchEvent "+mCanDragging+" "+canChildNotScrollDown()+" "+ev.getAction());
        if(canChildNotScrollDown()){
            return false;
        }
        acquireVelocityTracker(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mCanDragging = false;
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if(mCanDragging){
                    int d = (int) (ev.getY()-mStartY);
                    mOffset += d;
                    offsetTopAndBottomContentView(d);
                    if (mOffset <= 0&&d<0){
                        int oldAction = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(ev);
                        ev.setAction(oldAction);
                        return true;
                    }
                    mStartY = (int) ev.getY();
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                mStartY = (int) ev.getY();
                if(mCanDragging){


                    mVelocityTracker.computeCurrentVelocity(1, mMaxVelocity);
                    final float vy = mVelocityTracker.getYVelocity(mActivePointerId);

                    if(mContentView.getY()>OFFSET/2){
                        int d = (int) (mContentView.getY()-OFFSET);
                        smoothScrollTo(-d, (int) vy);
                        mOffset = OFFSET;
                    }else{
                        int d = (int) (mContentView.getY()-0);
                        smoothScrollTo(-d, (int) vy);
                        mOffset = 0;
                    }
                }
                releaseVelocityTracker();
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                releaseVelocityTracker();
                break;
            }


        }
        return mCanDragging;
    }

    private void offsetTopAndBottomContentView(int offset){
        int d = (int) (mContentView.getY()+offset);
        if (d<0){
            offset = (int) (-1*mContentView.getY());
        }

        if (!mIsCanSlipDown){
                if(mContentView.getY()+offset>OFFSET){
                    offset= (int) (OFFSET - mContentView.getY());
                }
        }
        mContentView.offsetTopAndBottom(offset);
    }
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){

            int offsetY = mScroller.getCurrY();
            Log.d(TAG,"computeScroll offsetY "+offsetY);
            offsetTopAndBottomContentView(offsetY-(int) (mContentView.getY()));
            invalidate();
        }
    }
    public void smoothScrollTo(int deltaY,int vy){
        int startX= (int) mContentView.getX();
        int startY = (int) mContentView.getY();

        mScroller.fling(startX, startY, 0, vy,
                startX, startX, startY+deltaY, startY+deltaY);
        invalidate();
    }
    private void dragging(int y){
        Log.d(TAG,"y = "+  y+" startY = "+mStartY+" mOffset "+mOffset+" SLOT ="+SLOT );
        if(y>mStartY||mOffset>0){
            int delta = Math.abs(mStartY - y);
            if(delta>SLOT&&!mCanDragging){
                mStartY = y;
                mCanDragging = true;
            }
        }
    }

}
