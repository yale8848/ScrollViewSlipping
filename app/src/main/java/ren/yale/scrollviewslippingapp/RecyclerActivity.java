package ren.yale.scrollviewslippingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ren.yale.android.scrollviewslipping.ScrollOffsetListener;
import ren.yale.android.scrollviewslipping.ScrollviewWrapperLayout;

public class RecyclerActivity extends Activity {
    private static final String TAG="RecyclerActivity";
    private RecyclerView mRecyclerView;
    private RecyclerActivity.TestAdapter mAdapter;
    private List<String> mDatas;
    ScrollviewWrapperLayout mScrollviewWrapperLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        mScrollviewWrapperLayout = (ScrollviewWrapperLayout) findViewById(R.id.scrollviewslip);

        mScrollviewWrapperLayout.setScrollOffsetListener(new ScrollOffsetListener() {
            @Override
            public void onSlipping(int headOffset, int scrollOffset) {
                Log.d(TAG,"headOffset = "+headOffset+" ; scrollOffset = "+scrollOffset);
            }
        });
        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new TestAdapter());
    }

    protected void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 0;i<100;i++) {
            mDatas.add("" +  i);
        }
    }

    class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    RecyclerActivity.this).inflate(R.layout.recycler_layout_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv_recycler_item);
            }
        }
    }
}
