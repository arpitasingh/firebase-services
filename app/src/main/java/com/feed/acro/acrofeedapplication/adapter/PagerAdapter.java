package com.feed.acro.acrofeedapplication.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.feed.acro.acrofeedapplication.fragments.TabFragment1;
import com.feed.acro.acrofeedapplication.fragments.TabFragment2;
import com.feed.acro.acrofeedapplication.fragments.TabFragment3;
import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;
import com.feed.acro.acrofeedapplication.models.Post;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<Post> dataFeed= new ArrayList<>();
    ArrayList<ImageUploadInfo> imgFeed;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Post> dataFeed) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.dataFeed = dataFeed;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle mBundle = new Bundle();
        imgFeed = new ArrayList<>();
        /////TODO
        /*for (int k = 0; k < dataFeed.size(); k++) {
            for(int j=0; j<dataFeed.get(k).getImageUploadInfo().size(); j++) {
                imgFeed.add(dataFeed.get(k).getImageUploadInfo().get(j));
            }
        }*/
        mBundle.putParcelableArrayList("data_list", dataFeed);

        switch (position) {
            case 0:
                TabFragment1 tab1 = new TabFragment1();
                tab1.setArguments(mBundle);
                return tab1;
            case 1:
                TabFragment2 tab2 = new TabFragment2();
                tab2.setArguments(mBundle);
                return tab2;
            case 2:
                TabFragment3 tab3 = new TabFragment3();
                tab3.setArguments(mBundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}