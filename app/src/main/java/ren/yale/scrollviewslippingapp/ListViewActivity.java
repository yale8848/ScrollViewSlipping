package ren.yale.scrollviewslippingapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity {
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
         mListView = (ListView) findViewById(R.id.listView);

        List<String> list = new ArrayList<>();
        for(int i = 0;i<100;i++){
            list.add(""+i);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        mListView.setAdapter(arrayAdapter);
    }
}
