package com.example.enthusiasticblobuploads;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;
/*
Currently not using..needed some changes
 */
public class NeumorphToast {
    public static void showToast(Activity activity, String str){
        View view = activity.getLayoutInflater().inflate(R.layout.layout_toast,null);
        Toast toast =  new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(str);
        toast.setView(view);
        toast.show();
    }
}
