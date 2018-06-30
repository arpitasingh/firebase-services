package com.feed.acro.acrofeedapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feed.acro.acrofeedapplication.R;
import com.feed.acro.acrofeedapplication.adapter.MoviesAdapter;
import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;
import com.feed.acro.acrofeedapplication.models.Post;

import java.util.ArrayList;

public class TabFragment2 extends Fragment {

    ArrayList<Post> dataFeed = new ArrayList<>();
    ArrayList<ImageUploadInfo> studentFeed = new ArrayList<>();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    MoviesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_fragment2, container, false);
        // Replace 'android.R.id.list' with the 'id' of your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.feeds_list);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(studentFeed, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            dataFeed = getArguments().getParcelableArrayList("data_list");
        }
        if(dataFeed!=null) {
            if(dataFeed.size()>0) {
                for (int i = 0; i < dataFeed.size(); i++) {
                    if (dataFeed.get(i).getImageUploadInfo().getStatus().equalsIgnoreCase("Approved") &&
                            dataFeed.get(i).getImageUploadInfo().getPostTo().equalsIgnoreCase("Student")) {
                        studentFeed.add(dataFeed.get(i).getImageUploadInfo());
                    }
                }
            }
        }

    }
}
