package com.lightenartes.bot.ig.db;

import com.orm.SugarRecord;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/25/15.
 */
public class Users extends SugarRecord<Users> {

    String username;
    String password;
    int following;
    int followers;
    boolean isFollow;

    public Users() {
    }

    public Users(String username, String password, boolean isFollow) {
        this.username = username;
        this.password = password;
        this.isFollow = isFollow;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
