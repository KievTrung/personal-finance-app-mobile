package com.example.personalfinance.datalayer.repositories;

import android.content.Context;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.UserService;
import com.example.personalfinance.fragment.transaction.models.UserModel;

import io.reactivex.rxjava3.core.Completable;

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

    public Completable addUser(UserModel userModel){
        //add from remote

        //then if success, add back to local, if not, display error
        return null;
    }

    public User toUser(UserModel userModel){
        User user = new User();
        user.setUserId(null);
        user.setUserName(userModel.getUserName());
        user.setPassword(userModel.getPassword());
        user.setEmail(userModel.getEmail());
        user.setCurrency(userModel.getCurrency());
        user.setLanguage(userModel.getLanguage());
        return user;
    }
}
