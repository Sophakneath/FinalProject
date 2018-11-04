package com.example.phakneath.contactapp.sharePreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.phakneath.contactapp.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserPreferences {

    private static final String PASSWORD = "PASSWORD";

    private static final String USER_PREFERENCE= "USER_PREFERECNCE";

    private static SharedPreferences getPreference(Context context)
    {
        return context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static void save(Context context, String password)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public static String getUser(Context context)
    {
        SharedPreferences preferences = getPreference(context);
        return  preferences.getString(PASSWORD,null);
    }

    public static void remove(Context context)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(PASSWORD,null);
        editor.commit();
    }
}
