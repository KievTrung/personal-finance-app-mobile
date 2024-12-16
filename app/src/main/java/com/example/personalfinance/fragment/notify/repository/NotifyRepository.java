package com.example.personalfinance.fragment.notify.repository;

import android.content.Context;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.NotifyDao;
import com.example.personalfinance.datalayer.local.daos.auxiliry.DeletedRowDao;
import com.example.personalfinance.datalayer.local.entity.Notify;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotifyRepository {
    private final NotifyDao notifyDao;
    private final DeletedRowDao deletedRowDao;
    private final Context context;

    public NotifyDao getNotifyDao(){
        return notifyDao;
    }

    public NotifyRepository(Context context) {
        this.context = context;
        AppLocalDatabase app = AppLocalDatabase.getInstance(context);
        notifyDao = app.getNotifyDao();
        deletedRowDao = app.getDeletedRowDao();
    }


    public Single<List<Notify>> getAll(){
        return notifyDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insert(Notify notify) throws Exception {
        return AppLocalDatabase
                .executeTransaction(() -> notifyDao.insertNotify(notify))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable hasRead(Integer notify_id) {
        return notifyDao.hasRead(notify_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(Notify notify) throws Exception {
        return notifyDao.deleteNotify(notify.getNotify_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
