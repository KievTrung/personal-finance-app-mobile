package com.example.personalfinance.datalayer.local.repositories;

import android.content.Context;
import android.util.Log;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.enums.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRepository {
    private static final String TAG = "kiev";
    private final UserDao userDao;

    public UserRepository(Context context){
        userDao = AppLocalDatabase.getInstance(context).getUserDao();
    }

    public Single<User> getUser(){
        return userDao
                .getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    public Completable deleteUser(){
//        return userDao
//                .deleteUser()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable setUser(User user) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> {
                    userDao.deleteUser();
                    userDao.addUser(user);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateUser(User user){
        return userDao
                .updateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable setCurrency(Currency currency){
        return userDao.setCurrency(currency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static final BigDecimal OneUsdToVnd = BigDecimal.valueOf(25344d);

    public static Double toCurrency(Double amount, Currency currency){
        switch (currency){
            case vnd:
                return (BigDecimal.valueOf(amount).multiply(OneUsdToVnd).setScale(0, RoundingMode.HALF_DOWN)).doubleValue();
            case usd: default:
                return amount;
        }
    }

    public static Double backCurrency(Double amount, Currency currency){
        switch (currency){
            case vnd:
                return (BigDecimal.valueOf(amount).divide(OneUsdToVnd, 10, RoundingMode.HALF_DOWN)).doubleValue();
            case usd: default:
                return amount;
        }
    }

    public static String formatNumber(Double number, boolean isCurrencyOn, Currency currency){
        DecimalFormat formatter;
        switch (currency){
            case vnd:
                Log.d(TAG, "formatNumber: invoked");
                formatter = new DecimalFormat("###,###,###");
                break;
            case usd: default:
                formatter = new DecimalFormat("###,###,##0.00");
        }
        return formatter.format(number)  + ((isCurrencyOn) ? " " + currency : "");
    }

    public Single<Currency> getCurrency(){
        return userDao
                .getSingleCurrency()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
