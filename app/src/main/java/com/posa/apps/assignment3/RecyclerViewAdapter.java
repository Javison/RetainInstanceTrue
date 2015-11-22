package com.posa.apps.assignment3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by javigm on 4/11/15.
 */
public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.URLViewHolder> {

    private ArrayList<String> mPhotosURL;

    public RecyclerViewAdapter() {
        mPhotosURL = new ArrayList<>();
    }

    public class URLViewHolder extends RecyclerView.ViewHolder {
        TextView photo_url;

        public URLViewHolder(View itemView) {
            super(itemView);
            photo_url = (TextView) itemView.findViewById(R.id.tv_photo_url);
        }
    }

    public RecyclerViewAdapter(ArrayList<String> mPhotosURL) {
        this.mPhotosURL = mPhotosURL;
    }

    @Override
    public URLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_url, parent, false);
        URLViewHolder viewHolder = new URLViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(URLViewHolder holder, int position) {
        holder.photo_url.setText(mPhotosURL.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotosURL.size();
    }

    public void add(String photoUrl) {
        mPhotosURL.add(photoUrl);
        notifyItemInserted(mPhotosURL.indexOf(photoUrl));
    }

    public void remove(String photoUrl) {
        notifyItemRemoved(mPhotosURL.indexOf(photoUrl));
        mPhotosURL.remove(photoUrl);
    }

    public void clear() {
        mPhotosURL.clear();
        notifyDataSetChanged();
    }

    public void remove(int positionPhotoUrl) {
        notifyItemRemoved(positionPhotoUrl);
        mPhotosURL.remove(positionPhotoUrl);
    }

    public ArrayList<String> getmPhotosURL() {
        return mPhotosURL;
    }

    public void setmPhotosURL(ArrayList<String> mPhotosURL) {
        this.mPhotosURL = mPhotosURL;
        notifyDataSetChanged();
    }
}
