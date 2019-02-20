package com.rahuldshetty.instacopy.Frames;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahuldshetty.instacopy.MainActivity;
import com.rahuldshetty.instacopy.PostActivity;
import com.rahuldshetty.instacopy.R;
import com.rahuldshetty.instacopy.utils.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    EditText title,desc;
    ImageView imageView;
    FloatingActionButton button;

    ProgressBar progressBar;


    private int RESULT_LOAD_IMAGE=5;

    utility Utility;

    private FirebaseFirestore db;
    private FirebaseUser mCurrrentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;

    private View mView;

    private Uri imageURI=null;


    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_camera, container, false);

        title=mView.findViewById(R.id.camera_title);
        desc=mView.findViewById(R.id.camera_desc);
        imageView=mView.findViewById(R.id.camera_imageView);
        button=mView.findViewById(R.id.floatingActionButton);
        progressBar=mView.findViewById(R.id.camera_progressBar);

        Utility=new utility();
        db=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        mAuth=FirebaseAuth.getInstance();
        mCurrrentUser=mAuth.getCurrentUser();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, RESULT_LOAD_IMAGE);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                final String titleText = title.getText().toString();
                final String descText = desc.getText().toString();

                if(!TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(descText))
                {
                    StorageReference storageReference=firebaseStorage.getReference();

                    final StorageReference ref= storageReference.child("posts").child(mCurrrentUser.getUid()).child(System.currentTimeMillis()+"");
                    ref.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Date timeStamp = (new Date());
                                    HashMap<String,Object> map=new HashMap<>();
                                    map.put("title",titleText);
                                    map.put("desc",descText);
                                    map.put("photourl",uri.toString());
                                    map.put("timestamp",timeStamp);

                                    db.collection("POSTS")
                                            .document(mCurrrentUser.getUid())
                                            .collection("SUBPOST")
                                            .add(map)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Utility.makeToast(MainActivity.mainContext,"Upload successful.");

                                                            title.setText("");
                                                            desc.setText("");
                                                            imageView.setImageURI(null);


                                                            Intent act=new Intent(MainActivity.mainContext, PostActivity.class);
                                                            startActivity(act);
                                                        }
                                                        else{
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Utility.makeToast(MainActivity.mainContext,"Failed to upload...");

                                                        }

                                                }
                                            });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Utility.makeToast(MainActivity.mainContext,"Failed to upload...");
                                }
                            });


                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Utility.makeToast(MainActivity.mainContext,"Failed to upload...");
                                }
                            });

                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Utility.makeToast(MainActivity.mainContext,"Enter all fields.");
                }
            }
        });



        return mView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE &&  resultCode == RESULT_OK)
        {
            imageURI = data.getData();
            imageView.setImageURI(imageURI);
        }

    }
}
