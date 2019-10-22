package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.SourceListAdapter;

import java.util.ArrayList;

public class VisitorBySourceActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recycler_view_source;
    ArrayList<String> sourceArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_by_source);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        iv_back=findViewById(R.id.iv_back);
        recycler_view_source=findViewById(R.id.recycler_view_source);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(VisitorBySourceActivity.this);
        recycler_view_source.setLayoutManager(linearLayoutManager);

        sourceArrayList.add("BNI Website");
        sourceArrayList.add("Facebook");
        sourceArrayList.add("Instagram");
        sourceArrayList.add("Self");
        sourceArrayList.add("BNI Member");
        sourceArrayList.add("Non BNI Member");

        SourceListAdapter sourceListAdapter=new SourceListAdapter(VisitorBySourceActivity.this,sourceArrayList);
        recycler_view_source.setAdapter(sourceListAdapter);
        sourceListAdapter.notifyDataSetChanged();
    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

