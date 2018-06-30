package com.feed.acro.acrofeedapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.feed.acro.acrofeedapplication.adapter.AdminViewsAdapter;
import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;
import com.feed.acro.acrofeedapplication.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hp on 26-03-2018.
 */

public class AdminPage extends AppCompatActivity {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    AdminViewsAdapter mAdapter;
    ArrayList<Post> contentFeed = null;
    Post posts ;
    ArrayList<ImageUploadInfo> imgFeed = new ArrayList<>();
    private DatabaseReference mDatabase, databaseReference, feedDataRefrence;
    FirebaseAuth firebaseAuth;
    String Database_Path = "All_Content_Uploads_Database/UploadedFeed";
    ArrayList<String> mylist = new ArrayList<String>();
    ProgressDialog progressDialog;
    ArrayList<String> myImageId = new ArrayList<String>();

    @BindView(R.id.noDataFound)
    TextView noDataFound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.feeds);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseAuth = FirebaseAuth.getInstance();
//        mAdapter = new AdminViewsAdapter(contentFeed, this);
//        mRecyclerView.setAdapter(mAdapter);

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
                    for (int i = 0; i < values.size(); i++) {
                        mylist.add(values.get(i));
                    }
                    getFeed(mylist);
                    /*final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            setAdapter();
                        }
                    };

                    handler.postDelayed(r, 3000);*/
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                System.out.println("The read failed: " + databaseError.getCode());
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
                        setAdapter();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AdminPage.this, "No data Found", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            handler.postDelayed(r, 4000);
        }
    }



  /*  private void getAllData(){

        if(myImageId != null && myImageId.size()>0){
            for(int i =0; i < myImageId.size(); i++){
                posts = new Post();
                posts.setUid(myImageId.get(i));
                feedDataRefrence = mDatabase.child(mylist.get(i));

                feedDataRefrence.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        imgFeed = new ArrayList<>();
                        if (dataSnapshot.getValue() != null) {
                            ImageUploadInfo imageUploadInfo = dataSnapshot.getValue(ImageUploadInfo.class);
//                        imgFeed.add(imageUploadInfo);
                            posts.setImageUploadInfo(imageUploadInfo);

//                            Log.e("imageUploadInfo: ", imageUploadInfo.getDescription());
                        }
                        contentFeed.add(posts);

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
*/
    private void setAdapter() {
        progressDialog.dismiss();
        ArrayList<Post> adminData = new ArrayList<>();
        if(contentFeed!=null){
            for(Post userContents : contentFeed){
                if(userContents.getImageUploadInfo().getStatus().equalsIgnoreCase("Pending")){
                    adminData.add(userContents);
                }
            }
        }
       if(adminData.size()>0){
           mAdapter = new AdminViewsAdapter(adminData, this);
           mRecyclerView.setAdapter(mAdapter);
       }else {
           mRecyclerView.setVisibility(View.GONE);
           noDataFound.setVisibility(View.VISIBLE);
       }

    }

    @Override
    public void onBackPressed() {
        firebaseAuth.signOut();
        super.onBackPressed();
    }

}
