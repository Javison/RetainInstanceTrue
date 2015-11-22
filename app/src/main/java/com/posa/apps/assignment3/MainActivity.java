package com.posa.apps.assignment3;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements TaskFragment.TaskCallbacks{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String KEY_CURRENT_PROGRESS = "current_progress";
    private static final String KEY_PERCENT_PROGRESS = "percent_progress";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String KEY_LIST_URL_IMAGES = "list_url_images";

    public static final String INTENT_VIEW_IMAGES = "arraylist_bitmap";

    private TaskFragment mTaskFragment;
    private ProgressBar mProgressBar;
    private TextView mPercent;
    private EditText mURLtoAdd;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ItemTouchHelper.SimpleCallback mSimpleItemTouchCallback;

    FloatingActionButton mFabDownload;
    FloatingActionButton mFabDelete;
    Button mBtnAddURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Alog.debug(LOG_TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        initViewById();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        initSimpleItemTouchCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        setTitle("POSA Activity 3 - MainActivity");

        // Restore saved state.
        if (savedInstanceState != null) {
            mProgressBar.setProgress(savedInstanceState.getInt(KEY_CURRENT_PROGRESS));
            mPercent.setText(savedInstanceState.getString(KEY_PERCENT_PROGRESS));
            mRecyclerViewAdapter.setmPhotosURL((ArrayList<String>) savedInstanceState.getSerializable(KEY_LIST_URL_IMAGES));
        } else {
            initListURLImages();
        }

        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is being retained
        // over a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        if (mTaskFragment.isRunning()) {
            mFabDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_white));
            mProgressBar.setVisibility(View.VISIBLE);
            mPercent.setVisibility(View.VISIBLE);
        } else {
            mFabDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));
        }

        initButtonsListener();

    }

    private void initSimpleItemTouchCallback() {
        mSimpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Alog.debug(LOG_TAG, "onMove() From:" + viewHolder.getAdapterPosition() + " To:" + target.getAdapterPosition());
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Alog.debug(LOG_TAG, "onMove() onSwiped:" + viewHolder.getAdapterPosition() + " Dir: " + swipeDir);
                int position = viewHolder.getAdapterPosition();
                mRecyclerViewAdapter.remove(position);
            }
        };
    }

    private void initViewById() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
        mFabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        mBtnAddURL = (Button) findViewById(R.id.btn_add_url);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_horizontal);
        mPercent = (TextView) findViewById(R.id.tv_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_urlList);
        mURLtoAdd = (EditText) findViewById(R.id.et_urlToAdd);
    }

    private void initListURLImages() {
        mRecyclerViewAdapter.add("http://st-listas.20minutos.es/images/2013-07/366227/4119118_640px.jpg");
        mRecyclerViewAdapter.add("https://upload.wikimedia.org/wikipedia/ca/1/1f/La_persist%C3%A8ncia_de_la_mem%C3%B2ria.jpg");
        mRecyclerViewAdapter.add("http://4.bp.blogspot.com/-YLmBl9DLa6I/VXeI_k1hJbI/AAAAAAAAASY/RDVh0Z2crNE/s320/CHUCK%2BNORRIS%2521.jpg");
        //mRecyclerViewAdapter.add("http://www.dre.vanderbilt.edu/~schmidt/ka.png");
        mRecyclerViewAdapter.add("http://st-listas.20minutos.es/images/2013-07/366227/4119120_640px.jpg");

    }

    private void initButtonsListener() {
        Alog.debug(LOG_TAG, "initButtonsListener()");

        /** Starts background task */
        mFabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTaskFragment.isRunning()) {
                    mTaskFragment.cancel();
                    Snackbar.make(view, "Cancelled download", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (mRecyclerViewAdapter.getmPhotosURL().size() > 0) {
                        String[] photosArr = mRecyclerViewAdapter.getmPhotosURL().toArray(new String[mRecyclerViewAdapter.getItemCount()]);
                        mTaskFragment.start(photosArr);
                        Snackbar.make(view, "Downloading " + mRecyclerViewAdapter.getItemCount() + " photos", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    } else {
                        Snackbar.make(view, "No images to download ", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });

        mFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> photos;
                photos = mTaskFragment.getmPhotosPath();
                if(photos != null && photos.size() > 0) {
                    int countDeleted = 0;
                    for (String photoPath : photos) {
                        Alog.debug(LOG_TAG, "Delete " + photoPath);
                        File file = new File(photoPath);
                        if (file.delete()){
                            countDeleted++;
                        }
                    }
                    mTaskFragment.getmPhotosPath().clear();

                    Snackbar.make(view, "Deleted " + countDeleted + " Images", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "No images to delete", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        mBtnAddURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlToAdd = mURLtoAdd.getText().toString().trim();

                if (urlToAdd.length() != 0) {
                    //TODO regex URL and img sufix validator
                    mRecyclerViewAdapter.add(urlToAdd);

                } else {
                    Snackbar.make(view, "Please introduce a valid photo URL", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Alog.debug(LOG_TAG, "onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PROGRESS, mProgressBar.getProgress());
        outState.putString(KEY_PERCENT_PROGRESS, mPercent.getText().toString());
        outState.putSerializable(KEY_LIST_URL_IMAGES, mRecyclerViewAdapter.getmPhotosURL());
    }


    /*********************************/
    /***** TASK CALLBACK METHODS *****/
    /*********************************/

    @Override
    public void onPreExecute() {
        Alog.debug(LOG_TAG, "onPreExecute()");
        mFabDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_white));
        mProgressBar.setVisibility(View.VISIBLE);
        mPercent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressUpdate(int percent) {
        // Alog.debug(LOG_TAG, "onProgressUpdate(int)");
        mProgressBar.setProgress(percent * mProgressBar.getMax() / 100);
        mPercent.setText(percent + "%");
    }

    @Override
    public void onCancelled() {
        Alog.debug(LOG_TAG, "onCancelled()");
        mProgressBar.setProgress(0);
        mFabDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));
        mProgressBar.setVisibility(View.GONE);
        mPercent.setVisibility(View.GONE);
    }


    @Override
    public void onPostExecute(ArrayList arrayList) {
        Alog.debug(LOG_TAG, "onPostExecute()");
        mProgressBar.setProgress(mProgressBar.getMax());
        mFabDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));
        mProgressBar.setProgress(0);
        mPercent.setText("0%");
        mProgressBar.setVisibility(View.GONE);
        mPercent.setVisibility(View.GONE);
        mRecyclerViewAdapter.clear();
        Snackbar.make(getCurrentFocus(), "Downloaded " + arrayList.size() + " photos", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        Alog.debug(LOG_TAG, "Launch activity GridViewActivity");
        Intent intent = new Intent(MainActivity.this, GridViewActivity.class);
        intent.putExtra(INTENT_VIEW_IMAGES, arrayList);
        startActivity(intent);
    }



    /************************/
    /***** OPTIONS MENU *****/
    /************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /************************/
    /***** LOGS & STUFF *****/
    /************************/


    @Override
    protected void onStart() {
        Alog.debug(LOG_TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Alog.debug(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Alog.debug(LOG_TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Alog.debug(LOG_TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Alog.debug(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }


}
