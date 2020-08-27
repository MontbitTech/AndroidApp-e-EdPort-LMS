package com.example.lmsandroidapplication.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {

    //*************************THIS CLASS IS USED FOR STORING SHARED PREFERENCES ALL THE SHARED PREFERENCES RELATED CODE SHALL BE WRITTEN HERE TO AVOID REDUNDANCY **********************//

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context){

        sharedPreferences= context.getSharedPreferences(Constants.FILE_NAME,Constants.PRIVATE_MODE);

        editor= sharedPreferences.edit();
    }


    //************* FOR FIRST TIME LAUNCH *************////////////
    public void setFirstTimeLaunch(boolean isFirstTime){

        editor.putBoolean(Constants.IS_FIRST_LAUNCH,isFirstTime);
        editor.commit();

    }

    public boolean isFirstTimeLaunch(){
        return sharedPreferences.getBoolean(Constants.IS_FIRST_LAUNCH,true);

    }


    //************** FOR SCHOOL URL STORED IN THE USER'S DEVICE ***************/////
    public void setSchoolUrl(String url){
        editor.putString(Constants.SCHOOL_URL,url);
        editor.commit();
    }

    public String getSchoolUrl(){
        return sharedPreferences.getString(Constants.SCHOOL_URL,null);
    }

}
