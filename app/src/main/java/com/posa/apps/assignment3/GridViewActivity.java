package com.posa.apps.assignment3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class GridViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = GridViewActivity.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        Alog.debug(LOG_TAG, "onCreate()");
        initViewById();
        setTitle("POSA Activity 3 - GridViewActivity");

        Bundle extras = getIntent().getExtras();
        ArrayList photosPath = new ArrayList();
        if (extras != null) {
            photosPath = (ArrayList) extras.getSerializable(MainActivity.INTENT_VIEW_IMAGES);
            Alog.debug(LOG_TAG, "photosPath.size:" + photosPath.size());
        }

        // Calling the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerGridViewAdapter = new RecyclerGridViewAdapter(this, photosPath);
        mRecyclerView.setAdapter(mRecyclerGridViewAdapter);

    }

    private void initViewById() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_bitmaps_grid);
    }

}
