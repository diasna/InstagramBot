package com.lightenartes.bot.ig.db;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/18/15.
 */
public class Souls extends SugarRecord<Souls> {
    Long instagram = null;
    String username = "";
    String fullname = "";
    Date executedate = new Date();
    int status = 0;
    Users users;

    public Souls (){

    }

    public Souls(Long instagram, String username, String fullname, Users users) {
        this.instagram = instagram;
        this.username = username;
        this.fullname = fullname;
        this.users = users;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getInstagram() {
        return instagram;
    }

    public void setInstagram(Long instagram) {
        this.instagram = instagram;
    }

    public Date getExecutedate() {
        return executedate;
    }

    public void setExecutedate(Date executedate) {
        this.executedate = executedate;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}