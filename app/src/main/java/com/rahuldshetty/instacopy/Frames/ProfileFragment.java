package com.rahuldshetty.instacopy.Frames;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.instacopy.EditActivity;
import com.rahuldshetty.instacopy.MainActivity;
import com.rahuldshetty.instacopy.PostActivity;
import com.rahuldshetty.instacopy.R;
import com.rahuldshetty.instacopy.adapters.GridAdapter;
import com.rahuldshetty.instacopy.models.Follow;
import com.rahuldshetty.instacopy.models.Post;
import com.rahuldshetty.instacopy.models.User;
import com.rahuldshetty.instacopy.utils.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private View mView;

    CircleImageView profImageView;
    TextView name,username,desc,postCount,folingCount,folwCount;
    Button mainBtn;
    ProgressBar progressBar;
    GridView gridLayout;


    GridAdapter adapter;
    utility Utility;

    private List<Post> postList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<DocumentReference> docList;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);


        //loading
        profImageView=mView.findViewById(R.id.profileImageView);
        name=mView.findViewById(R.id.profile_FullName);
        username=mView.findViewById(R.id.profile_UserName);
        desc=mView.findViewById(R.id.profile_Description);
        postCount=mView.findViewById(R.id.profile_postCount);
        folingCount=mView.findViewById(R.id.profileFollowingCount);
        folwCount=mView.findViewById(R.id.profileFollowersCount);
        mainBtn=mView.findViewById(R.id.profile_Btn);
        progressBar=mView.findViewById(R.id.profile_progressbar);
        gridLayout=mView.findViewById(R.id.gridView);

        docList=new ArrayList<DocumentReference>();

        postList=new ArrayList<Post>();

        adapter=new GridAdapter(this.getContext(),postList);
        gridLayout.setAdapter(adapter);

        gridLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO : TRANSFER TO POST
                Intent act=new Intent(MainActivity.mainContext, PostActivity.class);
                act.putExtra("POST_UID",MainActivity.profileName);
                act.putExtra("POST_DocID",docList.get(position).getId());
                startActivity(act);


            }
        });

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        Utility=new utility();

        //update button based on id
        if(MainActivity.profileName.equals(mAuth.getCurrentUser().getUid()))
        {
            mainBtn.setText("Edit Profile");
        }
        else{
            db.collection("FOLLOW")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(MainActivity.profileName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size()==0)
                            {
                                mainBtn.setText("Follow");

                            }
                            else {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String status = documentSnapshot.getString("status");
                                if (status.equals("NOT FOLLOWING")) {
                                    mainBtn.setText("Follow");
                                }
                                else {
                                    mainBtn.setText("UnFollow");

                                }

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utility.makeToast(MainActivity.mainContext,"Failed to load..");
                        }
                    })
            ;


        }
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.profileName.equals(mAuth.getCurrentUser().getUid()))
                {

                    Intent edit=new Intent(MainActivity.mainActivity,EditActivity.class);
                    startActivity(edit);
                }
                else{
                    Map<String,String> map=new HashMap<>();
                    if(mainBtn.getText().equals("Follow"))
                    {
                        map.put("status","FOLLOWING");
                        db.collection("FOLLOW")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection(MainActivity.profileName)
                                .document("FOLLOWCOLLECTION")
                                .set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            mainBtn.setText("UnFollow");
                                        }
                                        else{
                                            Utility.makeToast(MainActivity.mainContext,"Failed to send data...");
                                        }
                                    }
                                });

                    }
                    else{
                        map.put("status","NOT FOLLOWING");
                        db.collection("FOLLOW")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection(MainActivity.profileName)
                                .document("FOLLOWCOLLECTION")
                                .set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            mainBtn.setText("Follow");
                                        }
                                        else{
                                            Utility.makeToast(MainActivity.mainContext,"Failed to send data...");
                                        }
                                    }
                                });

                    }
                }

            }
        });



        db.collection("USERS")
                .document(MainActivity.profileName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {

                        User user=documentSnapshot.toObject(User.class);
                        if(user.getPhotourl()!="")
                        {
                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.avatar);

                            Glide.with(MainActivity.mainContext).load(user.getPhotourl()).apply(options).into(profImageView);
                        }
                        name.setText(user.getName());
                        username.setText("@"+user.getUsername());
                        desc.setText(user.getDesc());


                        // TODO : Getting followers count
                        // TODO : Getting following count

                        db.collection("POSTS")
                                .document(MainActivity.profileName)
                                .collection("SUBPOST")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            postList.clear();
                                            for(DocumentSnapshot docs:queryDocumentSnapshots.getDocuments())
                                            {
                                                docList.add(docs.getReference());
                                                Post post=docs.toObject(Post.class);
                                                postList.add(post);
                                            }
                                            adapter.notifyDataSetChanged();
                                            progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Utility.makeToast(MainActivity.mainContext,"Failed to load posts...");
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });






                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Utility.makeToast(MainActivity.mainContext,"Unable to load from server...");
                    }
                });





        return mView;

    }

}
