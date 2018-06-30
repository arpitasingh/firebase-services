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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feed.acro.acrofeedapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
 * Created by hp on 25-03-2018.
 */

public class ProfileActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.enrollment_number)
    EditText enrollmentNumber;

    @BindView(R.id.contact_number)
    EditText contactNumber;

    @BindView(R.id.editText_old_password)
    EditText oldPassword;

    @BindView(R.id.editText_new_password)
    EditText newPassword;

    @BindView(R.id.editText_confirm_password)
    EditText confirmPassword;

    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    String Database_Path = "All_Database";
    String userEmail = "", userPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        name.setEnabled(false);
        enrollmentNumber.setEnabled(false);
        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        final FirebaseUser user = firebaseAuth.getCurrentUser();
//        Database_Path = Database_Path + "/" + user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference(Database_Path).child(user.getUid());

        // Attach a listener to read the data at our posts reference
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User mUser = dataSnapshot.getValue(User.class);
                if (mUser != null) {
                    userEmail = mUser.getEmail();
                    userPassword = mUser.getPassword();
                    name.setText(mUser.getName());
                    enrollmentNumber.setText(mUser.getEnrollmentNumber());
                    contactNumber.setText(mUser.getContact());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

    }

    @OnClick(R.id.save)
    public void saveButtonClicked() {

        if (TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPassword)) {
            Toast.makeText(ProfileActivity.this, "Unexpected error", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (oldPassword.getText().toString().equalsIgnoreCase(userPassword)) {
                if (newPassword.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())) {


            final FirebaseUser user = firebaseAuth.getCurrentUser();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(userEmail, userPassword);

// Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Profile", "Password updated");
                                        } else {
                                            Log.d("Profile", "Error password not updated");
                                        }
                                    }
                                });
                            } else {
                                Log.d("Profile", "Error auth failed");
                            }
                        }
                    });

                } else {
                    Toast.makeText(ProfileActivity.this, "New password should match with confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}
