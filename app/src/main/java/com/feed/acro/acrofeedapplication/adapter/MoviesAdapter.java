
package com.feed.acro.acrofeedapplication.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feed.acro.acrofeedapplication.R;
import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private  Context mContext;
    private ArrayList<ImageUploadInfo> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,  story;
        public ImageView storyImg;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.postedBy);
            story = (TextView) view.findViewById(R.id.description);
            storyImg = (ImageView) view.findViewById(R.id.post_img);
        }
    }


    public MoviesAdapter(ArrayList<ImageUploadInfo> moviesList, Context mContext) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(moviesList.size()>0){
            ImageUploadInfo movie = moviesList.get(position);
            holder.title.setText(movie.getNameText());
            holder.story.setText(movie.getDescription());

            if(!TextUtils.isEmpty(movie.getImageURL())){
                Glide.with(mContext).load(movie.getImageURL()).into(holder.storyImg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}