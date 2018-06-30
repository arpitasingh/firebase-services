package com.feed.acro.acrofeedapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feed.acro.acrofeedapplication.models.ImageUploadInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hp on 25-03-2018.
 */

public class PostFeedActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.editText_name)
    EditText nameEditText;

    @BindView(R.id.editText_year)
    EditText yeaEditText;

    @BindView(R.id.text_desc)
    EditText descriptionEditText;

    @BindView(R.id.postingToGrp)
    RadioGroup postingToGrp;

    @BindView(R.id.postTo)
    TextView postTo;

    @BindView(R.id.selectedImage)
    ImageView selectedImage;

    @BindView(R.id.pick_image)
    Button chooseFile;

    String Storage_Path = "All_Image_Uploads/";
    String Database_Path = "All_Content_Uploads_Database";
    int Image_Request_Code = 7;

    String department = "Student";
    // Creating URI.
    Uri FilePathUri;
    private String TAG = PostFeedActivity.class.getSimpleName();
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    int loginType = 0;

    @BindView(R.id.cdc_radio)
    RadioButton cdcRadio;

    boolean pickImageFlage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_feed_activity);

        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);


        postingToGrp.setOnCheckedChangeListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        processIntent();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


    }

    private void processIntent() {
        Intent mIntent = getIntent();
        if(mIntent!=null){
            if(mIntent.hasExtra("Login_Type")){
                loginType = mIntent.getIntExtra("Login_Type", 0);
            }
        }

        loginType = Integer.parseInt(new Preferences(this).getData("Login_Type")) ;

        if(loginType == 0){
            postingToGrp.setVisibility(View.GONE);
            postTo.setVisibility(View.GONE);
        }else {
            postingToGrp.setVisibility(View.VISIBLE);
            postTo.setVisibility(View.VISIBLE);
            cdcRadio.setChecked(true);
            department = "Faculty";
        }
    }

    @OnClick(R.id.pick_image)
    public void chooseImage() {
        pickImageFlage = true;
       isStoragePermissionGranted();
    }

    public  void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } /*else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            Intent intent = new Intent();
            intent.setType("image*//*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Image_Request_Code);
            return true;
        }*/

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Image_Request_Code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            if(pickImageFlage==true) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Image_Request_Code);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                selectedImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
//                chooseFile.setText("Image Selected");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            String description = descriptionEditText.getText().toString().trim();
                            String name = nameEditText.getText().toString().trim();
                            String year = yeaEditText.getText().toString().trim();
                            String postingTo = department.trim();
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            String status = "Pending";

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Data Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(userId, description,
                                    taskSnapshot.getDownloadUrl().toString(), year, name, postingTo, status);

                            // Getting image upload ID.
//                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
//                            databaseReference.child(userId).child("UploadedFeed").child(ImageUploadId).setValue(imageUploadInfo);

                            String key = databaseReference.child("UploadedFeed").push().getKey();
//                            Post post = new Post(userId, username, title, body);
                            Map<String, Object> postValues = imageUploadInfo.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
//                            childUpdates.put("/UploadedFeed/" + key, postValues);
                            childUpdates.put("/UploadedFeed/" + userId + "/" + key, postValues);

                            databaseReference.updateChildren(childUpdates);
                            startActivity(new Intent(PostFeedActivity.this, TimelineActivity.class));
                            finish();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(PostFeedActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Uploading...");

                        }
                    });
        }else {

            OutputStream outStream = null;
            Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.acropolis_logo);
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File file = new File(extStorageDirectory, "acropolis_logo.PNG");
            try {
                outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            FilePathUri = Uri.fromFile(file);
            UploadImageFileToFirebaseStorage();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(i==R.id.cdc_radio){
            department = "CDC";
        }else if(i== R.id.administartion_radio) {
            department = "Administration";
        }else {
            department = "Student";
        }
    }

    @OnClick(R.id.post_btn)
    public void submitClicked(){
        if(firebaseAuth.getCurrentUser() != null){
            if(TextUtils.isEmpty(nameEditText.getText())){
                Toast.makeText(PostFeedActivity.this, "Name is mandatory", Toast.LENGTH_SHORT).show();
                return;
            }else if(TextUtils.isEmpty(yeaEditText.getText())){
                Toast.makeText(PostFeedActivity.this, "Year is mandatory", Toast.LENGTH_SHORT).show();
                return;
            }else if(TextUtils.isEmpty(descriptionEditText.getText())){
                Toast.makeText(PostFeedActivity.this, "Description is mandatory", Toast.LENGTH_SHORT).show();
                return;
            }else {
                UploadImageFileToFirebaseStorage();
            }
        }else {
            Toast.makeText(PostFeedActivity.this, "Failed to verify user", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PostFeedActivity.this, TimelineActivity.class));
        finish();
        super.onBackPressed();
    }
}
