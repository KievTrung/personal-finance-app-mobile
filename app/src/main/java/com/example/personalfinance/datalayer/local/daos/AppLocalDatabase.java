package com.example.personalfinance.datalayer.local.daos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.entities.UseWallet;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.converters.LocalDateTimeConverter;

@Database(entities = {
        Transact.class,
        Wallet.class,
        Category.class,
        UseWallet.class,
        User.class
}, version = 1)
@TypeConverters({LocalDateTimeConverter.class})
public abstract class AppLocalDatabase extends RoomDatabase {
    public abstract TransactDao getTransactDao();
    public abstract WalletDao getWalletDao();
    public abstract UseWalletDao getUseWalletDao();
    public abstract CategoryDao getCategoryDao();
    public abstract UserDao getUserDao();

    private static volatile AppLocalDatabase INSTANCE;

    public static AppLocalDatabase getInstance(final Context context){
        if (INSTANCE == null){
            synchronized (AppLocalDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppLocalDatabase.class, "local db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void closeDb(){
        if (!INSTANCE.isOpen())
            INSTANCE.close();
    }
}
