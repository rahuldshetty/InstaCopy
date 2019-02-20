package com.rahuldshetty.instacopy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.instacopy.Frames.CameraFragment;
import com.rahuldshetty.instacopy.Frames.HomeFragment;
import com.rahuldshetty.instacopy.Frames.NotificationFragment;
import com.rahuldshetty.instacopy.Frames.ProfileFragment;
import com.rahuldshetty.instacopy.Frames.SearchFragment;
import com.rahuldshetty.instacopy.models.User;
import com.rahuldshetty.instacopy.utils.utility;

import java.util.ArrayList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    public static String profileName; //  UID of the person to open profile
    public static Context mainContext;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    Toolbar toolbar;

    public  static MainActivity mainActivity;

    private FirebaseFirestore db;


    ArrayList<Integer> queue;

    utility Utility;

    Fragment homeFragment,cameraFragment,profileFragment,searchFragment,notifFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        frameLayout=findViewById(R.id.home_frame);
        toolbar=findViewById(R.id.main_toolbar);

        Utility=new utility();
        homeFragment=new HomeFragment();
        cameraFragment=new CameraFragment();
        profileFragment=new ProfileFragment();
        searchFragment=new SearchFragment();
        notifFragment=new NotificationFragment();

        queue=new ArrayList<Integer>();
        queue.add(R.id.bottomnav_home);
        queue.add(R.id.bottomnav_home);

        mainActivity=this;


        mainContext=this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        bottomNavigationView.setSelectedItemId(R.id.bottomnav_home);
        loadFragment(homeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {

                    case R.id.bottomnav_home:
                        queue.add(R.id.bottomnav_home);
                        loadFragment(homeFragment);
                        return true;
                    case R.id.bottomnav_search:
                        queue.add(R.id.bottomnav_search);
                        loadFragment(searchFragment);
                        return true;
                    case R.id.bottomnav_camera:
                        queue.add(R.id.bottomnav_camera);
                        loadFragment(cameraFragment);
                        return true;
                    case R.id.bottomnav_notif:
                        queue.add(R.id.bottomnav_notif);
                        loadFragment(notifFragment);
                        return true;
                    case R.id.bottomnav_profile:
                        queue.add(R.id.bottomnav_profile);
                        profileName=mCurrentUser.getUid();
                        loadFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.toolbar_edit:
                Intent activity=new Intent(MainActivity.this,EditActivity.class);
                startActivity(activity);
                return true;

            case R.id.toolbar_logout:
                mAuth.signOut();
                finish();
                startActivity(getIntent());
                return false;

            default:return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbatitems,menu);
        return true;
    }

    public void loadFragment(Fragment fragment)
    {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.home_frame,fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }



    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth!=null) {
            mCurrentUser = mAuth.getCurrentUser();

            if (mCurrentUser == null) {
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //additional code
            if(queue.size()!=0)
            {

                queue.remove(queue.size()-1);

                if(queue.size()==0)
                {
                    finish();
                }
                else {
                    int top = queue.get(queue.size() - 1);
                    bottomNavigationView.setSelectedItemId(top);
                    queue.remove(queue.size() - 1);

                }

            }

        } else {
            getFragmentManager().popBackStack();
        }
    }


    public void loadProfileFragment(String username) {

        final String uname = username;


        db.collection("USERS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                        {

                            User user=documentSnapshot.toObject(User.class);
                            if(user.getUsername().equals(uname))
                            {

                                profileName=documentSnapshot.getId();
                                FragmentManager fragmentManager=getSupportFragmentManager();
                                FragmentTransaction transaction=fragmentManager.beginTransaction();
                                ProfileFragment newPF = new ProfileFragment();
                                transaction.replace(R.id.home_frame,newPF);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                return;
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utility.makeToast(MainActivity.this,"Failed to load data...");
                    }
                })
        ;


    }

}
