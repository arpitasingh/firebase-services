
package com.feed.acro.acrofeedapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feed.acro.acrofeedapplication.AdminPage;
import com.feed.acro.acrofeedapplication.R;
import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;
import com.feed.acro.acrofeedapplication.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdminViewsAdapter extends RecyclerView.Adapter<AdminViewsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Post> moviesList;
    private DatabaseReference mDatabase;
    String Database_Path = "All_Content_Uploads_Database/UploadedFeed";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, story;
        public ImageView storyImg;
        public LinearLayout buttonsLayout;
        public Button reject, approve;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.postedBy);
            story = (TextView) view.findViewById(R.id.description);
            storyImg = (ImageView) view.findViewById(R.id.post_img);
            reject = (Button) view.findViewById(R.id.reject);
            approve = (Button) view.findViewById(R.id.approve);
            buttonsLayout = (LinearLayout) view.findViewById(R.id.buttonsLayout);
        }
    }


    public AdminViewsAdapter(ArrayList<Post> moviesList, Context mContext) {
        this.moviesList = moviesList;
        this.mContext = mContext;////////TODO
       /* for (int i = 0; i < moviesList.size(); i++) {
            for (int j = 0; j < moviesList.get(i).getImageUploadInfo().size(); j++) {
                feed.add(moviesList.get(i).getImageUploadInfo().get(j));
            }
        }*/
        mDatabase = FirebaseDatabase.getInstance().getReference(Database_Path);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (moviesList.size() > 0) {

//            final ImageUploadInfo movie = feed.get(position);
            final Post posts = moviesList.get(position);
            if (posts.getImageUploadInfo().getStatus().equalsIgnoreCase("Pending")) {
                holder.title.setText(posts.getImageUploadInfo().getNameText());
                holder.story.setText(posts.getImageUploadInfo().getDescription());

                if (!TextUtils.isEmpty(posts.getImageUploadInfo().getImageURL())) {
                    Glide.with(mContext).load(posts.getImageUploadInfo().getImageURL()).into(holder.storyImg);
                }

                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mDatabase.child(posts.getImageUploadInfo().getUserId())
                                .child(posts.getUid()).child("status").setValue("Rejected");
                        holder.buttonsLayout.setVisibility(View.INVISIBLE);
                        mContext.startActivity(new Intent(mContext, AdminPage.class));
                        ((Activity)mContext).finish();
                    }
                });

                holder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    /*for (int k = 0; k < moviesList.size(); k++) {
                        if(moviesList.get(k).getUid().equalsIgnoreCase(movie.getUserId())){
                            mDatabase.child(moviesList.get(k).getUid()).child("UploadedFeed")
                                    .child("status").setValue("Approved");
                        }
                    }
                    feed.remove(position);*/
                        mDatabase.child(posts.getImageUploadInfo().getUserId())
                                .child(posts.getUid()).child("status").setValue("Approved");

                        holder.buttonsLayout.setVisibility(View.INVISIBLE);
                        mContext.startActivity(new Intent(mContext, AdminPage.class));
                        ((Activity)mContext).finish();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}