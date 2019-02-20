package com.rahuldshetty.instacopy.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rahuldshetty.instacopy.EditActivity;
import com.rahuldshetty.instacopy.MainActivity;
import com.rahuldshetty.instacopy.R;
import com.rahuldshetty.instacopy.models.User;
import com.rahuldshetty.instacopy.utils.utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    public List<User> userList;

    public SearchAdapter(List<User> userList)
    {
        this.userList=userList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name,username;
        public CircleImageView circleImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.singleitemfullname);
            username=itemView.findViewById(R.id.singleitemusername);
            circleImageView=itemView.findViewById(R.id.singleitemimageview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            User userGlob=userList.get(getAdapterPosition());
            MainActivity.mainActivity.loadProfileFragment(userGlob.getUsername());

        }
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.singlesearchitem,viewGroup,false);



        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder myViewHolder, int i) {
        User user=userList.get(i);
        myViewHolder.username.setText(user.getUsername());
        myViewHolder.name.setText(user.getName());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar);
        Glide.with(MainActivity.mainContext).load(user.getPhotourl()).apply(options).into(myViewHolder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
