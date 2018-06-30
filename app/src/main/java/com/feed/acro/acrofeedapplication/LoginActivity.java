package com.feed.acro.acrofeedapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.feed.acro.acrofeedapplication.models.LoginDetails;
import com.feed.acro.acrofeedapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hp on 20-03-2018.
 */

public class LoginActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.text_user_id)
    EditText userIdText;

    @BindView(R.id.admin_login)
    ImageView adminLogin;

    @BindView(R.id.text_password)
    EditText passwordText;

    @BindView(R.id.role_type_radio)
    RadioGroup logintype;


    @BindView(R.id.student_radio)
    RadioButton studentLogin;

    @BindView(R.id.faculty_radio)
    RadioButton facultyLogin;

    private DatabaseReference mDatabase;
    private int selectedId = 0;

    private static final String TAG = LoginActivity.class.getSimpleName();
    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private DatabaseReference mDatabaseUserId;
    String Database_Path = "All_Database";
    String loginTypeString = "Student";

    @OnClick(R.id.register_button)
    public void registerClicked(){
        Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


        //if the objects getcurrentuser method is not null
        //means user is already logged in

          if(firebaseAuth.getCurrentUser() != null){
              if(firebaseAuth.getCurrentUser().getEmail().equalsIgnoreCase("admin@acrofeed.com")) {
//              startActivity(new Intent(LoginActivity.this,AdminPage.class));
              } else {
                  Intent mIntent = new Intent(LoginActivity.this, TimelineActivity.class);
                  mIntent.putExtra("Login_Type", selectedId);
                  startActivity(mIntent);
                  finish();
              }
          }

        progressDialog = new ProgressDialog(this);

        logintype.setOnCheckedChangeListener(this);


        adminLogin.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getApplicationContext(),
                        "Admin Login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, AdminLogin.class));
                return true;
            }
        });
    }

    @OnClick(R.id.login_button)
    public void onLoginClicked(){

        String password = passwordText.getText().toString().trim();
        String userName = userIdText.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Logging in. Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(userName, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if the task is successfull
                        new Preferences(LoginActivity.this).saveData("Login_Type", selectedId+"");
                        if(task.isSuccessful()){
                            //start the profile activity
                            final FirebaseUser user = firebaseAuth.getCurrentUser();

                            mDatabaseUserId = FirebaseDatabase.getInstance().getReference(Database_Path).child(user.getUid());

                            mDatabaseUserId.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User mUser = dataSnapshot.getValue(User.class);
                                    if(mUser!=null) {
                                     if( mUser.getLoginType().equalsIgnoreCase(loginTypeString)){
                                         Intent mIntent =new Intent(LoginActivity.this, TimelineActivity.class);
                                         mIntent.putExtra("Login_Type", selectedId);
                                         startActivity(mIntent);
                                         finish();
                                     } else {
                                         firebaseAuth.signOut();
                                         Toast.makeText(LoginActivity.this, "Please select correct login type.",
                                                 Toast.LENGTH_SHORT).show();
                                     }
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                    System.out.println("The read failed: " + databaseError.getCode());
                                }


                            });

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Invalid Credentials", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(LoginActivity.this, AdminLogin.class));
                        }
                    }
                });

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
       if(i==R.id.faculty_radio){
           selectedId = 1;
           loginTypeString = "Faculty";
       }else {
           selectedId = 0;
           loginTypeString = "Student";

       }
    }
}


