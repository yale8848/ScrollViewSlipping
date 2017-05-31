package ren.yale.scrollviewslippingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_listview:{
                startActivity(new Intent(MainActivity.this,ListViewActivity.class));
                break;
            }
            case R.id.btn_recycler:{
                startActivity(new Intent(MainActivity.this,RecyclerActivity.class));
                break;
            }
            case R.id.btn_scrollview:{
                startActivity(new Intent(MainActivity.this,ScrollViewActivity.class));
                break;
            }
            case R.id.btn_webview:{
                startActivity(new Intent(MainActivity.this,WebViewActivity.class));
                break;
            }
            case R.id.btn_behavior_recycler:{
                startActivity(new Intent(MainActivity.this,BehaviorRecyclerActivity.class));
                break;
            }
            case R.id.btn_behavior_webview:{
                startActivity(new Intent(MainActivity.this,BehaviorWebviewActivity.class));
                break;
            }
            case R.id.btn_behavior_scrollview:{
                startActivity(new Intent(MainActivity.this,BehaviorScrollViewActivity.class));
                break;
            }
        }
    }

}
