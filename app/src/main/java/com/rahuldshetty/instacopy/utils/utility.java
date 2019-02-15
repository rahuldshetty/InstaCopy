package com.rahuldshetty.instacopy.utils;

import android.content.Context;
import android.widget.Toast;

public class utility {

    public void makeToast(Context context,String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

}
