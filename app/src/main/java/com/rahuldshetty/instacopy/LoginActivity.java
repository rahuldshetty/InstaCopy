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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rahuldshetty.instacopy.utils.utility;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    EditText emailfield,passfield;
    TextView reg;
    Button logBtn;


    private FirebaseAuth mAuth;
    utility Utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailfield=findViewById(R.id.login_email);
        passfield=findViewById(R.id.login_password);
        logBtn=findViewById(R.id.login_loginbtn);
        reg=findViewById(R.id.login_register);


        FirebaseApp.initializeApp(this.getApplicationContext());
        mAuth=FirebaseAuth.getInstance();
        Utils=new utility();

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailfield.getText().toString();
                String pass=passfield.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass))
                {
                    //checking validity of the password
                    if(pass.length()<6)
                        Utils.makeToast(LoginActivity.this,"Password must be atleast length 6");
                    else if(!pass.matches(".*[a-z].*"))
                        Utils.makeToast(LoginActivity.this,"Password is missing lowercase letter.");
                    else if(!pass.matches(".*[A-Z].*"))
                        Utils.makeToast(LoginActivity.this,"Password is missing uppercase letter.");
                    else{
                        //success pass. now check with database
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    goToHome();
                                }
                                else{
                                    Utils.makeToast(LoginActivity.this,"Failed to login....");
                                }

                            }
                        });
                    }

                }
                else{
                    Toast.makeText(LoginActivity.this,"Enter field values...",Toast.LENGTH_LONG).show();
                }


            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regAct=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(regAct);
                finish();
            }
        });


    }

    void goToHome(){
        Intent home=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(home);
        finish();
    }

}
