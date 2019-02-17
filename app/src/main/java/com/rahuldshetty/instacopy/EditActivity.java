package com.rahuldshetty.instacopy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahuldshetty.instacopy.models.User;
import com.rahuldshetty.instacopy.utils.utility;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditActivity extends AppCompatActivity {

    utility Utility;

    ProgressBar progressBar;

    EditText pass,username,name,desc;
    CircleImageView imageView;
    Button saveBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;

    FirebaseFirestore db ;

    private User userObject;
    private Uri imageURI=null;
    private  int RESULT_LOAD_IMAGE = 5;

    private boolean alreadyLoaded=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        load();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, RESULT_LOAD_IMAGE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                final String userName = username.getText().toString();
                String password=pass.getText().toString();
                final String Name = name.getText().toString();
                final String Desc = desc.getText().toString();

                if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(password))
                {
                    Utility.makeToast(EditActivity.this,"Empty name or password fields..");
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(userObject.getEmail(), password);


                if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(Name))
                {
                    // validate password
                    mCurrentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        final Map<String ,String> hashmap=new HashMap<>();
                                        hashmap.put("username",userName);
                                        hashmap.put("name",Name);
                                        hashmap.put("email",userObject.getEmail());
                                        hashmap.put("photourl","");
                                        hashmap.put("desc",Desc);



                                        if(imageURI!=null)
                                        {
                                            //upload the image
                                            StorageReference userImage = mStorageRef.child("profilepics").child(mCurrentUser.getUid());

                                            final StorageReference ref=userImage;

                                            ref.putFile(imageURI)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Uri downloadUrl = uri;
                                                                    String stringuri=downloadUrl.toString();
                                                                    hashmap.put("photourl",stringuri);



                                                                    db.collection("USERS")
                                                                            .document(mCurrentUser.getUid())
                                                                            .set(hashmap)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        Utility.makeToast(EditActivity.this,"Changes saved...");
                                                                                    }
                                                                                    else{
                                                                                        Utility.makeToast(EditActivity.this,"Error updating...");
                                                                                    }
                                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Utility.makeToast(EditActivity.this,"Error uploading...");
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                }
                                                            });

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Utility.makeToast(EditActivity.this,"Failed to upload image...");
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    });

                                        }
                                        else{
                                            db.collection("USERS")
                                                    .document(mCurrentUser.getUid())
                                                    .set(hashmap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                Utility.makeToast(EditActivity.this,"Changes saved...");
                                                            }
                                                            else{
                                                                Utility.makeToast(EditActivity.this,"Error updating...");
                                                            }
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    });

                                        }

                                    }
                                    else{
                                        Utility.makeToast(EditActivity.this,"Password mismatch or unable to connect to server...");
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                }
                else{
                    Utility.makeToast(EditActivity.this,"Enter all fields..");
                    progressBar.setVisibility(View.INVISIBLE);
                }



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE &&  resultCode == RESULT_OK)
        {
            imageURI = data.getData();
            imageView.setImageURI(imageURI);



        }

    }

    void load(){
        pass=findViewById(R.id.edit_pass);
        username=findViewById(R.id.edit_username);
        name=findViewById(R.id.edit_name);
        desc=findViewById(R.id.edit_desc);
        imageView=findViewById(R.id.edit_imageview);
        saveBtn=findViewById(R.id.edit_savebtn);
        progressBar=findViewById(R.id.edit_progress);
        mAuth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        Utility=new utility();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!alreadyLoaded) {
            alreadyLoaded=true;
            if (mAuth != null) {
                mCurrentUser = mAuth.getCurrentUser();
                db = FirebaseFirestore.getInstance();

                db.collection("USERS")
                        .document(mCurrentUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User userObj = documentSnapshot.toObject(User.class);
                                userObject = userObj;
                                username.setText(userObj.getUsername());
                                name.setText(userObj.getName());
                                desc.setText(userObj.getDesc());
                                if (userObj.getPhotourl() != "") {
                                    RequestOptions options = new RequestOptions()
                                            .centerCrop()
                                            .placeholder(R.drawable.avatar);

                                    Glide.with(EditActivity.this).load(userObj.getPhotourl()).apply(options).into(imageView);

                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utility.makeToast(EditActivity.this, "Failed to contact the server. Try Again..");
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                ;


            }

        }


    }
}
