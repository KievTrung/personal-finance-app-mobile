package com.example.personalfinance.datalayer.local.daos;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.entities.Category;
import com.example.personalfinance.datalayer.local.entities.auxiliry.DeletedRow;
import com.example.personalfinance.datalayer.local.entities.Item;
import com.example.personalfinance.datalayer.local.entities.auxiliry.Token;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.entities.UseWallet;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.converters.LocalDateTimeConverter;

import io.reactivex.rxjava3.core.Completable;

@Database(entities = {
        Item.class,
        Transact.class,
        Wallet.class,
        Category.class,
        UseWallet.class,
        User.class,
        Token.class,
        DeletedRow.class
}, version = 1)
@TypeConverters({LocalDateTimeConverter.class})
public abstract class AppLocalDatabase extends RoomDatabase {
    private static final String TAG = "kiev";
    public abstract TransactDao getTransactDao();
    public abstract WalletDao getWalletDao();
    public abstract UseWalletDao getUseWalletDao();
    public abstract CategoryDao getCategoryDao();
    public abstract UserDao getUserDao();
    public abstract TokenDao getTokenDao();
    public abstract ItemDao getItemDao();
    public abstract DeletedRowDao getDeletedRowDao();

    private static volatile AppLocalDatabase INSTANCE;

    public static AppLocalDatabase getInstance(final Context context){
        if (INSTANCE == null){
            synchronized (AppLocalDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppLocalDatabase.class, "local db").build();
                }
            }
        }
        return INSTANCE;
    }

    public static Completable executeTransaction(Runnable runnable) throws Exception {
        if (INSTANCE.isOpen()){
            return Completable.fromAction(() -> INSTANCE.runInTransaction(runnable));
        }
        else{
            throw new Exception("instance is closed");
        }
    }

    public static void closeDb(){
        if (!INSTANCE.isOpen())
            INSTANCE.close();
    }
}
