package com.lightenartes.bot.ig.instagram;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/18/15.
 */
public class InstagramSession {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static final String SHARED = "instagram_preferences";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_PROFILE_PICTURE = "profile_picture";
    private static final String API_ACCESS_TOKEN = "access_token";
    public InstagramSession(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void storeAccessToken(String accessToken, String id, String username, String name, String profilePicture) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.putString(API_PROFILE_PICTURE, profilePicture);
        editor.commit();
    }
    public void storeAccessToken(String accessToken) {
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }
    /**
     * Reset access token and user name
     */
    public void resetAccessToken() {
        editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.commit();
    }
    /**
     * Get user name
     *
     * @return User name
     */
    public String getUsername() {
        return sharedPref.getString(API_USERNAME, null);
    }
    /**
     *
     * @return
     */
    public String getId() {
        return sharedPref.getString(API_ID, null);
    }
    /**
     *
     * @return
     */
    public String getName() {
        return sharedPref.getString(API_NAME, null);
    }
    /**
     * Get access token
     *
     * @return Access token
     */
    public String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }

    public String getProfilePicture() {
        return sharedPref.getString(API_PROFILE_PICTURE, null);
    }
}
