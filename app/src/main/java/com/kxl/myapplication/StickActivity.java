package com.kxl.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StickActivity extends AppCompatActivity {

    List<String> dataList = new ArrayList<>();

    String str[]={"陈奕迅","周星驰","周迅","肥嘟嘟","高圆圆","高进","权志龙","赵又廷","侧田","周杰伦","张学友","林忆莲","李荣浩","五月天","谢安琪","陶喆"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick);
        dataList.addAll(Arrays.asList(str));
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new StickItemDicoration(this,new StickItemDicoration.ObtainTextCallback(){
            @Override
            public String getText(int position) {
                return dataList.get(position).substring(0,1);
            }
        }));
        StickAdapter stickAdapter = new StickAdapter(R.layout.item_stick,dataList);
        recyclerView.setAdapter(stickAdapter);

    }
}
