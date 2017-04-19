# Android 滚动布局嵌套

## 需求

有时候需要实现下面的功能，上面是图片，下面是ListView;

![listview](https://solar.fbcontent.cn/s/3fccbb09-app-image.png)


## 解决思路

  一般要用ListView实现，都是给ListView添加HeaderView实现，但是这个需求要求HeaderView不动，而且能和ListView错位,那加入HeaderView的方式就不好处理了；
  我想到的一种解决方案就是自定义ViewGroup，然后添加时间拦截，然后根据情况来事件传递，下面讲一下要实现此功能的几个关键点；

## 关键点
  1. ViewGroup的子view的measure和layout，这是自定义view必须要处理的，这里就不讲了，网上讲的很多；

  2. View的事件拦截

     可以通过dispatchTouchEvent统一拦截，但是把所有逻辑都放在这个函数里觉得有些复杂了，这里通过onInterceptTouchEvent和onTouchEvent处理事件，
     onInterceptTouchEvent 拦截所有事件，只判断ListView是否可以drag，让后在onTouchEvent 具体实现drag，drag View的方式很多，这里通过View.offsetTopAndBottom来移动View；

     ```
        mContentView.offsetTopAndBottom(offset);
     ```
  3. 什么时候ListView滚动
     ListView drag到顶，并且是向上drag的时候

  4. 如何将事件传递给ListView

     ListView要将整个滚动事件接管，就必须先接受down事件，所以这块可以虚拟一个down事件，然后重新派发整个事件：

    ```
        int oldAction = ev.getAction();
        ev.setAction(MotionEvent.ACTION_DOWN);
        dispatchTouchEvent(ev);
        ev.setAction(oldAction);
    ```

  5. ListView滚动时就不要拦截事件了

     只要ListView还能继续向上滚动就停止拦截事件

     ```
        final AbsListView absListView = (AbsListView) mScroolView;
              return absListView.getChildCount() > 0
                         && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                         .getTop() < absListView.getPaddingTop());

     ```

  6. ListView有可能不让父类拦截事件，需要在父类中实现requestDisallowInterceptTouchEvent

    ```
        @Override
        public void requestDisallowInterceptTouchEvent(boolean b) {

        }

    ```

## 小细节

- 滑动抖动

  滑动差值必须大于一个阈值时才能认为可以滑动

    ```
        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        SLOT = vc.getScaledTouchSlop();
   ```

  上面的SLOT就是这个阈值，可以看出这个值在不同手机有可能是不一样的；



## 源码

   源码我已经放在github，https://github.com/yale8848/ScrollViewSlipping.git


## 使用步骤
1. 引入库

   `compile 'ren.yale.android:scrollviewslipping:0.0.4'`

2. 布局添加view

```
    <ren.yale.android.scrollviewslipping.ScrollviewWrapperLayout
        android:id="@+id/scrollviewslip"
        android:layout_width="match_parent"
        android:layout_height="match_parent"

        >
        <ren.yale.android.scrollviewslipping.ScrollHeadView
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

        </ren.yale.android.scrollviewslipping.ScrollHeadView>

        <ren.yale.android.scrollviewslipping.ScrollContentView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

        </ren.yale.android.scrollviewslipping.ScrollContentView>

    </ren.yale.android.scrollviewslipping.ScrollviewWrapperLayout>
```

如，添加listview

```
    xmlns:scrollslip="http://schemas.android.com/apk/res-auto"

    <ren.yale.android.scrollviewslipping.ScrollviewWrapperLayout
        android:id="@+id/scrollviewslip"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        scrollslip:canSlipDown="false"
        scrollslip:reset="true"
        scrollslip:resetRatio="0.3"
        >
        <ren.yale.android.scrollviewslipping.ScrollHeadView
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="300dp"
                android:scaleType="centerCrop"
                android:src="@drawable/head"/>
        </ren.yale.android.scrollviewslipping.ScrollHeadView>

        <ren.yale.android.scrollviewslipping.ScrollContentView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <ListView
                android:id="@+id/listView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="#ffffff"
                >
                </ListView>

        </ren.yale.android.scrollviewslipping.ScrollContentView>

    </ren.yale.android.scrollviewslipping.ScrollviewWrapperLayout>
```

  ScrollContentView 里可以嵌套ListView,RecyclerView,ScrollView,WebView;