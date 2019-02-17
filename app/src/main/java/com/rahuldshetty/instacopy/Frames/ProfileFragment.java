package com.rahuldshetty.instacopy.Frames;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rahuldshetty.instacopy.MainActivity;
import com.rahuldshetty.instacopy.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private View mView;

    CircleImageView profImageView;
    TextView name,username,desc,postCount,folingCount,folwCount;
    Button mainBtn;


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


        




        return mView;

    }

}
