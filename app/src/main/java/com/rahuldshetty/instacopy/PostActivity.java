package com.rahuldshetty.instacopy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.instacopy.models.Like;
import com.rahuldshetty.instacopy.models.Post;
import com.rahuldshetty.instacopy.utils.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    TextView postTite,postDesc,postTimestamp,likeCount;
    ImageView postImage,likeBtn;



    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String uid,docid;

    utility Utility;
    ProgressBar progressBar;

    boolean likeStatus=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postTite=findViewById(R.id.post_title);
        postDesc=findViewById(R.id.post_desc);
        postTimestamp=findViewById(R.id.post_timestamp);
        postImage=findViewById(R.id.post_imageView);
        progressBar=findViewById(R.id.post_progressBar);
        likeBtn=findViewById(R.id.postLikeButton);
        likeCount=findViewById(R.id.postLikeCount);


        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        Utility=new utility();

        uid = getIntent().getStringExtra("POST_UID");
        docid=getIntent().getStringExtra("POST_DocID");

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeStatus=!likeStatus;
                Like like=new Like(likeStatus);
                db.collection("LIKES")
                        .document(docid)
                        .collection("SUBLIKES")
                        .document(mAuth.getCurrentUser().getUid())
                        .set(like)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    updateLikeBtn(likeStatus);
                                }
                                else{
                                    Utility.makeToast(PostActivity.this, "Unable to like content...");
                                    likeStatus=!likeStatus;
                                }

                            }
                        });



            }
        });

        db.collection("POSTS")
                    .document(uid)
                    .collection("SUBPOST")
                    .document(docid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post post = documentSnapshot.toObject(Post.class);
                            if(post!=null) {
                                postTite.setText(post.getTitle());
                                postDesc.setText(post.getDesc());
                                postTimestamp.setText(post.getTimestamp().toString().substring(0, 16) + "  " + post.getTimestamp().toString().substring(30));

                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.avatar);

                                Glide.with(PostActivity.this).load(post.getPhotourl()).apply(options).into(postImage);

                                db.collection("LIKES")
                                        .document(docid)
                                        .collection("SUBLIKES")
                                        .whereEqualTo("likestatus",true)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                if(task.isSuccessful())
                                                {

                                                    QuerySnapshot queryDocumentSnapshots=task.getResult();

                                                    likeCount.setText(queryDocumentSnapshots.getDocuments().size()+"");

                                                    db.collection("LIKES")
                                                            .document(docid)
                                                            .collection("SUBLIKES")
                                                            .document(mAuth.getCurrentUser().getUid())
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        Like userLike = task.getResult().toObject(Like.class);
                                                                        if(userLike==null)
                                                                        {
                                                                            likeStatus=false;
                                                                        }
                                                                        else{
                                                                            likeStatus=userLike.isLikestatus();
                                                                        }
                                                                        updateLikeBtn(likeStatus);
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                    }
                                                                    else{
                                                                        Utility.makeToast(PostActivity.this, "Unable to get like status...");
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                    }
                                                                }
                                                            });


                                                }
                                                else{
                                                    Utility.makeToast(PostActivity.this, "Unable to get like status...");
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }

                                            }
                                        });




                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utility.makeToast(PostActivity.this, "Unable to load content...");
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


    }

    private void updateLikeBtn(boolean likeStatus) {
        if(likeStatus)
        {
            likeBtn.setImageDrawable(getDrawable(R.drawable.liked));
        }
        else{
            likeBtn.setImageDrawable(getDrawable(R.drawable.likesimple));
        }
        db.collection("LIKES")
                .document(docid)
                .collection("SUBLIKES")
                .whereEqualTo("likestatus",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            QuerySnapshot queryDocumentSnapshots = task.getResult();

                            likeCount.setText(queryDocumentSnapshots.getDocuments().size() + "");
                        }
                        else{
                            Utility.makeToast(PostActivity.this, "Unable to get like status...");

                        }
                    }
                });
    }
}
