package com.feed.acro.acrofeedapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hp on 26-03-2018.
 */

public class AdminLogin extends AppCompatActivity {

    @BindView(R.id.id)
    EditText adminId;

    @BindView(R.id.password)
    EditText password;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @OnClick(R.id.admin_login)
    public void login(){
        hideSoftKeyboard(this);
        if(TextUtils.isEmpty(adminId.getText().toString().trim())){
            Toast.makeText(this, "Invalid id", Toast.LENGTH_SHORT).show();
            adminId.requestFocus();
            return;
        }else  if(TextUtils.isEmpty(password.getText().toString().trim())){
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }else  if(!adminId.getText().toString().trim().equalsIgnoreCase("admin@acrofeed.com")){
            Toast.makeText(this, "Invalid id", Toast.LENGTH_SHORT).show();
            adminId.requestFocus();
            return;
        }else  if(!password.getText().toString().trim().equalsIgnoreCase("acrofeed")){
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            //logging in the user
            firebaseAuth.signInWithEmailAndPassword(adminId.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                //start the profile activity
                                Toast.makeText(AdminLogin.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminLogin.this, AdminPage.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "Invalid Credentials", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);
        ButterKnife.bind(this);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
