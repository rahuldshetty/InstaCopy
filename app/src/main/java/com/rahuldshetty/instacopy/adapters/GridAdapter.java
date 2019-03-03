package com.rahuldshetty.instacopy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rahuldshetty.instacopy.MainActivity;
import com.rahuldshetty.instacopy.R;
import com.rahuldshetty.instacopy.models.Post;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    List<Post> postList;
    private Context ctx;
    private LayoutInflater layoutInflater;

    public GridAdapter(Context context, List<Post> customizedListView) {
        this.ctx = context;
        layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        postList = customizedListView;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.singlegriditem, parent, false);
            listViewHolder.imageView = (ImageView)convertView.findViewById(R.id.singleimageitem);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        Post post = postList.get(position);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar);
        Glide.with(MainActivity.mainContext).load(post.getPhotourl()).apply(options).into(listViewHolder.imageView);

        return convertView;
    }

    public class ViewHolder
    {
        ImageView imageView;
    }

}
