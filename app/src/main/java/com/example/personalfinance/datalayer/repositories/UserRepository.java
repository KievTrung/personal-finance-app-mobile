package com.example.personalfinance.datalayer.repositories;

import android.content.Context;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.UserService;

public class UserRepository {
    private final UserDao userDao;
    private final UserService userService;

    public UserRepository(Context context){
        userDao = AppLocalDatabase.getInstance(context).getUserDao();
        userService = ApiServiceFactory.getUserService();
    }

    public Integer getUserId(){
        return null;
    }
}
