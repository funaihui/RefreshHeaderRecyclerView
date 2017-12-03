package com.wizardev.headerrefreshforrecyclerview;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.wizardev.headerrefreshforrecyclerview.adapter.RefreshHeaderAdapter;
import com.wizardev.headerrefreshforrecyclerview.interfaces.OnRefreshListener;
import com.wizardev.headerrefreshforrecyclerview.view.RefreshHeaderRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> mStringList = new ArrayList<>();
    private RefreshHeaderRecyclerView mRecyclerView;
    private RefreshHeaderAdapter mAdapter;
    private static final int FINISH = 1;

    @SuppressLint("HandlerLeak")
    private  Handler sHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FINISH) {
                Toast.makeText(MainActivity.this,"刷新完成！",Toast.LENGTH_SHORT).show();
                mRecyclerView.refreshComplete();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new RefreshHeaderAdapter(mStringList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();//模拟数据的请求

            }
        });
    }

    private void requestData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.toString();
                try {
                    Thread.sleep(1500);
                    Message message = Message.obtain();
                    message.what = FINISH;
                    sHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void initData() {
        for (int i = 0; i < 15; i++) {
            mStringList.add("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sHandler.removeCallbacks(null);
    }
}
