package com.example.sebas.todoapp;

import com.example.sebas.todoapp.roomdb.User;
// Makes the userId available in the whole app, important to display only user-specific to-dos
public class ApplicationState {

    private static ApplicationState instance;

    public static ApplicationState getInstance () {
        if (ApplicationState.instance == null) {
            ApplicationState.instance = new ApplicationState();
        }
        return ApplicationState.instance;
    }


    private ApplicationState () {}


    private User user = null;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}