package ren.yale.scrollviewslippingapp;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ren.yale.android.scrollviewslipping.Behavior.ScrollCommonBehavior;

public class BehaviorRecyclerActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    private List<String> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_recycler);
        recycler();
    }
    private void addBehavior(View v){
        CoordinatorLayout.LayoutParams headerLp = (CoordinatorLayout.LayoutParams) v
                .getLayoutParams();
        try {
            headerLp.setBehavior(new ScrollCommonBehavior( Util.dp2px(this,80),Util.dp2px(this,500)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBehaviorListener(View v){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        ScrollCommonBehavior scrollCommonBehavior = (ScrollCommonBehavior) lp.getBehavior();
        scrollCommonBehavior.setScrollOffsetListener(new ScrollCommonBehavior.ScrollOffsetListener() {
            @Override
            public void scroll(int offset, float rate) {

            }
        });
    }
    private void recycler(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        // addBehavior(mRecyclerView);
        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TestAdaper());
    }
    protected void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mDatas.add("" + (char) i);
        }
    }
    private class TestViewHolder extends  RecyclerView.ViewHolder{

        public TextView tv;
        public TestViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.id_num);
        }
    }
    private class TestAdaper extends  RecyclerView.Adapter<TestViewHolder>{

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TestViewHolder(LayoutInflater.from(BehaviorRecyclerActivity.this).inflate(R.layout.item_test,parent,false));
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }
}
