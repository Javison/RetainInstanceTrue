package com.posa.apps.assignment3;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {

    private static final String LOG_TAG = TaskFragment.class.getSimpleName();

    private ArrayList<String> mPhotosPath;
    private TaskCallbacks mCallbacks;
    private DownloadTask mDownloadTask;
    private BlackAndWhiteTask mBlackAndWhiteTask;
    private boolean mRunning;

    private ConcurrentHashMap<Bitmap, Boolean> mURLImagesDownloadedMap;
    private int mTotalImagesToDownload;
    private int mTotalImagesConvertedBlackWhite;

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute(ArrayList arrayList);
        //void onPostExecuteBlackWhite(ArrayList arrayList);
    }



    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        Alog.debug(LOG_TAG, "onAttach()--Deprecated");
        super.onAttach(activity);
        //onAttach(activity.getApplicationContext());
        // mCallbacks = (TaskCallbacks) activity;

        if (!(activity instanceof TaskCallbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }

        mCallbacks = (TaskCallbacks) activity;

    }

    public ArrayList<String> getmPhotosPath() {
        return mPhotosPath;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Alog.debug(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    /**
     * Note that this method is <em>not</em> called when the Fragment is being
     * retained across Activity instances. It will, however, be called when its
     * parent Activity is being destroyed for good (such as when the user clicks
     * the back button, etc.).
     */
    @Override
    public void onDestroy() {
        Alog.debug(LOG_TAG, "onDestroy()");
        super.onDestroy();
        cancel();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        Alog.debug(LOG_TAG, "onDetach()");
        super.onDetach();
        mCallbacks = null;
    }

    /*****************************/
    /***** TASK FRAGMENT API *****/
    /*****************************/

    /**
     * Start the background task.
     */
    public void start(String[] photosURL) {
        Alog.debug(LOG_TAG, "start()");

        mTotalImagesToDownload = photosURL.length;
        mURLImagesDownloadedMap = new ConcurrentHashMap<Bitmap, Boolean>();

        if (!mRunning) {
            mPhotosPath = new ArrayList<>();
            mDownloadTask = new DownloadTask();
            mBlackAndWhiteTask = new BlackAndWhiteTask();
            mDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photosURL);
            mBlackAndWhiteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mRunning = true;
        }
    }

    /**
     * Cancel the background task.
     */
    public void cancel() {
        Alog.debug(LOG_TAG, "cancel()");
        if (mRunning) {
            mDownloadTask.cancel(false);
            mDownloadTask = null;
            mRunning = false;
        }
    }

    /**
     * Returns the current state of the background task.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /***************************/
    /***** BACKGROUND TASK *****/
    /***************************/

    /**
     * A download task that performs some background work and
     * proxies progress updates and results back to the Activity.
     *
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the Activity's and
     * Fragment's onDestroy() method have been called.
     */
    private class DownloadTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            Alog.debug(LOG_TAG, "DownloadTask onPreExecute()");
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
                mRunning = true;
            }
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(String... urls) {
            Alog.debug(LOG_TAG, "DownloadTask doInBackground()");

            // Download bitmaps
            for(int i = 0; !isCancelled() && i < urls.length; i++) {
                Alog.debug(LOG_TAG, "Downloading: " + urls[i]);
                Bitmap bitmap = Utils.downloadAndDecodeImage(urls[i]);
                if (bitmap != null) {
                    mURLImagesDownloadedMap.putIfAbsent(bitmap, false);
                } else {
                    mTotalImagesToDownload--;
                }
                publishProgress((i + 1) * 100 / urls.length);
                // SystemClock.sleep(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // Alog.debug(LOG_TAG, "onProgressUpdate()");
            if (mCallbacks != null) {
                mCallbacks.onProgressUpdate(percent[0]);
            }
        }

        @Override
        protected void onCancelled() {
            Alog.debug(LOG_TAG, "DownloadTask onCancelled()");
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
                mRunning = false;
            }
        }


    }

    /**
     * A task that performs Black And White progresion
     *
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the Activity's and
     * Fragment's onDestroy() method have been called.
     */
    private class BlackAndWhiteTask extends AsyncTask<Void, Void, ArrayList> {

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            Alog.debug(LOG_TAG, "BlackAndWhiteTask doInBackground()");

            while (mTotalImagesConvertedBlackWhite < mTotalImagesToDownload) {
                for (Bitmap bitmap : mURLImagesDownloadedMap.keySet()) {
                    if (mURLImagesDownloadedMap.replace(bitmap, Boolean.valueOf(false), Boolean.valueOf(true))) {
                        Alog.debug(LOG_TAG, "BlackAndWhiteTask doInBackground()");
                        // Download bitmaps
                        Bitmap bitmapBlackWhite = Utils.toGrayscale(bitmap);
                        String photoPathBlackAndWhite = Utils.saveBitmapOnPngFile(bitmapBlackWhite);
                        Alog.debug(LOG_TAG, "Downloaded photoPathBlackAndWhite: " + photoPathBlackAndWhite);
                        if (photoPathBlackAndWhite != "") {
                            mPhotosPath.add(photoPathBlackAndWhite);
                        }
                        mTotalImagesConvertedBlackWhite++;
                    }
                }
                try { Thread.sleep(100); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
            return mPhotosPath;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            Alog.debug(LOG_TAG, "BlackAndWhiteTask onPostExecute() arrayList.size:" + arrayList.size());
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(arrayList);
                mRunning = false;
            }
        }

    }


    /************************/
    /***** LOGS & STUFF *****/
    /************************/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Alog.debug(LOG_TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Alog.debug(LOG_TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Alog.debug(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Alog.debug(LOG_TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Alog.debug(LOG_TAG, "onStop()");
        super.onStop();
    }

}


