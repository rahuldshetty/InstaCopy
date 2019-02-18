package com.rahuldshetty.instacopy.Frames;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahuldshetty.instacopy.EditActivity;
import com.rahuldshetty.instacopy.MainActivity;
import com.rahuldshetty.instacopy.R;
import com.rahuldshetty.instacopy.models.User;
import com.rahuldshetty.instacopy.utils.utility;

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
    GridLayout gridLayout;

    utility Utility;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


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
        gridLayout=mView.findViewById(R.id.gridLayout);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        Utility=new utility();

        //update button based on id
        if(MainActivity.profileName.equals(mAuth.getCurrentUser().getUid()))
        {
            mainBtn.setText("Edit Profile");
        }
        else{
            //TODO: get follow status and update


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
                    //TODO : FOLLOW OR UNFOLLOW

                }

            }
        });



        db.collection("USERS")
                .document(MainActivity.profileName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

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

                        // TODO : Getting each posts
                        // TODO : Getting followers count
                        // TODO : Getting following count







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
