package com.rahuldshetty.instacopy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.rahuldshetty.instacopy.utils.utility;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameField,nameField,passField,cpassField,emailField;
    TextView loginField;
    Button regBtn;

    utility Utils;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFirestoreSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();



        settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();


        db=FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        usernameField=findViewById(R.id.register_username);
        nameField=findViewById(R.id.register_fname);
        passField=findViewById(R.id.register_pass);
        cpassField=findViewById(R.id.register_cpass);
        emailField=findViewById(R.id.regiter_email);
        loginField=findViewById(R.id.register_login);
        regBtn=findViewById(R.id.register_regBtn);

        Utils=new utility();


        loginField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username=usernameField.getText().toString();
                final String name=nameField.getText().toString();
                String pass=passField.getText().toString();
                String cpass=cpassField.getText().toString();
                final String email=emailField.getText().toString();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(cpass) && !TextUtils.isEmpty(email))
                {

                    if(pass.equals(cpass))
                    {
                        if(pass.length()<6)
                            Utils.makeToast(RegisterActivity.this,"Password must be atleast length 6");
                        else if(!pass.matches(".*[a-z].*"))
                            Utils.makeToast(RegisterActivity.this,"Password is missing lowercase letter.");
                        else if(!pass.matches(".*[A-Z].*"))
                            Utils.makeToast(RegisterActivity.this,"Password is missing uppercase letter.");
                        else{
                            //success pass. now try to add to database
                            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {

                                        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

                                        // TODO : ADD TO FIRESTORE
                                        Map<String ,String> hashmap=new HashMap<>();
                                        hashmap.put("username",username);
                                        hashmap.put("name",name);
                                        hashmap.put("email",email);

                                        db.collection("USERS")
                                                .document("USERS")
                                                .collection(mCurrentUser.getUid())
                                                .add(hashmap)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        goToHome();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Utils.makeToast(RegisterActivity.this,"Failed to add to database....");
                                                    }
                                                })
                                        ;





                                    }
                                    else{
                                        Utils.makeToast(RegisterActivity.this,"Failed to register....");
                                    }

                                }
                            });
                        }
                    }
                    else
                        Utils.makeToast(RegisterActivity.this,"Password mismatch...");

                }
                else{
                    Utils.makeToast(RegisterActivity.this,"Enter all fields...");
                }


            }
        });

    }

    void goToHome(){
        Intent home=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(home);
        finish();
    }
}
