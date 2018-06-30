package com.feed.acro.acrofeedapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hp on 24-04-2018.
 */

public class Preferences {

    Context context;

    Preferences(Context context) {
        this.context = context;
    }

    public void saveData(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getData(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }


}
