package com.posa.apps.assignment3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by javigm on 5/11/15.
 */
public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mImagesPath;

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView imageName;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.iv_image_thumbnail);
            imageName = (TextView)itemView.findViewById(R.id.tv_image_name);
        }
    }

    public RecyclerGridViewAdapter(Context context, ArrayList imagePaths) {
        super();
        mContext = context;
        mImagesPath = imagePaths;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_image, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        File photo = new File(mImagesPath.get(position));
        viewHolder.imageName.setText(photo.getName());
        Picasso.with(mContext)
                .load(photo)
                .fit()
                .into(viewHolder.imgThumbnail);
    }

    @Override
    public int getItemCount() {
        return mImagesPath.size();
    }


}

