package com.rahuldshetty.instacopy;

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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rahuldshetty.instacopy.Frames.CameraFragment;
import com.rahuldshetty.instacopy.Frames.HomeFragment;
import com.rahuldshetty.instacopy.Frames.NotificationFragment;
import com.rahuldshetty.instacopy.Frames.ProfileFragment;
import com.rahuldshetty.instacopy.Frames.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    Toolbar toolbar;

    Fragment homeFragment,cameraFragment,profileFragment,searchFragment,notifFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();

        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        frameLayout=findViewById(R.id.home_frame);
        toolbar=findViewById(R.id.main_toolbar);

        homeFragment=new HomeFragment();
        cameraFragment=new CameraFragment();
        profileFragment=new ProfileFragment();
        searchFragment=new SearchFragment();
        notifFragment=new NotificationFragment();


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
                        loadFragment(homeFragment);
                        return true;
                    case R.id.bottomnav_search:
                        loadFragment(searchFragment);
                        return true;
                    case R.id.bottomnav_camera:
                        loadFragment(cameraFragment);
                        return true;
                    case R.id.bottomnav_notif:
                        loadFragment(notifFragment);
                        return true;
                    case R.id.bottomnav_profile:
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

    void loadFragment(Fragment fragment)
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
}
