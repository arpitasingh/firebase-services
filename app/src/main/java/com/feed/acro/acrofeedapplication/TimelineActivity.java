package com.feed.acro.acrofeedapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.feed.acro.acrofeedapplication.adapter.PagerAdapter;
import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;
import com.feed.acro.acrofeedapplication.models.Post;
import com.feed.acro.acrofeedapplication.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hp on 18-03-2018.
 */

public class TimelineActivity extends AppCompatActivity {

    private int loginType = 0;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase, databaseReference, feedDataRefrence;
    ArrayList<String> mylist = new ArrayList<String>();
    ArrayList<String> myImageId = new ArrayList<String>();
    Post posts;
    ArrayList<ImageUploadInfo> imgFeed;
    ArrayList<Post> contentFeed = null;
    String Database_Path = "All_Content_Uploads_Database/UploadedFeed";
    ProgressDialog progressDialog;

    @BindView(R.id.noDataFound)
    TextView noDataFound;

    ArrayList<ImageUploadInfo> postList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        processIntent();
//        Database_Path = Database_Path + "/" +firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(Database_Path);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        contentFeed = new ArrayList<>();
        // Attach a listener to read the data at our posts reference
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
//                collectData((Map<String,Object>) dataSnapshot.getValue());
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                    List<String> values = new ArrayList<>(td.keySet());
                    mylist = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        mylist.add(values.get(i));
                    }
                    getFeed(mylist);

                }else {
                    progressDialog.dismiss();
                    noDataFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void setUpViewPager() {
        progressDialog.dismiss();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("CDC"));
        tabLayout.addTab(tabLayout.newTab().setText("Department"));
        tabLayout.addTab(tabLayout.newTab().setText("Administration"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), contentFeed);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getFeed(final ArrayList<String> userIdList) {

        if (userIdList != null) {
            for (final String userId : userIdList) {
                databaseReference = mDatabase.child(userId);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                            myImageId = new ArrayList<>(td.keySet());

                            if(myImageId !=null){
                                for (final String dataId_ : myImageId) {
                                    feedDataRefrence = mDatabase.child(userId).child(dataId_);
                                    feedDataRefrence.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            posts = new Post();
                                            if (dataSnapshot.getValue() != null) {
                                                ImageUploadInfo imageUploadInfo = dataSnapshot.getValue(ImageUploadInfo.class);
                                                posts.setUid(dataId_);
                                                posts.setImageUploadInfo(imageUploadInfo);
                                                contentFeed.add(posts);
                                                Log.d("UserId: ", userId);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                            System.out.println("The read failed: " + databaseError.getCode());
                                        }
                                    });

                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }

            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    if (contentFeed != null && contentFeed.size() > 0) {
                        setUpViewPager();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(TimelineActivity.this, "No data Found", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            handler.postDelayed(r, 4000);
        }
    }




    @OnClick(R.id.floatingActionButton)
    public void fabClicked() {
        Intent mIntent = new Intent(TimelineActivity.this, PostFeedActivity.class);
        mIntent.putExtra("Login_Type", loginType);
        startActivity(mIntent);
        finish();
    }

    private void processIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            if (mIntent.hasExtra("Login_Type")) {
                loginType = mIntent.getIntExtra("Login_Type", 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_profile:
                intent = new Intent(TimelineActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(TimelineActivity.this, AboutUsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                if (firebaseAuth.getCurrentUser() != null) {
                    firebaseAuth.signOut();
                    finish();
                    //starting login activity
                    startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
