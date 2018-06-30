package com.feed.acro.acrofeedapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.feed.acro.acrofeedapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.editText_enroll)
    EditText enrollmentNumber;

    @BindView(R.id.editText_name)
    EditText name;

    @BindView(R.id.editText_dob)
    EditText dob;

    @BindView(R.id.editText_email)
    EditText email;

    @BindView(R.id.editText_password)
    EditText password;

    @BindView(R.id.editText_contact)
    EditText contact;

    @BindView(R.id.gender_radio)
    RadioGroup gender;

    @BindView(R.id.register_button)
    Button btnSave;
    String genderText = "Male";
    private ProgressDialog progressDialog;

    String Database_Path = "All_Database";
    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.postingToGrp)
    RadioGroup postingToGrp;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    String TAG = MainActivity.class.getSimpleName();
    public Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    String department = "Student";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        mFirebaseInstance = FirebaseDatabase.getInstance();
//        mFirebaseInstance.setPersistenceEnabled(true);
        mFirebaseDatabase = mFirebaseInstance.getReference(Database_Path);
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.male_radio){
                    genderText = "Male";
                }else {
                    genderText = "Female";
                }
            }
        });
        postingToGrp.setOnCheckedChangeListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


    }

    @OnClick(R.id.editText_dob)
    public void openDatePicker(){
        new DatePickerDialog(MainActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }
 
    private void registerUser(){

        final String enrollmentNumberText = enrollmentNumber.getText().toString().trim();

        final String nameText= name.getText().toString().trim();

        final String dobText= dob.getText().toString().trim();

        final String emailText= email.getText().toString().trim();
        final String passwordText= password.getText().toString().trim();

        final String contactText= contact.getText().toString();gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.male_radio){
                    genderText = "Male";
                }else {
                    genderText = "Female";
                }
            }
        });


 
        //checking if email and passwords are empty
        if(TextUtils.isEmpty(emailText)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }
 
        if(TextUtils.isEmpty(passwordText)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }
 
        //if the email and password are not empty
        //displaying a progress dialog

        final User user = new User(nameText, emailText, contactText, genderText,
                dobText, enrollmentNumberText,passwordText,department);
 
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
//                            Toast.makeText(MainActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();

                            mFirebaseDatabase.child(task.getResult().getUser().getUid()).setValue(user);

                            addUserChangeListener(task.getResult().getUser().getUid());


                        }else{
                            //display some message here
                            Toast.makeText(MainActivity.this,"Registration Error: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
 
    }

    private void addUserChangeListener(String userId) {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email);
                Toast.makeText(MainActivity.this,
                        R.string.registration_success,Toast.LENGTH_LONG).show();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Toast.makeText(MainActivity.this,
//                        R.string.registration_failure,Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });

        progressDialog.dismiss();
        finish();

    }


    @OnClick(R.id.register_button)
    public void onClick(View view) {
        //calling register method on click
        registerUser();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(i==R.id.cdc_radio){
            department = "Faculty";
        }else if(i== R.id.administartion_radio) {
            department = "Faculty";
        }else {
            department = "Student";
        }
    }
}